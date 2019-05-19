import java.io.File

import cats.effect.{ExitCode, IO}


trait Main {

}

object Main {
  def main(args: Array[String]): Unit = {
    Reader.dumpFile[IO](new File("C:\\Users\\User\\Desktop\\vars\\src\\main\\resources\\file1.vars")).unsafeRunSync()
  }
}
