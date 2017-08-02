package restaure.rdf

import org.apache.jena.rdf.model.{Model, ModelFactory, Property, Resource}

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
  val TranslatableInFrench:Property = m.createProperty(uri + "TranslatableInFrench")
  val TranslatableAsDef:Property = m.createProperty(uri + "TranslatableAsDef")
  val ExampleInPicard:Property = m.createProperty(uri + "ExampleInPicard")
  val oldInformation:Property = m.createProperty(uri + "oldInformation")
  val LInguisticMEtadata = "http://www.w3.org/ns/lemon/lime#"
  val Entry:Property = m.createProperty(LInguisticMEtadata + "entry")

  val lexinfo = "http://lexinfo.net/ontology/2.0/lexinfo#"
  val lexicalInfo:Property = m.createProperty(lexinfo)
  val liPartOfSpeech:Property = m.createProperty(lexinfo + "partOfSpeech")



  val liGender:Property = m.createProperty(lexinfo + "gender")





  val vartrans = "https://www.w3.org/ns/lemon/vartrans#"
  val vttranslatableAs:Property = m.createProperty(vartrans + "TranslatableAs")

  val liFemale:Resource = m.createResource(lexinfo + "Female")
  val liMasculine:Resource = m.createResource(lexinfo + "Masculine")
  liFemale.addProperty(Voc.writtenRep,m.createLiteral("Féminin","fra"))
  liMasculine.addProperty(Voc.writtenRep,m.createLiteral("Masculin","fra"))

  val liVerb:Resource = m.createResource(lexinfo + "Verb")
  val liNoun:Resource = m.createResource(lexinfo + "Noun")
  val liAdj:Resource = m.createResource(lexinfo + "Adjective")
  val liPreposition:Resource = m.createResource(lexinfo + "Preposition")
  liPreposition.addProperty(Voc.writtenRep,m.createLiteral("Préposition","fra"))
  liVerb.addProperty(Voc.writtenRep, m.createLiteral("Verbe","fra"))
  liNoun.addProperty(Voc.writtenRep,m.createLiteral("Nom","fra"))
  liAdj.addProperty(Voc.writtenRep,m.createLiteral("Adjectif","fra"))
}
