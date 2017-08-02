package restaure.rdf

import java.io.FileWriter

import commons.XMLUnitParser
import org.apache.jena.rdf.model._

import scala.xml.{Elem, NodeSeq}
import fastparse.all._
import org.apache.jena.util.URIref
import restaure.rdf.Voc._

/**
  * URIref
  * Label
  */

/**
  * Created by khamphousone on 7/12/17.
  */

class ModelBuilder(xml : Elem){
  val XMLUP = new XMLUnitParser
  val model:Model = ModelFactory.createDefaultModel()

  val articles:NodeSeq = xml \ "ArticleDeDictionnaire"
  val UF = new UsefulFunction()
  articles.zipWithIndex.foreach({case (a,i) => UF.RDFWriter(model,a,i)})



    def dumpModel(path: String): Unit = {
      val fw = new FileWriter(path)
      model.setNsPrefix("restaure", uri)
      model.setNsPrefix("ontolex", Voc.ontolex)
      model.setNsPrefix("lexinfo", Voc.lexinfo)
      model.setNsPrefix("vartrans", Voc.vartrans)
      model.setNsPrefix("lime", Voc.LInguisticMEtadata)
      model.write(fw, "Turtle")
      fw.close()
    }
  }


class UsefulFunction {


  def RDFWriter (m: Model, article: NodeSeq, id:Int): Unit = {


    val leURI = uri + s"le_$id"
    val lexicon = m.createResource(Voc.LInguisticMEtadata + "Lexion")
    val lexEntry = m.createResource (leURI)
    lexicon.addProperty(Voc.Entry,lexEntry)
    LexicalEntryWriter (lexEntry, article, leURI, m)
    StructGramWriter(article,lexEntry,m)
    LexieEntiereWriter(article,m, lexEntry)
  }

  def LexicalEntryWriter (lexEntry:Resource, article: NodeSeq, leURI: String, m: Model): Unit = {

    val Entry = article \ "Entrée"
    val fEntry = m.createResource (URIref.encode(uri + s"lf_${Entry.text}"))
    lexEntry.addProperty (Voc.lexicalForm, fEntry)

    val Complement = article \ "Complément"
    fEntry.addProperty(Voc.writtenRep,s"${Entry.text} ${Complement.text}")
  }

  def StructGramWriter(article : NodeSeq, lexEntry : Resource, m: Model): Unit ={
    val XMLUP = new XMLUnitParser

    val StructureGrammaticale = article \ "StructureGrammaticale"

    val resStruct = XMLUP.XMLStructureGrammaticale.parse(StructureGrammaticale.text) match {
      case Parsed.Success(seq,_) => seq
      case _:Parsed.Failure => Nil
    }


    for (struct <- resStruct) {

      struct match {
        case "v." =>
          lexEntry.addProperty(Voc.liPartOfSpeech, liVerb)

        case "s." =>
          lexEntry.addProperty(Voc.liPartOfSpeech, liNoun)

        case "adj." =>
          lexEntry.addProperty(Voc.liPartOfSpeech,liAdj)

        case "f." =>
          lexEntry.addProperty(Voc.liGender, liFemale)

        case "m." =>
          lexEntry.addProperty(Voc.liGender, liMasculine)

        case "prép." =>
          lexEntry.addProperty(Voc.liPartOfSpeech, liPreposition)

        case _ =>
      }
    }
  }

  def LexieEntiereWriter(article: NodeSeq, model:Model, lexEntry:Resource):Unit={
    val LexieEntière = article \ "LexieEntiere"
    val fLexieEntière = model.createResource(URIref.encode(uri + s"${LexieEntière.text}"))
    lexEntry.addProperty(Voc.TranslatableAsForm, fLexieEntière)
    fLexieEntière.addProperty(Voc.writtenRep,LexieEntière.text)
  }





}