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

class RDFDebrie(xml : Elem) {
    val XMLUP = new XMLUnitParser
    val model:Model = ModelFactory.createDefaultModel()

    val lexicon:Resource = model.createResource(uri + "LexiqueDebrie")
  lexicon.addProperty(lexiconNameWR,model.createLiteral("Lexique Picard des Parlers Ouest-Amiénois","fra"))
  lexicon.addProperty(lexiconAuthorWR,model.createLiteral("René Debrie","fra"))
  lexicon.addProperty(lexiconDirectionWR,model.createLiteral("picard vers français","fra"))
  lexicon.addProperty(Voc.language,model.createLiteral("pcd","fra"))
    val lexiconcoordinate:Resource = model.createResource(uri + "coordinate")
    lexicon.addProperty(Voc.coordinate, lexiconcoordinate)
    val xcoordinateAmiens:Property = model.createProperty(uri + "xAmiens")
    val ycoordinateAmiens:Property = model.createProperty(uri + "yAmiens")
  xcoordinateAmiens.addProperty(Voc.writtenRep,model.createLiteral("Amiens","fra"))
  ycoordinateAmiens.addProperty(Voc.writtenRep,model.createLiteral("Amiens","fra"))
  lexiconcoordinate.addProperty(xcoordinateAmiens,model.createTypedLiteral(new java.lang.Double(49.895)))
    lexiconcoordinate.addProperty(ycoordinateAmiens,model.createTypedLiteral(new java.lang.Double(2.3022)))


    val articles:NodeSeq = xml \\ "entry"
    val UF = new UsefulFunctionDebrie
    articles.zipWithIndex.foreach({case (a,i) => UF.RDFWriter(model,a,i,lexicon)})



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
class UsefulFunctionDebrie  {


  def RDFWriter (m: Model, article: NodeSeq, id:Int, lexicon:Resource): Unit = {


    val leURI = uri + s"le_RenéDebrie_$id"
    val lexEntry = m.createResource (leURI)
    lexicon.addProperty(Voc.Entry,lexEntry)
    lexEntry.addProperty(RDF.`type`,LexicalEntry)
    LexicalEntryWriter (lexEntry, article, leURI, m)
    StructGramWriter(article,lexEntry,m)
    LexieEntiereWriter(article,m, lexEntry)
  }

  def LexicalEntryWriter (lexEntry:Resource, article: NodeSeq, leURI: String, m: Model): Unit = {

    val Entry = article \ "form"
    val fEntry = m.createResource (URIref.encode(uri + s"Debrie_lf_${(Entry \ "orth").text}"))

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
          liVerb.addProperty(Voc.writtenRep, m.createLiteral("Verbe","fra"))

        case "s." =>
          lexEntry.addProperty(Voc.liPartOfSpeech, liNoun)
          liNoun.addProperty(Voc.writtenRep,m.createLiteral("Nom","fra"))

        case "adj." =>
          lexEntry.addProperty(Voc.liPartOfSpeech,liAdj)
          liAdj.addProperty(Voc.writtenRep,m.createLiteral("Adjectif","fra"))
        case "f." =>
          lexEntry.addProperty(Voc.liGender, liFemale)
          liFemale.addProperty(Voc.writtenRep,m.createLiteral("Féminin","fra"))

        case "m." =>
          lexEntry.addProperty(Voc.liGender, liMasculine)
          liMasculine.addProperty(Voc.writtenRep,m.createLiteral("Masculin","fra"))

        case "prép." =>
          lexEntry.addProperty(Voc.liPartOfSpeech, liPreposition)
          liPreposition.addProperty(Voc.writtenRep,m.createLiteral("Préposition","fra"))

        case _ =>
      }
    }
  }

  def LexieEntiereWriter(article: NodeSeq, model:Model, lexEntry:Resource):Unit={
    val Traduction = article \\ "cit" \ "quote"
    for (elem <- Traduction){
      val RessourceTraduction =
        model.createResource(URIref.encode(uri + s"Traduction${elem.text}"))
      lexEntry.addProperty(Voc.verbatim, RessourceTraduction)
      RessourceTraduction.addProperty(RDF.`type`,Form)
      RessourceTraduction.addProperty(Voc.writtenRep,elem.text)
    }
  }





}