package restaure.rdf

import scala.xml.XML

object mainRDFDebrie {

  def main(args:Array[String]):Unit={
    println("Entrez le chemin du fichier XML :")
    /*
    /people/khamphousone/Documents/ParsersScala/XML/ParserDebrie/Version 2.0/ResultDebrie
    */
    var path=scala.io.StdIn.readLine()
    val xml = XML.loadFile(path)

    val ClassModele = new RDFDebrie(xml)

    println("Entrez l'emplacement du résultat RDF :")
    path=scala.io.StdIn.readLine()
    /*
    /people/khamphousone/IdeaProjects/DictionnairePicard/RDFDebrie/RDFDebrieResults.ttl
    */
    println("RDF Debrie DONE")
    ClassModele.dumpModel(path)
  }
}