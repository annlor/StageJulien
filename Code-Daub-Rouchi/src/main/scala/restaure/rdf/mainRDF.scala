package restaure.rdf

import scala.xml.XML

object mainRDF {

  def main(args:Array[String]):Unit={
    println("Entrez le chemin du fichier XML :")
    /*
    /people/khamphousone/IdeaProjects/DictionnairePicard/XMLSecondParser/all.xml
    */
    var path=scala.io.StdIn.readLine()
    val xml = XML.loadFile(path)

    val ClassModele = new ModelBuilder(xml)

    println("Entrez l'emplacement du résultat RDF :")
    path=scala.io.StdIn.readLine()
    /*
    /people/khamphousone/IdeaProjects/DictionnairePicard/RDFSecondParser/RDFresult.ttl
    */
    ClassModele.dumpModel(path)
  }
}