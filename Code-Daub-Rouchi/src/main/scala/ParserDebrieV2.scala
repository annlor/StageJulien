/**
  * Created by khamphousone on 6/29/17.
  *
  * Version 2.0
  */

import scala.io.Source
import java.io._

import Config.Configuration
import fastparse.all._

import scala.xml._
import commons._



class ParserDebrieV2 {

  def ParserDV2(str: String): ArticlePicard = {
    val UP = new UnitParser()

    val EntréePicarde = P(UP.Ponctuation.? ~ (UP.Letters.rep(sep = "'" | " ") ~ ")".? ~
      " ".? ~ ("(".? ~ UP.Letters ~ "'".? ~ ")".?).?).!)

    val StructGrammaticaleDebrie = P((UP.LowerCaseLetter.rep(sep=" ") ~" ".? ~ ".").rep(min = 1, sep = (" "|"'").?).!)
    val Nombre = P(UP.Numbers.rep(min = 1) ~ !")".!)
    val Numerotation = P((UP.Numbers ~ ")").!)
    val DefinitionDebrie = P((UP.Letters|Nombre).rep(min = 1, sep = UP.Ponctuation | Nombre ~ UP.Ponctuation.?).! ~
      (UP.Ponctuation | Nombre).rep)

    val ArticleDeDictionnaire :P[(String, String, Seq[String])]
    = P(" ".rep ~ EntréePicarde.! ~ " ".? ~
      ",".? ~ " ".? ~ StructGrammaticaleDebrie.! ~ (","|" "|";").rep ~
      (Numerotation ~ DefinitionDebrie | DefinitionDebrie ).!.rep(min = 1))

    val ParserLexique :P[ArticlePicard] =
      P(ArticleDeDictionnaire.map(a => ArticlePicard(a._1, a._2, a._3)))

    val SousArticleDeDictionnaire : P[(String, Option[String], Option[String], String)] =
      P(" ".rep ~ EntréePicarde.! ~ " ".? ~ ",".? ~ " ".? ~
        ("dans l'exp." ~ " ".?).!.? ~ StructGrammaticaleDebrie.!.? ~
        (","|" "|";").rep ~ AnyChar.rep.!)

    val SousParserLexique :P[ArticlePicard] =
      P(SousArticleDeDictionnaire.map(a =>
        if (a._3.mkString == "dans l'exp."){
          ArticlePicard(a._1,"",Seq(a._2.mkString + a._3.mkString + a._4))
        }
        else {
        ArticlePicard(a._1,a._3.mkString,Seq(a._2.mkString + a._4))
        }))

    ParserLexique.parse(str) match {
      case Parsed.Success(seqarticle: ArticlePicard,_) =>

        println("Success")
        seqarticle

      case f:Parsed.Failure =>

        println("Failure")

        SousParserLexique.parse(str) match {
          case Parsed.Success(seqarticle:ArticlePicard,_) =>
            println("SECONDSuccess")
            seqarticle
          case f:Parsed.Failure =>
            println("SECONDFailure")
            ArticlePicard(s"Failure $str \n ${f.extra.traced.trace}", s"${f.index}",Seq(""))
        }

    }
  }
  def replaceAllBreaklines(str : String): String = {
    var resstr = str.replaceAll("\n\n\n","BREAKLINES")
    resstr = resstr.replaceAll("\n\n","BREAKLINES")
    resstr = resstr.replaceAll("\n","")
    resstr = resstr.replaceAll("BREAKLINES","\n")
    resstr
  }
}

object ParserDebrieV2 {


  def main(args: Array[String]): Unit = {
    val classpath = new Configuration
    val buff: Source = Source.fromFile(classpath.pathReneDebrie)
    val Parsing = new ParserDebrieV2()
    val tradtoxml = new toXML()
    var str = buff.getLines.slice(10,21708).mkString("\n").replaceAll("\t"," ")
    str = Parsing.replaceAllBreaklines(str)
    val file = new File(classpath.pathOutputReneDebrie)
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(str)
    val Dict = for (line <- Source.fromFile(file).getLines.filter(_.count(_.equals(' ')) >= 3)) yield {
      if (line.length <= 7){
        ArticlePicard("","",Seq(""))
      }
      else {
        Parsing.ParserDV2(line)
      }
    }
    val DictionnaireSeq = Dictionnaire(Dict.toSeq)

    val DictXML = for(elements <- DictionnaireSeq.Article) yield {
      val Traduction = new tradtoxml.TradDebrie(elements)
      Traduction.toXml
    }

    val listXml = <Nomenclature>{DictXML}</Nomenclature>

    XML.save(classpath.pathOutputXMLDebrie2, listXml, "utf-8", true, null)



    println("Parser Debrie Version 2.0 DONE")

  }
}