package restaure.rdf

import org.apache.jena.rdf.model.{Model, ModelFactory}
import org.apache.jena.vocabulary.VCARD

/**
  * Created by khamphousone on 7/12/17.
  */
object Voc {
  val uri = "http://restaure.limsi.fr/2017/rdf#"

  private val m: Model = ModelFactory.createDefaultModel()
  val ontolex = "http://www.w3.org/ns/lemon/ontolex#"
  val LexicalEntry = m.createResource(ontolex + "LexicalEntry")
  val Form = m.createResource(ontolex + "Form")

  val lexicalForm = m.createProperty(ontolex + "lexicalForm")
  val writtenRep = m.createProperty(ontolex + "writtenRep")

  val translation = m.createProperty(uri + "translation")

  val lexinfo = "http://lexinfo.net/ontology/2.0/lexinfo#"
  val lexicalinfo = m.createProperty(lexinfo)
  val lipartofspeech = m.createProperty(lexinfo + "partOfSpeech")
  val liverb = m.createProperty(lexinfo + "Verb")
  val linoun = m.createProperty(lexinfo + "Noun")

}
