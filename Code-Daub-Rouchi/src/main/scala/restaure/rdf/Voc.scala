package restaure.rdf

import org.apache.jena.rdf.model.{Model, ModelFactory, Property, Resource}
import org.apache.jena.vocabulary.RDF

/**
  * Created by khamphousone on 7/12/17.
  */
object Voc {
  val uri = "http://restaure.limsi.fr/2017/rdf#"

  private val m: Model = ModelFactory.createDefaultModel()

  /*val ontolex = "http://www.w3.org/ns/lemon/ontolex#"*/
  m.read("http://www.w3.org/ns/lemon/ontolex#")
  val ontolex:Resource = m.getResource("http://www.w3.org/ns/lemon/ontolex#")
  val LexicalEntry:Resource = m.createResource(ontolex + "LexicalEntry")
  val Form:Resource = m.createResource(ontolex + "Form")
  val lexicalForm:Property = m.createProperty(ontolex + "lexicalForm")
  val writtenRep:Property = m.createProperty(ontolex + "writtenRep")

  val translatableAs:Property = m.createProperty(uri + "translatableAs")

  //val TranslatableInPicardOneWord:Property = m.createProperty(uri + "TranslatableInPicardOneWord")

  val verbatim:Property = m.createProperty(uri + "verbatim")

  val hasExample:Property = m.createProperty(uri + "hasExample")

  val etymology:Property = m.createProperty(uri + "etymology")

  m.read("http://www.w3.org/ns/lemon/lime#")
  val LInguisticMEtadata:Resource = m.getResource("http://www.w3.org/ns/lemon/lime#")
  val language:Property = m.createProperty(LInguisticMEtadata+"language")
  val Entry:Property = m.createProperty(LInguisticMEtadata + "entry")

  m.read("http://lexinfo.net/ontology/2.0/lexinfo#")
  val lexinfo:Resource = m.getResource("http://lexinfo.net/ontology/2.0/lexinfo#")
  val lexicalInfo:Property = m.createProperty(lexinfo.toString)
  val liPartOfSpeech:Property = m.createProperty(lexinfo + "partOfSpeech")
  val synonym:Property = m.createProperty(lexinfo + "synonym")


  val liGender:Property = m.createProperty(lexinfo + "gender")




 /*m.read("https://www.w3.org/ns/lemon/vartrans#")
  val vartrans:Resource = m.getResource("https://www.w3.org/ns/lemon/vartrans#")
  val vttranslatableAs:Property = m.createProperty(vartrans + "TranslatableAs")*/


  val coordinate:Property = m.createProperty(uri + "Coordinate")
  val lexiconNameWR:Property = m.createProperty(uri + "lexiconNameWrittenRep")
  val lexiconAuthorWR:Property = m.createProperty(uri + "lexiconAuthorWrittenRep")
  val lexiconDirectionWR:Property = m.createProperty(uri + "lexiconDirectionWrittenRep")

  m.read("http://www.w3.org/2004/02/skos/core#")
  val skos:Resource = m.getResource("http://www.w3.org/2004/02/skos/core#")
  val definition:Property = m.createProperty(skos + "definition")
}
