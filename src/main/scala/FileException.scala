abstract class FileException(message: String) extends Exception(message, null, false, false)

final case class DuplicateVariable(fileName: String, lineNumber: Int, varName: String) extends
  FileException(s"file: $fileName; line: $lineNumber; duplicate variable: \'$varName\'")

final case class DuplicateVariableInImports(fileName: String, lineNumber: Int,
                                            fileName1: String, fileName2: String, varName: String) extends
  FileException(s"file: $fileName; line: $lineNumber; duplicate variable: \'$varName\' " +
    s"in imports \'$fileName1\', \'$fileName2\'")

final case class ValueOfVariableAbsentInImports(fileName: String, lineNumber: Int, varName: String) extends
  FileException(s"file: $fileName; line: $lineNumber; value of variable \'$varName\' absent")

final case class IncorrectLine(fileName: String, lineNumber: Int, line: String) extends
  FileException(s"file: $fileName; line: $lineNumber; incorrect line: \'$line\'")

final case class RoundImports(fileName1: String, fileName2: String) extends
  FileException(s"Round imports between files: \'$fileName1\' and \'$fileName2\'")