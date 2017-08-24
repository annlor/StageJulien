package enrichissementXML

import java.io.File
import java.nio.file.Paths

import commons.{UnitParser, toXML}
import fastparse.all._
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

    val str1 = strinput.split("([;,.](?=(?:[^()]*[()][^()]*[()])*[^()]*$))+")
  for (elements <- str1) yield {
      stripAll(elements, ",;:").trim
    }
  }


  def updateVersion( node : Node ) : Node = {
    def updateElements( seq : Seq[Node]) : Seq[Node] =
      for( subNode <- seq ) yield updateVersion( subNode )

    node match {
      case <Nomenclature>{ ch @ _* }</Nomenclature> => <Nomenclature>{ updateElements( ch ) }</Nomenclature>
      case <Entrée>{ ch @ _* }</Entrée> => <Entrée>{ updateElements( ch ) }</Entrée>
      case <Définition>{ ch @ _* }</Définition> => <Définition>{ updateElements( ch ) }</Définition>
      case <DéfinitionDétaillée>{ ch @ _* }</DéfinitionDétaillée> => <DéfinitionDétaillée>{ updateElements( ch ) }</DéfinitionDétaillée>
      case <DéfinitionFrançaise>{ch @ _*}</DéfinitionFrançaise> => <DéfinitionFrançaise>{ updateElements( ch ) }</DéfinitionFrançaise>
      case <df>{contents}</df> => <df><dfEntière>{contents}</dfEntière>{for (elements<-XMLenrichment(contents.text)) yield {<CandidatTraduction>{elements}</CandidatTraduction>}}</df>
      case other @ _ => other
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
      case <Nomenclature>{ ch @ _* }</Nomenclature> => <Nomenclature>{ updateElements( ch ) }</Nomenclature>
      case <Entrée>{ ch @ _* }</Entrée> => <Entrée>{ updateElements( ch ) }</Entrée>
      case <Définition>{ ch @ _* }</Définition> => <Définition>{ updateElements( ch ) }</Définition>
      case <DéfinitionDétaillée>{ ch @ _* }</DéfinitionDétaillée> => <DéfinitionDétaillée>{ updateElements( ch ) }</DéfinitionDétaillée>
      case <DéfinitionFrançaise>{ch @ _*}</DéfinitionFrançaise> => <DéfinitionFrançaise>{ updateElements( ch ) }</DéfinitionFrançaise>
      case <df>{ch @ _*}</df> => <df>{ updateElements( ch ) }</df>
      case <CandidatTraduction>{contents}</CandidatTraduction> =>
      val couple = containsallwords(s"$contents")
        if (couple._2 == false){
          <Trad><CandidatTraduction>{contents}</CandidatTraduction></Trad>
        }
        else {
          <Trad><CandidatTraduction>{contents}</CandidatTraduction><Traduction>{couple._1}</Traduction></Trad>
        }
        case other @ _ => other
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

    println("Entrez le chemin du dossier contenant le XML")
    /*
    /people/khamphousone/IdeaProjects/DictionnairePicard/XMLFirstParser/
    */
    val allWords = Source
      .fromFile("/people/khamphousone/Documents/Dictionnaires/results2.txt")
      .getLines.map(_.trim)
      .toSet
    val path = scala.io.StdIn.readLine()
    val files = getListOfFiles(s"$path")
    for (file <- files.zipWithIndex) {
      val xml = XML.loadFile(file._1)
      val classenrich = new enrichissementFirstParsing
      val nodexml = classenrich.createSynonym(classenrich.updateVersion(xml), allWords)
      XML.save(s"./XMLFirstParser/Enrichment/Enrichmentresults_${file._2}.xml", nodexml, "utf-8", true, null)

    }
    println("Enrichissement First Parsing DONE")
  }





}