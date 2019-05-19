val a = "import file10"

val correctPat = "^\\s*(?i)([a-z]\\w*)\\s*=\\s*(\\d+)\\s*$".r
val referPat = "^\\s*(?i)([a-z]\\w*)\\s*=\\s*([a-z]\\w*)\\s*$".r

val lineImportPat = "^\\s*import (file\\d+)(,\\s*file\\d+)*\\s*$".r
val alone = "(file\\d+)".r

alone.findAllIn(a).mkString("|||")

lineImportPat.findFirstIn(a)


a match {
  case lineImportPat(c, b) => println(s"${c}youp$b")
  case correctPat(name, value) => println(s"$name is $value")
  case referPat(name1, name2) => println(s"$name1 refet to $name2")
  case _ => println("invalid")
}




