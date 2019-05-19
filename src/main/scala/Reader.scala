import java.io._

import cats.effect._

object Reader {
  private def reader[F[_]](file: File)(implicit F: Sync[F]): Resource[F, BufferedReader] =
    Resource.fromAutoCloseable(F.delay {
      new BufferedReader(new FileReader(file))
    })

  private def dumpResource[F[_]](res: Resource[F, BufferedReader])(implicit F: Sync[F]): F[Unit] = {
    val correctPat = "^\\s*(?i)([a-z]\\w*)\\s*=\\s*(\\d+)\\s*$".r
    val referPat = "^\\s*(?i)([a-z]\\w*)\\s*=\\s*([a-z]\\w*)\\s*$".r
    var count = 0

    def loop(in: BufferedReader): F[Unit] =
      F.suspend {
        val line = in.readLine(); count += 1
        if (line != null) {
          line match {
            case correctPat(name, value) => println(s"$name is $value")
            case referPat(name1, name2) => println(s"$name1 refer to $name2")
            case _ => println("invalid")
          }

          loop(in)
        } else {
          F.unit
        }
      }
    res.use(loop)
  }

  def dumpFile[F[_]](file: File)(implicit F: Sync[F]): F[Unit] =
    dumpResource(reader(file))
}
