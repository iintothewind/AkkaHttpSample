package ivar.test.stream

import akka.stream.scaladsl.Source
import akka.stream.testkit.scaladsl.TestSink
import ivar.test.AbstractTest

import scala.concurrent.Future

class StreamTest extends AbstractTest {

  trait Builder {


  }

  "Source" can "be created" in new Builder {
    val emptySource = Source.empty[String]
    emptySource.runForeach(println)
    val singleSource = Source.single("single element")
    singleSource.runForeach(println)
    val seqSource = Source(1 to 3)
    seqSource.runForeach(println)
    val futureSource = Source.fromFuture(Future("single value from a future"))
    futureSource.runForeach(println)
  }

  it can "also be tested" in {
    val sourceUnderTest = Source(1 to 4).filter(_ % 2 == 0).map(_ * 2)
    sourceUnderTest
      .runWith(TestSink.probe[Int])
      .request(2)
      .expectNext(4, 8)
      .expectComplete()
  }

  "Infinite Source" can "be created" in {
    Source.cycle(() => Iterator(1, 2, 3))
      .take(9)
      .runWith(TestSink.probe[Int])
      .request(2)
      .expectNext(1, 2)
  }
}
