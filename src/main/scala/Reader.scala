import java.io._

import cats.effect._

import Patterns._

import scala.collection.mutable.ListBuffer

class Reader(fileName: String) {
  var variables: Map[String, String] = Map.empty
  val importList: ListBuffer[String] = new ListBuffer[String]

  private def reader[F[_]](file: File)(implicit F: Sync[F]): Resource[F, BufferedReader] =
    Resource.fromAutoCloseable(F.delay {
      new BufferedReader(new FileReader(file))
    })

  private def dumpResource[F[_]](res: Resource[F, BufferedReader])(implicit F: Sync[F]): F[Unit] = {
    var count = 0
    def loop(in: BufferedReader): F[Unit] =
      F.suspend {
        val line = in.readLine()
        count += 1
        if (line != null) {
          line match {
            case correctPat(name, value) => variables.get(name) match {
              case None => variables += (name -> value)
              case Some(_) => throw DuplicateVariable(fileName, count, name)
            }
            case referPat(name1, name2) => variables.get(name2) match {
              case Some(value) => variables += (name1 -> value)
              case None => println(s"Vse ploho $name1 refer to $name2") // todo find in import files
            }
            case lineImportPat(_, _) => parseImports(line)
            case "" =>
            case _ => throw IncorrectLine(fileName, count, line)
          }

          loop(in)
        } else {
          F.unit
        }
      }

    res.use(loop)
  }

  def dumpFile[F[_]](file: File)(implicit F: Sync[F]): F[Unit] = {
    dumpResource(reader(file))
  }

  def parseImports(line: String): Unit = {
    val imports = filePat.findAllIn(line)
    while (imports.hasNext) {
      val file = imports.next()
      importList += file
    }
  }
}
