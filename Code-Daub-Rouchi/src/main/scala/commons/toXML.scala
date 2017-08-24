package commons

/**
  * Created by khamphousone on 6/29/17.
  */
import scala.xml._

class toXML {
  def SeqtoXml(seq: Seq[String]): Seq[Elem] = {
    val res = for (elements <- seq) yield {
      <Traduction>{elements}</Traduction>
    }
    res
  }


  class Trad(mot : String, abreviation : String, Lexie : Seq[CCLexie], seqopt : Seq[Option[String]],
             definitionentiere : Seq[String]) {

    def SeqtoXmlExemple(seq: Seq[String]): Seq[Elem] = {

      val res = for (elements <- seq.zipWithIndex) yield {
          <ExemplePicard>{elements._1}</ExemplePicard> %
            Attribute(None,"id", Text(s"${elements._2+1}"),Null)
      }
      res
    }

    def SeqTupletoXml(seq: Seq[(Option[String], String,Seq[String])],
                      seqopt: Seq[Option[String]]): NodeSeq = {
      val seqcollect1 = seq.map(_._1)
      val seqcollect2 = seq.map(_._2)
      val seqcollect3 = seq.map(_._3)
      var seqexemple = Seq[String]()
      for (seqelem <- seqcollect3){
        for(elem <- seqelem){
          seqexemple = seqexemple :+ elem
        }
      }
      val seqres = seqexemple++seqcollect1.flatten
      val loop1 =  <DéfinitionFrançaise>{for (elements <- seqcollect2.zipWithIndex) yield {<df>{elements._1}</df> % Attribute(None,"id", Text(s"${elements._2+1}"),Null)
      }}</DéfinitionFrançaise>
      val loop2 = <Exemples>{SeqtoXmlExemple(seqres)}</Exemples>
      val loop3 = <Ancien>{for (elem <- seqopt) yield {if (elem.map(_.toString).getOrElse("").length > 1) {elem.map(_.toString).getOrElse("")}}}</Ancien>
      loop1 ++ loop2 ++ loop3
    }

    def toXml : Elem =
      <Entrée>
        <Vocable>{mot}</Vocable>
        <CatégorieGrammaticale>{abreviation}</CatégorieGrammaticale>
        <Définition>{for (str <- definitionentiere) yield {
          <DéfinitionEntiere>{str}</DéfinitionEntiere>}}
          <DéfinitionDétaillée>{for (lex <- Lexie) yield {
      SeqTupletoXml(lex.seq, seqopt)}}</DéfinitionDétaillée></Définition>
      </Entrée>

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
        <Entrée>{mot.trim}</Entrée>
        <Complément>{complement.getOrElse("")}</Complément>
        <StructureGrammaticale>{abreviation}</StructureGrammaticale>
        <LexieEntiere>{lexie}</LexieEntiere>
        {DoubleSeqtoXml(traduction)}
      </ArticleDeDictionnaire>

  }

  class TradDebrie(article : ArticlePicard) {

    def toXml : Elem =
      <Entrée>
        <Vocable>{article.Entrée.trim}</Vocable>
        <CatégorieGrammaticale>{article.StructureGrammaticale}</CatégorieGrammaticale>
        {SeqtoXml(article.Lexies)}
      </Entrée>
  }
}
