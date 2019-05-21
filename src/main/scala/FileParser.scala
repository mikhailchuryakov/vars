import java.io.File

import common.Patterns._
import cats.effect.IO
import common.{DuplicateVariable, DuplicateVariableInImports, IncorrectLine, ValueOfVariableAbsentInImports}

object FileParser {

  def parseLine(line: String, reader: Reader): Unit = {
    line match {
      // check the line for correctness
      case correctPat(name, value) => reader.variables.get(name) match {
        case None => reader.variables += (name -> value)
        case Some(_) => throw DuplicateVariable(reader.fileName, reader.count, name)
      }
      // check the line for refer to
      case referPat(name1, name2) => reader.variables.get(name2) match {
        case Some(value) => reader.variables += (name1 -> value);
        case None => findInImportFile(name1, name2, reader)
      }
      // parse imports
      case lineImportPat(_, _) => parseImportsLine(line, reader)
      // new line
      case "" =>
      // other
      case _ => throw IncorrectLine(reader.fileName, reader.count, line)
    }
  }

  private def parseImportsLine(line: String, reader: Reader): Unit = {
    val imports = filePat.findAllIn(line).drop(1) // drop "import"
    imports.toList.foreach(file => if (!reader.importList.contains(file)) reader.importList += file)
  }

  private def parseImportFiles(reader: Reader): Unit = {
    reader.importList.foreach(fileName => {
      Reader.allImports.get(fileName) match {
        case Some(value) =>
          if (!reader.importFilesList.contains(value)) reader.importFilesList += value // use existence
        case None =>
          val r = new Reader(fileName)
          reader.importFilesList += r
          Reader.allImports += (fileName -> r)
          r.dumpFile[IO](new File(getClass.getResource(s"$fileName.vars").getPath)).unsafeRunSync()
      }
    })
  }

  private def findInImportFile(name1: String, name2: String, reader: Reader): Unit = {
    var isOneEntry = false
    var fileNameFirst = "" // need for exception message
    parseImportFiles(reader)
    reader.importFilesList.foreach(file => {
      file.variables.get(name2) match {
        case Some(value) =>
          if (isOneEntry) throw DuplicateVariableInImports(reader.fileName,
            reader.count, fileNameFirst, file.fileName, name2)
          else {
            fileNameFirst = file.fileName
            isOneEntry = !isOneEntry
          }
          reader.variables += (name1 -> value)
        case None =>
      }
    })
    if (!isOneEntry) throw ValueOfVariableAbsentInImports(reader.fileName, reader.count, name1)
  }
}
