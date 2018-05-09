package ivar.test

import java.security.cert.X509Certificate

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.{Http, HttpsConnectionContext}
import akka.stream.ActorMaterializer
import akka.testkit.{ImplicitSender, TestKit}
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.sslconfig.akka.AkkaSSLConfig
import javax.net.ssl.{KeyManager, SSLContext, X509TrustManager}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEachTestData, FlatSpecLike, Matchers}
import org.slf4s.Logging

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContextExecutor}

abstract class AbstractTest(_system: ActorSystem)
  extends TestKit(_system)
    with ImplicitSender
    with Matchers
    with FlatSpecLike
    with BeforeAndAfterEachTestData
    with BeforeAndAfterAll
    with Logging {
  implicit val cfg: Config = ConfigFactory.load
  implicit val executor: ExecutionContextExecutor = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  private val routeBuffer: ListBuffer[Route] = ListBuffer.empty[Route]

  sealed case class RouteRegister() {
    def update(route: => Route): Unit = {
      routeBuffer += pathPrefix("api")(route)
    }
  }

  implicit val routeBuf: RouteRegister = RouteRegister()

  def routes: List[Route] = routeBuffer.result()

  def this() = this(ActorSystem("test", ConfigFactory.load))

  private val looseSslContext: SSLContext = {
    object NoCheckX509TrustManager extends X509TrustManager {
      override def checkClientTrusted(chain: Array[X509Certificate], authType: String): Unit = ()

      override def checkServerTrusted(chain: Array[X509Certificate], authType: String): Unit = ()

      override def getAcceptedIssuers: Array[X509Certificate] = Array[X509Certificate]()
    }

    val context = SSLContext.getInstance("TLS")
    context.init(Array[KeyManager](), Array(NoCheckX509TrustManager), null)
    context
  }

  private val ctx: HttpsConnectionContext = Http().createClientHttpsContext(
    AkkaSSLConfig()
      .mapSettings(s =>
        s.withLoose(
          s.loose
            .withAcceptAnyCertificate(true)
            .withAllowWeakCiphers(true)
            .withDisableSNI(true)
            .withDisableHostnameVerification(true))))

  private val httpsConnectionContext = new HttpsConnectionContext(
    sslContext = looseSslContext,
    sslConfig = ctx.sslConfig,
    enabledCipherSuites = ctx.enabledCipherSuites,
    enabledProtocols = ctx.enabledProtocols,
    clientAuth = ctx.clientAuth,
    sslParameters = ctx.sslParameters
  )
  Http().setDefaultClientHttpsContext(httpsConnectionContext)

  override def afterAll(): Unit = {
    Await.result(system.terminate(), 10.seconds)
  }
}