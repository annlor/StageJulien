package restaure.rdf

import Config.Configuration

import scala.xml.XML

object mainRDF {

  def main(args:Array[String]):Unit={
    val classpath = new Configuration()
    val xml = XML.loadFile(classpath.pathXMLtoRDFSecondParser)

    val ClassModele = new ModelBuilder(xml)

    ClassModele.dumpModel(classpath.pathOutputRDFSecondParser)
  }
}