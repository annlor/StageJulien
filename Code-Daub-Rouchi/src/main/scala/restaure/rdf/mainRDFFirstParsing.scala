package restaure.rdf

import java.io.File

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
      println("Entrez le chemin du dossier contenant le XML")
      /*
      /people/khamphousone/IdeaProjects/DictionnairePicard/XMLFirstParser/Enrichment/
      */
      val path = scala.io.StdIn.readLine()
      val files = getListOfFiles(s"$path")
      println("Entrez l'emplacement du résultat RDF :")
      val path2 = scala.io.StdIn.readLine()
      /*
      /people/khamphousone/IdeaProjects/DictionnairePicard/RDFFirstParser/
       */
      new File(path2 + "*").delete()
      for (file <- files.zipWithIndex) {

        val xml = XML.loadFile(file._1)

        val ClassModele = new RDFWriterFirstParsing(xml, file._2)


        ClassModele.dumpModel(path2 + s"${file._2}.ttl")


      }
      println("RDF First Parsing DONE")
    }
  }
