import java.io.File

import cats.effect.IO


class Main {}

object Main {
  def main(args: Array[String]): Unit = {
    val file = new File(getClass.getResource(args.head).getPath)
    val reader = new Reader(file.getName.split("\\.")(0))
    reader.dumpFile[IO](file).unsafeRunSync()
    reader.variables.foreach(pair => println(s"${pair._1} = ${pair._2}"))
  }
}
