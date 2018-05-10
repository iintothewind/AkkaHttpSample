package ivar.test

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEachTestData, FlatSpecLike, Matchers}
import org.slf4s.Logging

import scala.concurrent.Await
import scala.concurrent.duration._

abstract class AbstractTest(_system: ActorSystem)
  extends TestKit(_system)
    with ImplicitSender
    with Matchers
    with FlatSpecLike
    with BeforeAndAfterEachTestData
    with BeforeAndAfterAll
    with Logging {

  override def afterAll(): Unit = {
    Await.result(system.terminate(), 10.seconds)
  }
}