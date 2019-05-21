import java.io._

import cats.effect._

import scala.collection.mutable.ListBuffer

class Reader(val fileName: String) {

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
          FileParser.parseLine(line, this)
          loop(in)
        } else {
          F.unit
        }
      }

    res.use(loop)
  }
}

object Reader {
  var allImports: Map[String, Reader] = Map.empty
}
