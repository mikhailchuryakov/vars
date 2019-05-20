import java.io._

import Patterns._
import cats.effect._

import scala.collection.mutable.ListBuffer

class Reader(val fileName: String, val calledBy: String = "") {

  val importList: ListBuffer[String] = new ListBuffer[String]
  val importFilesList: ListBuffer[Reader] = new ListBuffer[Reader]

  var variables: Map[String, String] = Map.empty
  var count = 0 // count of line for exceptions

  def dumpFile[F[_]](file: File)(implicit F: Sync[F]): F[Unit] = dumpResource(reader(file))

  private def reader[F[_]](file: File)(implicit F: Sync[F]): Resource[F, BufferedReader] =
    Resource.fromAutoCloseable(F.delay {
      new BufferedReader(new FileReader(file))
    })

  private def dumpResource[F[_]](res: Resource[F, BufferedReader])(implicit F: Sync[F]): F[Unit] = {
    def loop(in: BufferedReader): F[Unit] =
      F.suspend {
        val line = in.readLine()
        count += 1
        if (line != null) {
          parseLine(line)
          loop(in)
        } else {
          F.unit
        }
      }

    res.use(loop)
  }

  private def parseLine(line: String): Unit = {
    line match {
      // check the line for correctness
      case correctPat(name, value) => variables.get(name) match {
        case None => variables += (name -> value)
        case Some(_) => throw DuplicateVariable(fileName, count, name)
      }
      // check the line for refer to
      case referPat(name1, name2) => variables.get(name2) match {
        case Some(value) => variables += (name1 -> value)
        case None => findInImportFile(name1, name2)
      }
      // parse imports
      case lineImportPat(_, _) => parseImportsLine(line)
      // new line
      case "" =>
      // other variants
      case _ => throw IncorrectLine(fileName, count, line)
    }
  }

  private def parseImportsLine(line: String): Unit = {
    val imports = filePat.findAllIn(line)
    imports.toList.foreach(file => if (!importList.contains(file)) importList += file)
  }

  private def parseImportFiles(): Unit = {
    importList.foreach(file => {
      Reader.allImports.get(file) match {
        case Some(reader) =>
          if (!importFilesList.contains(reader)) importFilesList += reader // use existence
        case None =>
          val reader = new Reader(file, fileName)
          if (file == calledBy) throw CycleImport(calledBy, fileName) // it works only for two-cycle import
          importFilesList += reader
          Reader.allImports += (file -> reader)
          reader.dumpFile[IO](new File(getClass.getResource(s"$file.vars").getPath)).unsafeRunSync()
      }
    })
  }

  private def findInImportFile(name1: String, name2: String): Unit = {
    var isOneEntry = false
    var fileNameFirst = "" // need for exception message
    parseImportFiles()
    importFilesList.foreach(file => {
      file.variables.get(name2) match {
        case Some(value) =>
          if (isOneEntry) throw DuplicateVariableInImports(fileName, count, fileNameFirst, file.fileName, name2)
          else {
            fileNameFirst = file.fileName
            isOneEntry = !isOneEntry
          }
          variables += (name1 -> value)
        case None =>
      }
    })
    if (!isOneEntry) throw ValueOfVariableAbsentInImports(fileName, count, name1)
  }
}

object Reader {
  private var allImports: Map[String, Reader] = Map.empty
}
