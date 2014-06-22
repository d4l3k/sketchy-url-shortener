package lc.fn.sketchy

import org.scalatra._
import scalate.ScalateSupport
import scala.collection.JavaConversions._
import scala.io.Source
import scala.util.Random
import com.github.tototoshi.base62.Base62
import com.netaporter.uri.dsl._
import com.netaporter.uri.Uri.parse
import com.redis._

class Sketchy extends SketchyUrlShortenerStack with ScalateSupport {
  val r = new RedisClient("localhost", 6379)
  val base62 = new Base62
  val files = new java.io.File("words").listFiles
  val rand = new Random
  var words:Map[String,List[String]] = Map()
  var endings = List("mkv", "mp4", "dll", "exe", "so", "out", "kmod", "msi", "msp", "jar", "bat", "cmd", "py", "sh", "vbs", "pdf", "rb")
  for(file <- files) {
    val name = file.getName
    var tmp_words:List[String] = List()
    for(line <- Source.fromFile(file).getLines) {
      if(line.length > 0 && line.head != '#') {
        tmp_words = line.split(",").map(x => x.trim).toList ::: tmp_words
      }
    }
    words += (name -> tmp_words)
  }
  get("/:url") {
    val url = params("url")
    var to = r.get("sketchy:url:"+url)
    val uri = parse(to.get)
    if(uri.host == None){
      to = Some("http://"+to.get)
    }
    to match {
      case Some(s) => redirect(s)
      case None => halt(404, <h1>Url Not Found</h1>)
    }
  }
  get("/") {
    contentType = "text/html"
    ssp("/WEB-INF/views/index.jade", "lists" -> words)
  }
  get("/short") {
    contentType = "text/html"
    ssp("/WEB-INF/views/index.jade", "lists" -> words)
  }
  post("/new") {
    contentType = "text/html"
    val url = params("url")
    val hidden = params("hidden")
    val lists = request.getParameterValues("lists").toList
    var word_count = params("words").toInt
    if(hidden.length > 0){
      halt(403, "NOPE")
    }
    var short = ""
    params.get("base62") match {
      case Some(s) => {
        val num = r.incr("sketchy:latestid")
        short = base62.encode(num.last)
      }
      case None => {
        short = getUrl(lists, word_count)
      }
    }
    r.set("sketchy:url:"+short, url)
    ssp("/WEB_INF/views/new.jade", "url" -> url, "short" -> ("http://fn.lc/"+short))
  }
  get("/test") {
    getUrl(words.keys.toList, 8)
  }
  def randWord(lists: List[String]): String = {
    var tmp_words:List[String] = List()
    for(list <- lists){
      tmp_words = words(list) ::: tmp_words
    }
    tmp_words(rand.nextInt(tmp_words.length))
  }
  def messCase(word: String): String = {
    rand.nextInt(3) match {
      case 0 => word.toLowerCase
      case 1 => word.toLowerCase.split(" ").map(_.capitalize).mkString(" ")
      case 2 => word
    }
  }
  def getUrl(lists: List[String], count: Int): String = {
    var parts:List[String] = List()
    for(v <- 0 to rand.nextInt(count) ) {
      parts = messCase(randWord(lists)).replaceAll(" ", "_") :: parts
    }
    val num = r.incr("sketchy:latestid")
    parts = insertAt(base62.encode(num.last), rand.nextInt(parts.length + 1), parts)
    var url = parts.mkString("_")
    url += "."+endings(rand.nextInt(endings.length))
    //url = url.replaceAll(" ", "_")//.toLowerCase
    //url = url.flatMap { case ' ' => "_-.".charAt(rand.nextInt(3)).toString case c => c.toString }
    url
  }
  def insertAt[A](e: A, n: Int, ls: List[A]): List[A] = ls.splitAt(n) match {
    case (pre, post) => pre ::: e :: post
  }
}
