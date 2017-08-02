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

class RDFWriterFirstParsing(xml : Elem, indice : Int){
  val XMLUP = new XMLUnitParser
  val model:Model = ModelFactory.createDefaultModel()

  val articles:NodeSeq = xml \\ "Entrée"
  val UF = new UsefulFunctionFirstParsing
  articles.zipWithIndex.foreach({case (a,i) => UF.RDFWriter(model,a,i,indice)})



  def dumpModel(path : String): Unit = {
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


class UsefulFunctionFirstParsing  {


  def RDFWriter (m: Model, article: NodeSeq, id:Int, indice : Int): Unit = {


    val leURI = uri + s"le${indice}PicardVersFrançais_$id"
    val lexicon = m.createResource(Voc.LInguisticMEtadata + "Lexion")
    val lexEntry = m.createResource (leURI)
    lexicon.addProperty(Voc.Entry,lexEntry)
    LexicalEntryWriter (lexEntry, article, leURI, m)
    StructGramWriter(article,lexEntry,m)
    LexieEntiereWriter(article,m, lexEntry)
  }

  def LexicalEntryWriter (lexEntry:Resource, article: NodeSeq, leURI: String, m: Model): Unit = {

    val Entry = article \ "Vocable"
    val fEntry = m.createResource (URIref.encode(uri + s"lf_${Entry.text}"))
    lexEntry.addProperty (Voc.lexicalForm, fEntry)
    fEntry.addProperty(Voc.writtenRep,s"${Entry.text}")

  }

  def StructGramWriter(article : NodeSeq, lexEntry : Resource, m: Model): Unit ={
    val XMLUP = new XMLUnitParser

    val CatégorieGrammaticale = article \ "CatégorieGrammaticale"

    val resStruct = XMLUP.XMLStructureGrammaticale.parse(CatégorieGrammaticale.text) match {
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
    val Définition = article \\ "Définition"
    val LexieEntière = Définition \ "DéfinitionEntiere"
    val fLexieEntière = model.createResource(URIref.encode(uri + s"${LexieEntière.text}"))
    lexEntry.addProperty(Voc.TranslatableAsDef, fLexieEntière)
    fLexieEntière.addProperty(Voc.writtenRep,LexieEntière.text)

    val DefinitionFrançaise = Définition \ "DéfinitionDétaillée" \ "DéfinitionFrançaise"  \ "df" \ "dfEntière"
    for (elem <- DefinitionFrançaise){

      val RessourceDefinitionFrançaise =
        model.createResource(URIref.encode(uri + s"${elem.text}"))
      lexEntry.addProperty(Voc.TranslatableInFrench, RessourceDefinitionFrançaise)
      RessourceDefinitionFrançaise.addProperty(Voc.writtenRep,elem.text)
    }


    val Exemples = Définition \\ "Exemples" \ "ExemplePicard"
    for (elem <- Exemples) {
      val RessourceExemplePicard =
        model.createResource(URIref.encode(uri + s"${elem.text}"))
      lexEntry.addProperty(Voc.ExampleInPicard, RessourceExemplePicard)
      RessourceExemplePicard.addProperty(Voc.writtenRep, elem.text)
    }

    val Ancien = Définition \ "DéfinitionDétaillée" \ "Ancien"
    val RessourceAncien = model.createResource(URIref.encode(uri + s"${Ancien.text}"))
    lexEntry.addProperty(Voc.oldInformation, RessourceAncien)
    RessourceAncien.addProperty(writtenRep,Ancien.text)
    }





}