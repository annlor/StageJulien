package restaure.rdf

import Config.Configuration

import scala.xml.XML

object mainRDFDebrie {

  def main(args:Array[String]):Unit={
    val classpath = new Configuration()
    val xml = XML.loadFile(classpath.pathOutputXMLDebrie2)

    val ClassModele = new RDFDebrie(xml)



    println("RDF Debrie DONE")
    ClassModele.dumpModel(classpath.pathOutputRDFReneDebrie)
  }
}