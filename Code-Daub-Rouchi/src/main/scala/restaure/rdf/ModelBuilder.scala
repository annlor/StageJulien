package restaure.rdf;

import java.io.FileWriter

import commons.XMLUnitParser
import org.apache.jena.rdf.model._

import scala.xml.{Elem, XML}
import fastparse.all._
import restaure.rdf.Voc.{uri}


/**
  * Created by khamphousone on 7/12/17.
  */

class ModelBuilder(xml : Elem){

  val XMLUP = new XMLUnitParser

  val model = ModelFactory.createDefaultModel()
  var id = 1

  val articles = xml \ "ArticleDeDictionnaire"
  for (article <- articles){
    val leURI = uri + s"le_$id"
    id += 1
    val lexEntry = model.createResource(leURI)
    val Entry = article \ "Entrée"
    var FinalEntry = Entry.text.replace(' ','_')
    if ((FinalEntry takeRight 1) == "_") {
      FinalEntry = FinalEntry.dropRight(1)
    }
    val fEntry = model.createResource(uri + s"le_$FinalEntry")
    lexEntry.addProperty(Voc.lexicalForm, fEntry)

    val StructureGrammaticale = article \ "StructureGrammaticale"

    val resStruct = XMLUP.XMLStructureGrammaticale.parse(StructureGrammaticale.text) match {
      case Parsed.Success(seq,_) => seq
      case f:Parsed.Failure => Nil
    }
    for (struct <- resStruct) {
      struct match {
        case "v." =>
          val fStruct = model.createResource(uri + "le_verbe")
          lexEntry.addProperty(Voc.lipartofspeech, fStruct)
          lexEntry.addProperty(Voc.liverb, fStruct)

        case "s." =>
          val fStruct = model.createResource(uri + "le_substantif")
          lexEntry.addProperty(Voc.lipartofspeech, fStruct)
          lexEntry.addProperty(Voc.linoun, fStruct)

        case _ =>
      }
    }

  }

  def dumpModel(path: String) = {
    val fw = new FileWriter(path)
    model.setNsPrefix("restaure", uri)
    model.setNsPrefix("ontolex", Voc.ontolex)
    model.setNsPrefix("lexinfo", Voc.lexinfo)
    model.write(fw, "Turtle")
    fw.close()
  }
}

