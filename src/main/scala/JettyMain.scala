import org.eclipse.jetty.server.nio.SelectChannelConnector
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener

object JettyMain {

  def run = {
    val server = new Server
    val connector = new SelectChannelConnector
    connector.setPort(8080)
    server.addConnector(connector)

    val context = new WebAppContext
    context.setContextPath("/")

    val resourceBase = getClass.getClassLoader.getResource("webapp").toExternalForm
    context.setResourceBase(resourceBase)
    context.setEventListeners(Array(new ScalatraListener))
    server.setHandler(context)

    server.start
    server.join
  }
}
