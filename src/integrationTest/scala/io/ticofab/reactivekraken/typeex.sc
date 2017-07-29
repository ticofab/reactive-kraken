abstract class SuperClass(root: List[String])

trait Common {
  val root: List[String]
}

case class SubClass1(open: List[String]) extends Common {
  override val root = open
}

case class SubClass2(closed: List[String]) extends Common {
  override val root = closed
}

def extractRoot[T <: Common](anyClass: T) = {
  anyClass.root
}

val a: Option[String] = Some("fabio")
val b: Option[String] = Some("yo")

val ss = Option("fabio")

val res = for {
  name1 <- a
  name2 <- b
} yield (name1, name2)

res.get._1