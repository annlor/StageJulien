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
  lexicon.addProperty(lexiconNameWR,model.createLiteral("Le Livre du \"Rouchi\" Parler Picard de Valenciennes","fra"))
  lexicon.addProperty(lexiconAuthorWR,model.createLiteral("Jean Dauby","fra"))
  lexicon.addProperty(lexiconDirectionWR,model.createLiteral("picard vers français","fra"))
  lexicon.addProperty(Voc.language,model.createLiteral("pcd","fra"))
  val lexiconcoordinate:Resource = model.createResource(uri + "coordinate")
  lexicon.addProperty(Voc.coordinate, lexiconcoordinate)
  val xcoordinateValenciennes:Property = model.createProperty(uri + "xValenciennes")
  val ycoordinateValenciennes:Property = model.createProperty(uri + "yValenciennes")
  xcoordinateValenciennes.addProperty(Voc.writtenRep,model.createLiteral("Valenciennes","fra"))
  ycoordinateValenciennes.addProperty(Voc.writtenRep,model.createLiteral("Valenciennes","fra"))
  lexiconcoordinate.addProperty(xcoordinateValenciennes,model.createTypedLiteral(new java.lang.Double(50.35)))
  lexiconcoordinate.addProperty(ycoordinateValenciennes,model.createTypedLiteral(new java.lang.Double(3.53333)))

  val articles:NodeSeq = xml \\ "entry"
  val UF = new UsefulFunctionFirstParsing
  articles.zipWithIndex.foreach({case (a,i) => UF.RDFWriter(model,a,i,indice,lexicon)})



  def dumpModel(path : String): Unit = {
    val fw = new FileWriter(path)
    model.setNsPrefix("restaure", uri)
    model.setNsPrefix("ontolex", Voc.ontolex.getURI)
    model.setNsPrefix("lexinfo", Voc.lexinfo.getURI)
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

    val Entry = article \ "form"
    val fEntry = m.createResource (URIref.encode(uri + s"lf_${(Entry \ "orth").text}"))

    lexEntry.addProperty (Voc.lexicalForm, fEntry)
    fEntry.addProperty(Voc.writtenRep,s"${(Entry \ "orth").text.toLowerCase}")
    fEntry.addProperty(RDF.`type`,Form)

  }

  def StructGramWriter(article : NodeSeq, lexEntry : Resource, m: Model): Unit ={
    val XMLUP = new XMLUnitParser



    val CatégorieGrammaticale = article \ "gramGrp" \ "pos"

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

  def LexieEntiereWriter(article: NodeSeq, model:Model, lexEntry:Resource):Unit= {
    val Définition = article \\ "def"
    for (defelem <- Définition) {
      if ((defelem \ "@label").toString == "verbatim") {
        val LexieEntière = defelem \ "quote"
        val fLexieEntière = model.createResource(URIref.encode(uri + s"${LexieEntière.text}"))
        lexEntry.addProperty(Voc.verbatim, fLexieEntière)
        fLexieEntière.addProperty(Voc.writtenRep, LexieEntière.text)
      }
      if ((defelem \ "@label").toString == "française") {
        val DefinitionFrançaise = defelem \ "quote"

        for (elem <- DefinitionFrançaise) {

          val RessourceDefinitionFrançaise =
            model.createResource(URIref.encode(uri + s"${elem.text}"))
          lexEntry.addProperty(Voc.definition, RessourceDefinitionFrançaise)
          RessourceDefinitionFrançaise.addProperty(Voc.writtenRep, elem.text)
        }

        val Traduction = defelem \ "cit"
        for (elem <- Traduction) {
          val RessourceTraduction =
            model.createResource(URIref.encode(uri + s"Traduction${elem.text}"))
          lexEntry.addProperty(Voc.translatableAs, RessourceTraduction)
          RessourceTraduction.addProperty(RDF.`type`, Form)
          RessourceTraduction.addProperty(Voc.writtenRep, elem.text)
        }
      }
    }
    val cit = article \ "cit"
    for (elemcit <- cit) {

      if ((elemcit \ "@type").toString == "example") {
        val Exemples = elemcit \ "quote"
        for (elem <- Exemples) {
          val RessourceExemplePicard =
            model.createResource(URIref.encode(uri + s"${elem.text}"))
          lexEntry.addProperty(Voc.hasExample, RessourceExemplePicard)
          RessourceExemplePicard.addProperty(RDF.`type`, Form)
          RessourceExemplePicard.addProperty(Voc.writtenRep, elem.text)
        }
      }
    }
    val Ancien = article \ "etym"
    val RessourceAncien = model.createResource(URIref.encode(uri + s"${Ancien.text}"))
    lexEntry.addProperty(Voc.etymology, RessourceAncien)
    RessourceAncien.addProperty(writtenRep,Ancien.text)
    }





}