package restaure.rdf

import java.io.FileWriter

import commons.XMLUnitParser
import org.apache.jena.rdf.model._

import scala.xml.{Elem, NodeSeq}
import fastparse.all._
import org.apache.jena.util.URIref
import restaure.rdf.Voc._
import enrichissementXML.enrichissementFirstParsing
import org.apache.jena.vocabulary.RDF

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
  val lexicon:Resource = model.createResource(uri + "LexiqueDaubyRouchiFra")
  lexicon.addProperty(lexiconNameWR,model.createLiteral("Le Livre du \"Rouchi\" Parler Picard de Valenciennes","fra"))
  lexicon.addProperty(lexiconAuthorWR,model.createLiteral("Jean Dauby","fra"))
  lexicon.addProperty(lexiconDirectionWR,model.createLiteral("français vers picard","fra"))
  val lexiconcoordinate:Resource = model.createResource(uri + "coordinate")
  lexicon.addProperty(Voc.coordinate, lexiconcoordinate)
  val xcoordinateValenciennes:Property = model.createProperty(uri + "xValenciennes")
  val ycoordinateValenciennes:Property = model.createProperty(uri + "yValenciennes")
  xcoordinateValenciennes.addProperty(Voc.writtenRep,model.createLiteral("Valenciennes","fra"))
  ycoordinateValenciennes.addProperty(Voc.writtenRep,model.createLiteral("Valenciennes","fra"))
  lexiconcoordinate.addProperty(xcoordinateValenciennes,model.createTypedLiteral(new java.lang.Double(50.35)))
  lexiconcoordinate.addProperty(ycoordinateValenciennes,model.createTypedLiteral(new java.lang.Double(3.53333)))
  val articles:NodeSeq = xml \ "ArticleDeDictionnaire"
  val UF = new UsefulFunction()
  articles.zipWithIndex.foreach({case (a,i) => UF.RDFWriter(model,a,i,lexicon)})



    def dumpModel(path: String): Unit = {
      val fw = new FileWriter(path)
      model.setNsPrefix("restaure", uri)
      model.setNsPrefix("ontolex", Voc.ontolex.getURI)
      model.setNsPrefix("lexinfo", Voc.lexinfo.getURI)
      model.setNsPrefix("vartrans", Voc.vartrans.getURI)
      model.setNsPrefix("lime", Voc.LInguisticMEtadata.getURI)
      model.write(fw, "Turtle")
      fw.close()
    }
  }


class UsefulFunction {


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

  def RDFWriter (m: Model, article: NodeSeq, id:Int, lexicon:Resource): Unit = {


    val leURI = uri + s"le_$id"
    val lexEntry = m.createResource (leURI)
    lexEntry.addProperty(RDF.`type`,LexicalEntry)
    lexicon.addProperty(Voc.Entry,lexEntry)
    LexicalEntryWriter (lexEntry, article, leURI, m)
    StructGramWriter(article,lexEntry,m)
    LexieEntiereWriter(article,m, lexEntry)
  }

  def LexicalEntryWriter (lexEntry:Resource, article: NodeSeq, leURI: String, m: Model): Unit = {

    val Entry = article \ "Entrée"
    val fEntry = m.createResource (URIref.encode(uri + s"lf_${Entry.text}"))
    fEntry.addProperty(RDF.`type`, Form)
    lexEntry.addProperty (Voc.lexicalForm, fEntry)


    val Complement = article \ "Complément"
    var Complementtxt = Complement.text
    if (Complementtxt.length > 0){
      Complementtxt = " " + Complementtxt
    }
    fEntry.addProperty(Voc.writtenRep,s"${Entry.text.toLowerCase}${Complementtxt.toLowerCase}")
  }

  def StructGramWriter(article : NodeSeq, lexEntry : Resource, m: Model): Unit ={
    val XMLUP = new XMLUnitParser

    val liFemale:Resource = m.createResource(lexinfo + "Female")
    liFemale.addProperty(Voc.writtenRep,m.createLiteral("Féminin","fra"))
    val liMasculine:Resource = m.createResource(lexinfo + "Masculine")

    liMasculine.addProperty(Voc.writtenRep,m.createLiteral("Masculin","fra"))
    val liVerb:Resource = m.createResource(lexinfo + "Verb")
    liVerb.addProperty(Voc.writtenRep, m.createLiteral("Verbe","fra"))
    val liNoun:Resource = m.createResource(lexinfo + "Noun")
    liNoun.addProperty(Voc.writtenRep,m.createLiteral("Nom","fra"))
    val liAdj:Resource = m.createResource(lexinfo + "Adjective")
    liAdj.addProperty(Voc.writtenRep,m.createLiteral("Adjectif","fra"))
    val liPreposition:Resource = m.createResource(lexinfo + "Preposition")
    liPreposition.addProperty(Voc.writtenRep,m.createLiteral("Préposition","fra"))

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

  def LexieEntiereWriter(article: NodeSeq, model:Model, lexEntry:Resource):Unit= {
    val LexieEntière = article \ "LexieEntiere"
    val fLexieEntière = model.createResource(URIref.encode(uri + s"${LexieEntière.text}"))
    lexEntry.addProperty(Voc.TranslatableAsForm, fLexieEntière)
    fLexieEntière.addProperty(RDF.`type`,Form)
    fLexieEntière.addProperty(Voc.writtenRep, LexieEntière.text)

    val SousLexie = article \ "Lexie" \ "SousLexie"
    if (SousLexie.nonEmpty) {
      val RessourceCandidatTraduction = new Array[Resource](SousLexie.indices.max + 1)

      for (indice <- SousLexie.indices) {

        val str = stripAll(s"${SousLexie(indice).text.trim}", " .")
        if (str.nonEmpty) {
          RessourceCandidatTraduction(indice) =
            model.createResource(URIref.encode(uri + str))
          lexEntry.addProperty(Voc.TranslatableInPicardOneWord, RessourceCandidatTraduction(indice))
          RessourceCandidatTraduction(indice).addProperty(RDF.`type`, Form)
          RessourceCandidatTraduction(indice).addProperty(Voc.writtenRep, str)
        }
      }
      if (RessourceCandidatTraduction.length > 1) {
        for (indice1 <- RessourceCandidatTraduction.indices) {
          for (indice2 <- RessourceCandidatTraduction.indices.filter(_ > indice1)) {
            println(indice1 + " " + indice2 +" "+ RessourceCandidatTraduction.length + "\n")
            if (RessourceCandidatTraduction(indice1) != null & RessourceCandidatTraduction(indice2) != null) {
              RessourceCandidatTraduction(indice1).addProperty(Voc.synonym, RessourceCandidatTraduction(indice2))
            }
          }
        }
      }
    }
  }





}