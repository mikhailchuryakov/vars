import java.io.File

import cats.effect.{ExitCode, IO}


trait Main {

}

object Main {
  def main(args: Array[String]): Unit = {
    val file = new File("C:\\Users\\User\\Desktop\\vars\\src\\main\\resources\\file1.vars") // todo change path
    val reader = new Reader(file.getName)
    reader.dumpFile[IO](file).unsafeRunSync()
    println(reader.variables)
    println(reader.importList)
    println(reader.importsMapList.head)
  }
}
