import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}


class TestFoldMap extends FunSuite with BeforeAndAfter with ScalaFutures {

  case class Parent(id:Int, name:String, children:Seq[Child]=Seq())
  case class Child(id:Int, parentId:Int)


  import cats.instances.list._
  import cats.instances.tuple._
  import cats.instances.int._
  import cats.instances.map._
  import cats.syntax.foldable._

  test("Grouping by FoldMap") {


    val t = List( (1,"A"), (1,"B"), (2,"C"),(3,"D"), (3,"E") )
    val t2 = t.foldMap{case (a,b) => Map(a -> List(b))}

    println(t2) // Map(2 -> List(C), 1 -> List(A, B), 3 -> List(D, E))

    val target:List[(Parent, Child)] = List(
      (Parent(1, "P1"), Child(1,1)),
      (Parent(1, "P1"), Child(2,1)),
      (Parent(2, "P2"), Child(3,2)),
      (Parent(3, "P3"), Child(4,3)),
      (Parent(3, "P3"), Child(5,3))
    )

    val grouped = target.foldMap{case (p,c) => Map(p -> List(c))}// group by same parent
                    .toList.map{case (p, cs) => p.copy(children = cs)} // set parent's children
                    .sortBy(_.id) // sort
    println(grouped)
    assert(grouped(0).children.size == 2)
    assert(grouped(1).children.size == 1)
    assert(grouped(2).children.size == 2)

    assert(grouped(0).children(0).id == 1)
    assert(grouped(0).children(1).id == 2)
    assert(grouped(1).children(0).id == 3)
    assert(grouped(2).children(0).id == 4)
    assert(grouped(2).children(1).id == 5)

  }

}