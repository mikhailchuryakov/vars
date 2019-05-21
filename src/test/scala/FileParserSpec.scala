import java.io.File

import cats.effect.IO
import common.{DuplicateVariable, DuplicateVariableInImports, ValueOfVariableAbsentInImports}
import org.scalatest.FlatSpec

class FileParserSpec extends  FlatSpec{

  def createReader(fileName: String): IO[Unit] = {
    val file = new File(getClass.getResource(fileName).getPath)
    val reader = new Reader(file.getName.split("\\.")(0))
    reader.dumpFile[IO](file)
  }

  "variables of file3" should "be equals 10 40 10"  in {
    val trueMap = Map("a3" -> "10", "b3" -> "40", "foo3" -> "10")
    val file3 = new File(getClass.getResource("file3.vars").getPath)
    val reader = new Reader(file3.getName.split("\\.")(0))
    reader.dumpFile[IO](file3).unsafeRunSync()
    assert(reader.variables == trueMap)
  }

  it should "throw DuplicateVariables exception" in {
    assertThrows[DuplicateVariable] {
      createReader("duplicateVariables.vars").unsafeRunSync()
    }
  }

  it should "throw DuplicateVariablesInImports exception" in {
    assertThrows[DuplicateVariableInImports] {
      createReader("duplicateVariablesInImport1.vars").unsafeRunSync()
    }
  }

  "different localization of variables with same names" should "be correct" in {
    val trueMap = Map("ndv1" -> "10", "ndv2" -> "11")
    val file3 = new File(getClass.getResource("nonDuplicateV.vars").getPath)
    val reader = new Reader(file3.getName.split("\\.")(0))
    reader.dumpFile[IO](file3).unsafeRunSync()
    assert(reader.variables == trueMap)
  }


  "cycle dependencies" should "thrown exception" in {
    assertThrows[ValueOfVariableAbsentInImports] {
      createReader("cycleImport1.vars").unsafeRunSync()
    }
  }
}
