package lc.fn.sketchy

import org.scalatra._
import scalate.ScalateSupport

class Sketchy extends SketchyUrlShortenerStack {

  get("/") {
    <html>
      <body>
        <h1>Hello, world!</h1>
        Say <a href="hello-scalate">hello to Scalate</a>.
      </body>
    </html>
  }
  
}
