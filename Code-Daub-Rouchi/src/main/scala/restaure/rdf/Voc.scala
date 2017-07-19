package restaure.rdf

import org.apache.jena.rdf.model.{Model, ModelFactory, Property, Resource}
import org.apache.jena.vocabulary.VCARD

/**
  * Created by khamphousone on 7/12/17.
  */
object Voc {
  val uri = "http://restaure.limsi.fr/2017/rdf#"

  private val m: Model = ModelFactory.createDefaultModel()

  val ontolex = "http://www.w3.org/ns/lemon/ontolex#"
  val LexicalEntry:Resource = m.createResource(ontolex + "LexicalEntry")
  val Form:Resource = m.createResource(ontolex + "Form")
  val lexicalForm:Property = m.createProperty(ontolex + "lexicalForm")
  val writtenRep:Property = m.createProperty(ontolex + "writtenRep")
  val TranslatableAsForm:Property = m.createProperty(uri + "TranslatableAsForm")

  val lexinfo = "http://lexinfo.net/ontology/2.0/lexinfo#"
  val lexicalInfo:Property = m.createProperty(lexinfo)
  val liPartOfSpeech:Property = m.createProperty(lexinfo + "partOfSpeech")
  val liVerb:Property = m.createProperty(lexinfo + "Verb")
  val liNoun:Property = m.createProperty(lexinfo + "Noun")
  val liAdj:Property = m.createProperty(lexinfo + "Adjective")
  val liPreposition:Property = m.createProperty(lexinfo + "Preposition")


  val liGender:Property = m.createProperty(lexinfo + "Gender")
  val liGFemale:Property = m.createProperty(lexinfo + "Female")
  val liMasculine:Property = m.createProperty(lexinfo + "Masculine")


  val vartrans = "https://www.w3.org/ns/lemon/vartrans#"
  val vttranslatableAs:Property = m.createProperty(vartrans + "TranslatableAs")
}
