abstract class FileException(message: String) extends Exception(message, null, false, false)

final case class DuplicateVariable(fileName: String, lineNumber: Int, varName: String) extends
  FileException(s"file: $fileName; line: $lineNumber; duplicate variable: \'$varName\'")

final case class IncorrectLine(fileName: String, lineNumber: Int, line: String) extends
  FileException(s"file: $fileName; line: $lineNumber; inccorrect line: \'$line\'")
