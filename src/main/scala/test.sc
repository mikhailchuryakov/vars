val a = "qwe = qwe"

val correctPat = "^\\s*(?i)([a-z]\\w*)\\s*=\\s*(\\d+)\\s*$".r
val referPat = "^\\s*(?i)([a-z]\\w*)\\s*=\\s*([a-z]\\w*)\\s*$".r
val regex = correctPat.findFirstIn(a)

a match {
  case correctPat(name, value) => println(s"$name is $value")
  case referPat(name1, name2) => println(s"$name1 refet to $name2")
  case _ => println("invalid")
}