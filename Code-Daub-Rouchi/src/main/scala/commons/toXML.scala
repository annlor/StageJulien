package commons

/**
  * Created by khamphousone on 6/29/17.
  */
import scala.xml.Elem

class toXML {
  def SeqtoXml(seq: Seq[String]): Seq[Elem] = {
    val res = for (elements <- seq) yield {
      <Lexie>
        {elements}
      </Lexie>
    }
    res
  }


  class Trad(mot : String, abreviation : String, definitions : Seq[String]) {

    def toXml : Elem =
      <ArticleDeDictionnaire>
        <Entrée>{mot}</Entrée>
        <StructureGrammaticale>{abreviation}</StructureGrammaticale>
        {SeqtoXml(definitions)}
      </ArticleDeDictionnaire>

  }
  class Trad2(mot : String, complement : Option[String]
              , abreviation : String, traduction : Seq[Seq[String]]
              , lexie : Seq[String]) {

    def DoubleSeqtoXml(doubleseq: Seq[Seq[String]]): Seq[Elem] = {
      for (elements <- doubleseq) yield {
        <Lexie>{for (finalelements <- elements) yield {
          <SousLexie>{finalelements}</SousLexie>
        }
      }</Lexie>}

    }

    def toXml : Elem =
      <ArticleDeDictionnaire>
        <Entrée>{mot}</Entrée>
        <Complément>{complement.getOrElse("")}</Complément>
        <StructureGrammaticale>{abreviation}</StructureGrammaticale>
        <LexieEntiere>{lexie}</LexieEntiere>
        {DoubleSeqtoXml(traduction)}
      </ArticleDeDictionnaire>

  }

  class TradDebrie(article : ArticlePicard) {

    def toXml : Elem =
      <ArticleDeDictionnaire>
        <Entrée>{article.Entrée}</Entrée>
        <StructureGrammaticale>{article.StructureGrammaticale}</StructureGrammaticale>
        {SeqtoXml(article.Lexies)}
      </ArticleDeDictionnaire>
  }
}
