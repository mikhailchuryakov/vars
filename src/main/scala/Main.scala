import java.io.File

import cats.effect.IO


class Main {

}

object Main {
  def main(args: Array[String]): Unit = {
    //val file = new File("C:\\Users\\User\\Desktop\\vars\\src\\main\\resources\\file1.vars") // todo change path
    val file = new File(getClass.getResource(s"${args.head}.vars").getPath)
    val reader = new Reader(file.getName.split("\\.")(0))
    reader.dumpFile[IO](file).unsafeRunSync()
    println(reader.variables)

  }
}
