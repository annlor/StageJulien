package enrichissementXML

import java.io.{File, FileWriter}
import java.nio.file.Paths

import commons.{UnitParser, toXML}
import fastparse.all._
import Config.Configuration

import scala.io.Source
import scala.xml.{Elem, Node, NodeSeq, XML}

class enrichissementFirstParsing {

  def stripAll(s: String, bad: String): String = {

    @scala.annotation.tailrec def start(n: Int): String =
      if (n == s.length) ""
      else if (bad.indexOf(s.charAt(n)) < 0) end(n, s.length)
      else start(1 + n)

    @scala.annotation.tailrec def end(a: Int, n: Int): String =
      if (n <= a) s.substring(a, n)
      else if (bad.indexOf(s.charAt(n - 1)) < 0) s.substring(a, n)
      else end(a, n - 1)

    start(0)
  }


  def XMLenrichment(strinput : String):Array[String] = {
    val str1 = strinput.split("([=;,.](?=(?:[^()]*[()][^()]*[()])*[^()]*$))+")
  for (elements <- str1) yield {
      stripAll(elements, ",;:").trim
    }
  }


  def updateVersion( node : Node ) : Node = {

    def updateElements( seq : Seq[Node]) : Seq[Node] =
      for( subNode <- seq ) yield updateVersion( subNode )
    node match {
      case Elem(pfx, "TEI", attrs, ns, ch @ _*) =>
        Elem(pfx, "TEI", attrs, ns, true, updateElements(ch):_*)
      case Elem(pfx, "entry", attrs, ns, ch @ _*) =>
        Elem(pfx, "entry", attrs, ns, true, updateElements(ch):_*)
      case Elem(pfx, "gramGrp", attrs, ns, ch @ _*) =>
        Elem(pfx, "gramGrp", attrs, ns, true, updateElements(ch):_*)

     /* case c @ <def>{ ch @ _* }</def>  if c.label == "def" && (c \ "@label").text.trim == "française" =>
        <def>{c.child}{for (elements<-XMLenrichment(c.child.text)) yield {<cit xml:lang="fr" type="translation">{elements}</cit>}}</def>
    */
      case Elem(pfx, "def", attrs, ns, ch @ _*) if attrs.get("label").exists(_.text == "française") =>

        Elem(pfx, "def",attrs, ns, true, ch ++ {for (elements<-XMLenrichment(ch.text)) yield {
          <cit type="translation">{elements}</cit>}}:_*)
      case Elem(pfx, "def", attrs, ns, ch @ _*) =>
        Elem(pfx, "def", attrs, ns, true, updateElements(ch):_*)
      case Elem(p, l, a, n, ch @ _*) =>
        Elem(p, l, a, n, true, ch : _*)
      case t: xml.Text => t
    }
  }


  def createSynonym (node : Node, set : Set[String]) : Node = {

    def updateElements( seq : Seq[Node]) : Seq[Node] =
      for( subNode <- seq ) yield createSynonym( subNode, set )

    def containsallwords(str: String) : (String,Boolean) = {
      if (set.contains(str) & str.length > 1){
        (str,true)
      }
      else{
        ("",false)
      }
    }




    node match {

      case Elem(pfx, "TEI", attrs, ns, ch @ _*) =>
        Elem(pfx, "TEI", attrs, ns, true, updateElements(ch):_*)
      case Elem(pfx, "entry", attrs, ns, ch @ _*) =>
        Elem(pfx, "entry", attrs, ns, true, updateElements(ch):_*)
      case Elem(pfx, "gramGrp", attrs, ns, ch @ _*) =>
        Elem(pfx, "gramGrp", attrs, ns, true, updateElements(ch):_*)
      case Elem(pfx, "def", attrs, ns, ch @ _*) =>
        Elem(pfx, "def", attrs, ns, true, updateElements(ch):_*)
      case Elem(pfx, "quote", attrs, ns, ch @ _*) =>
        Elem(pfx, "quote", attrs, ns, true, updateElements(ch):_*)
        /*
      case contents @ <cit>{ ch @ _* }</cit> if (contents \ "@type").text == "translation" =>

          val couple = containsallwords(s"${contents.text}")
          if (couple._2 == false) {
              <cit/>
          }
          else {

          }*/
      case Elem(pfx,"cit",attrs,ns,ch @ _*) if attrs.get("type").exists(_.text == "translation") =>
        val couple = containsallwords(s"${ch.text}")
        if (couple._2 == false) {
            Elem(pfx,"cit",attrs,ns,true)
        }
        else {
          val pic = <cit xml:lang="fr" type="translation">{couple._1}</cit>
          Elem(pfx,"cit",attrs,ns,true,pic)
        }
      case Elem(p, l, a, n, ch @ _*) =>
        Elem(p, l, a, n, true, ch : _*)
      case t: xml.Text => t
      }

    }
  }


object FirstParsing {

  def getListOfFiles(dir: String):List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isFile).toList
    } else {
      List[File]()
    }
  }

  def main(args: Array[String]): Unit = {
    val classpath = new Configuration()

    val allWords = Source
      .fromFile(classpath.pathGLAWI)
      .getLines.map(_.trim)
      .toSet

    val files = getListOfFiles(classpath.pathOutputFirstParsing)

    for (file <- files.zipWithIndex) {
      val xml = XML.loadFile(file._1)
      val classenrich = new enrichissementFirstParsing
      val nodexml = classenrich.createSynonym(classenrich.updateVersion(xml), allWords)
     /* XML.save(s"./XMLFirstParser/Enrichment/TEI/TEIEnrichmentresults_${file._2}.xml", nodexml, "utf-8", true, null)*/

      val printer = new scala.xml.PrettyPrinter(80,2)
      val str = printer.format(nodexml)
      val fw = new FileWriter(s"./XMLFirstParser/Enrichment/TEI/TEIEnrichmentresults_${file._2}.xml")
      fw.write(str)
      fw.close()

    }
    println("Enrichissement First Parsing DONE")
  }





}