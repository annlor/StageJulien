/**
  * Created by khamphousone on 6/29/17.
  *
  * Version 2.0
  */

import scala.io.Source
import java.io._

import fastparse.all._

import scala.xml._
import commons._



class ParserDebrieV2 {
  def ParserDV2(str: String): Dictionnaire = {
    val UP = new UnitParser()

    val EntréePicarde = P(UP.Ponctuation.? ~ (UP.Letters.rep(sep = "'" | " ") ~ ")".? ~
      " ".? ~ ("(".? ~ UP.Letters ~ "'".? ~ ")".?).?).!)

    val StructGrammaticaleDebrie = P((UP.LowerCaseLetter ~ ".").rep(min = 1, sep = " ".?).! ~ ",".?)
    val Nombre = P(UP.Numbers.rep(min = 1, max = 2).!)
    val Numerotation = P((UP.Numbers ~ ")").!)
    val DefinitionDebrie = P(UP.Letters.rep(min = 1, sep = UP.Ponctuation | Nombre ~ UP.Ponctuation.?).! ~
      (UP.Ponctuation | Nombre).rep).log()

    val ArticleDeDictionnaire :P[(String, String, Seq[String])]
    = P("\n\n" ~ " ".? ~ EntréePicarde.! ~ " ".? ~
      "," ~ " ".? ~ StructGrammaticaleDebrie.! ~ " ".? ~
      (DefinitionDebrie | (Numerotation ~ DefinitionDebrie)).!.rep(sep = "\n" ~ !"\n"))

    val ParserLexique :P[Seq[ArticlePicard]] =
      P(ArticleDeDictionnaire.map(a => ArticlePicard(a._1, a._2, a._3)).rep(min = 11)).log()

    ParserLexique.parse(str) match {
      case Parsed.Success(seqresult: Seq[ArticlePicard],_) => {
        println("Success")
        Dictionnaire(seqresult)
      }
      case f:Parsed.Failure =>
        println("Failure")
        Dictionnaire(Seq(ArticlePicard(s"Failure $str \n ${f.extra.traced.trace}", s"${f.index}",Seq(""))))

    }
  }
}

object ParserDebrieV2 {


  def main(args: Array[String]): Unit = {
    println("Entrez le chemin du dictionnaire Debrie")
    val path = scala.io.StdIn.readLine()
    val buff: Source = Source.fromFile("path")
    val Parsing = new ParserDebrieV2()
    val tradtoxml = new toXML()

    val Dict = Parsing.ParserDV2(buff.getLines.slice(0,21708).mkString("\n").replaceAll("\t"," "))


    val DictXML = for(elements <- Dict.Article) yield {
      val Traduction = new tradtoxml.TradDebrie(elements)
      Traduction.toXml
    }

    val listXml = <Nomenclature>{DictXML}</Nomenclature>

    XML.save(s"/people/khamphousone/Documents/ParsersScala/XML/ParserDebrie/Version 2.0/ResultDebrie", listXml, "utf-8", true, null)
    println("Parser Debrie Version 2.0 DONE")
  }
}