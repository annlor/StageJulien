package restaure.rdf

import java.io.File

import Config.Configuration

import scala.xml.XML

object mainRDFFirstParsing {
  def getListOfFiles(dir: String):List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isFile).toList
    } else {
      List[File]()
    }
  }

    def main(args:Array[String]):Unit={
      val classpath = new Configuration
      val files = getListOfFiles(classpath.pathInputXMLFirstParsingEnrichment)
      new File(classpath.pathOutputRDFFrstParsing + "*").delete()
      for (file <- files.zipWithIndex) {

        val xml = XML.loadFile(file._1)

        val ClassModele = new RDFWriterFirstParsing(xml, file._2)


        ClassModele.dumpModel(classpath.pathOutputRDFFrstParsing + s"${file._2}.ttl")


      }
      println("RDF First Parsing DONE")
    }
  }
