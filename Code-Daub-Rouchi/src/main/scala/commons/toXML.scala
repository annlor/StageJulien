package commons

/**
  * Created by khamphousone on 6/29/17.
  */
import scala.xml._

class toXML {
  def SeqtoXml(seq: Seq[String]): Seq[Elem] = {
    val res = for (elements <- seq) yield {
      <cit type="translation"><quote>{elements}</quote></cit>
    }
    res
  }


  class Trad(mot : String, abreviation : String, Lexie : Seq[CCLexie], seqopt : Seq[Option[String]],
             definitionentiere : Seq[String]) {

    def SeqtoXmlExemple(seq: Seq[String]): Seq[Elem] = {

      val res = for (elements <- seq.zipWithIndex) yield {
          <cit type="example" xml:lang="pcd"><quote>{elements._1}</quote></cit>
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
      val loop1 =  {for (elements <- seqcollect2) yield {<def label="française"><quote>{elements}</quote></def>
      }}
      val loop2 = {SeqtoXmlExemple(seqres)}
      val loop3 = <etym>{for (elem <- seqopt) yield {if (elem.map(_.toString).getOrElse("").length > 1) <quote>{elem.map(_.toString).getOrElse("")}</quote>}}</etym>
      loop1 ++ loop2 ++ loop3
    }

    def toXml : Elem =
      <entry>
        <form><orth>{mot.trim}</orth></form>
        <gramGrp><pos>{abreviation}</pos></gramGrp>
        <def label ="verbatim">{for (str <- definitionentiere) yield {
          <quote>{str}</quote>}}</def>
          {for (lex <- Lexie) yield {
      SeqTupletoXml(lex.seq, seqopt)}}</entry>


  }
  class Trad2(mot : String, complement : Option[String]
              , abreviation : String, traduction : Seq[Seq[String]]
              , lexie : Seq[String]) {

    def DoubleSeqtoXml(doubleseq: Seq[Seq[String]]): Seq[Elem] = {
      for (elements <- doubleseq) yield {
        <cit type ="translation">{for (finalelements <- elements) yield {
          <quote>{finalelements}</quote>
        }
      }</cit>}

    }

    def toXml : Elem =
      <entry>
        <form><orth>{mot.trim}</orth><Complément>{complement.getOrElse("")}</Complément></form>
        <gramGrp><pos>{abreviation}</pos></gramGrp>
        <def><quote>{lexie}</quote></def>
        {DoubleSeqtoXml(traduction)}
      </entry>

  }

  class TradDebrie(article : ArticlePicard) {

    def toXml : Elem =
      <entry>
        <form><orth>{article.Entrée.trim}</orth></form>
        <gramGrp><pos>{article.StructureGrammaticale}</pos></gramGrp>
        {SeqtoXml(article.Lexies)}
        </entry>
  }
}
