package restaure.rdf

import java.io.FileWriter
import java.lang._

import commons.XMLUnitParser
import org.apache.jena.rdf.model._

import scala.xml.{Elem, NodeSeq}
import fastparse.all._
import org.apache.jena.util.URIref
import restaure.rdf.Voc._
import org.apache.jena.vocabulary.RDF


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

  val lexicon:Resource = model.createResource(uri + "LexiqueDaubyRouchiPicard")

  val lexiconcoordinate:Resource = model.createResource(uri + "coordinate")
  lexicon.addProperty(Voc.coordinate, lexiconcoordinate)
  val xcoordinateAmiens:Property = model.createProperty(uri + "xAmiens")
  val ycoordinateAmiens:Property = model.createProperty(uri + "yAmiens")
  xcoordinateAmiens.addProperty(Voc.writtenRep,model.createLiteral("Amiens","fra"))
  ycoordinateAmiens.addProperty(Voc.writtenRep,model.createLiteral("Amiens","fra"))
  lexiconcoordinate.addProperty(xcoordinateAmiens,model.createTypedLiteral(new java.lang.Double(49.895)))
  lexiconcoordinate.addProperty(ycoordinateAmiens,model.createTypedLiteral(new java.lang.Double(2.3022)))

  val articles:NodeSeq = xml \\ "Entrée"
  val UF = new UsefulFunctionFirstParsing
  articles.zipWithIndex.foreach({case (a,i) => UF.RDFWriter(model,a,i,indice,lexicon)})



  def dumpModel(path : String): Unit = {
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


class UsefulFunctionFirstParsing  {


  def RDFWriter (m: Model, article: NodeSeq, id:Int, indice : Int, lexicon:Resource): Unit = {


    val leURI = uri + s"le${indice}PicardVersFrançais_$id"
    val lexEntry = m.createResource (leURI)
    lexicon.addProperty(Voc.Entry,lexEntry)
    lexEntry.addProperty(RDF.`type`,LexicalEntry)
    LexicalEntryWriter (lexEntry, article, leURI, m)
    StructGramWriter(article,lexEntry,m)
    LexieEntiereWriter(article,m, lexEntry)
  }

  def LexicalEntryWriter (lexEntry:Resource, article: NodeSeq, leURI: String, m: Model): Unit = {

    val Entry = article \ "Vocable"
    val fEntry = m.createResource (URIref.encode(uri + s"lf_${Entry.text}"))

    lexEntry.addProperty (Voc.lexicalForm, fEntry)
    fEntry.addProperty(Voc.writtenRep,s"${Entry.text.toLowerCase}")
    fEntry.addProperty(RDF.`type`,Form)

  }

  def StructGramWriter(article : NodeSeq, lexEntry : Resource, m: Model): Unit ={
    val XMLUP = new XMLUnitParser



    val CatégorieGrammaticale = article \ "CatégorieGrammaticale"

    val resStruct = XMLUP.XMLStructureGrammaticale.parse(CatégorieGrammaticale.text) match {
      case Parsed.Success(seq,_) => seq
      case _:Parsed.Failure => Nil
    }

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

    val Traduction = Définition \ "DéfinitionDétaillée" \ "DéfinitionFrançaise"  \ "df" \ "Trad" \ "Traduction"
    for (elem <- Traduction){
      val RessourceTraduction =
        model.createResource(URIref.encode(uri + s"Traduction${elem.text}"))
      lexEntry.addProperty(Voc.TranslatableInFrenchOneWord, RessourceTraduction)
      RessourceTraduction.addProperty(RDF.`type`,Form)
      RessourceTraduction.addProperty(Voc.writtenRep,elem.text)
    }


    val Exemples = Définition \\ "Exemples" \ "ExemplePicard"
    for (elem <- Exemples) {
      val RessourceExemplePicard =
        model.createResource(URIref.encode(uri + s"${elem.text}"))
      lexEntry.addProperty(Voc.ExampleInPicard, RessourceExemplePicard)
      RessourceExemplePicard.addProperty(RDF.`type`,Form)
      RessourceExemplePicard.addProperty(Voc.writtenRep, elem.text)
    }

    val Ancien = Définition \ "DéfinitionDétaillée" \ "Ancien"
    val RessourceAncien = model.createResource(URIref.encode(uri + s"${Ancien.text}"))
    lexEntry.addProperty(Voc.oldInformation, RessourceAncien)
    RessourceAncien.addProperty(writtenRep,Ancien.text)
    }





}