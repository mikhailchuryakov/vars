import java.io._

import Patterns._
import cats.effect._

import scala.collection.mutable.ListBuffer

class Reader(val fileName: String, val calledBy: String = "") {

  var variables: Map[String, String] = Map.empty
  val importList: ListBuffer[String] = new ListBuffer[String]
  val importFilesList: ListBuffer[Reader] = new ListBuffer[Reader]
  var count = 0 // count number of line for exceptions

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
          line match {
            // check the line for correctness
            case correctPat(name, value) => variables.get(name) match {
              case None => variables += (name -> value)
              case Some(_) => throw DuplicateVariable(fileName, count, name)
            }
            // check the line for refer to
            case referPat(name1, name2) => variables.get(name2) match {
              case Some(value) => variables += (name1 -> value)
              case None => findInImportFile(name1, name2) // println(s"Vse ploho $name1 refer to $name2") // todo find in import files
            }
            // parse imports
            case lineImportPat(_, _) => parseImports(line)
            // new line
            case "" =>
            // other variants
            case _ => throw IncorrectLine(fileName, count, line)
          }

          loop(in)
        } else {
          F.unit
        }
      }

    res.use(loop)
  }

  def dumpFile[F[_]](file: File)(implicit F: Sync[F]): F[Unit] = dumpResource(reader(file))


  def parseImports(line: String): Unit = {
    val imports = filePat.findAllIn(line)
    while (imports.hasNext) { // remove while
      val file = imports.next()
      val reader = new Reader(file, fileName)
      if (file == calledBy) throw RoundImports(calledBy, fileName)
      reader.dumpFile[IO](new File(s"C:\\Users\\User\\Desktop\\vars\\src\\main\\resources\\$file.vars")).unsafeRunSync()
      importFilesList += reader
      importList += file
    }
  }

  def checkRoundImports(): Unit = {
    println("cheking")
    importFilesList.foreach(impFile => {
      impFile.importFilesList.foreach(impImpFile => {
        if (impImpFile.fileName == impFile.fileName) throw RoundImports(impFile.fileName, impImpFile.fileName)
      })
    })
  }

  def findInImportFile(name1: String, name2: String): Unit = {
    var isOneEntry = false
    var fileNameFirst = ""
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
