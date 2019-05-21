package common

import scala.util.matching.Regex

object Patterns {
  val correctPat: Regex = "^\\s*(?i)([a-z]\\w*)\\s*=\\s*(\\d+)\\s*$".r
  val referPat: Regex = "^\\s*(?i)([a-z]\\w*)\\s*=\\s*([a-z]\\w*)\\s*$".r
  val lineImportPat: Regex = "^\\s*import (\\w+)(\\s*,\\s*\\w+)*\\s*$".r
  val filePat: Regex = "(\\w+)".r
}
