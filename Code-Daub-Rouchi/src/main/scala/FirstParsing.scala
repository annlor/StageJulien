/**
  * Created by khamphousone on 6/22/17.
  */

import java.io.{File, FileWriter}

import Config.Configuration

import scala.io.Source
/**
import java.io._
*/
import commons._
import fastparse.all._

import scala.xml._
/**
import scala.collection.JavaConverters._
*/


/**
  * Cette case classe contriendra les éléments de chaque ligne
  */


/**
  * Classe permettant de définir les différents parsings
  */
class FirstParsing {



  /**
    *
    * @param str String : chaîne de caractères à parser
    * @return String après parsage
    */
  def Parser(str: String): TraductionPicard = {
    val UP = new UnitParser()

    val MotPicard = P(("* ".? ~ UP.Letters.rep(sep = UP.Parenthesis) ~ " ".? ~
      UP.Parenthesis).!)



    val Debut: Parser[(String, String, Seq[String])] = P(MotPicard.! ~ ", ".? ~
      UP.Abreviation.?.! ~ " : ".?
  ~ (UP.PoncDefinitions | UP.NumbersDef ~ UP.PoncDefinitions).!.rep)


   Debut.parse(str) match {
     case Parsed.Success(("",_,_),_) => TraductionPicard("","",Seq(""),Seq(""))
     case Parsed.Success((str1: String, str2: String, seq: Seq[String]), _) =>
       TraductionPicard(str1, str2, seq,seq)

     case f: Parsed.Failure =>
       TraductionPicard(s"Failure $str \n ${f.extra.traced.trace}", s"${f.index}", Seq(""),Seq(""))
       /**TraductionPicard("","",Seq(""))*/

     case Parsed.Success(_,_) => TraductionPicard("Error","",Seq(""),Seq(""))

   }

  }

}

class ParsingDefinition {

  def ANCParser (str:String) : Seq[(String,Option[String])]={
    val UP = new UnitParser
    val parsertoutsaufancien = P((!"ANC." ~ AnyChar).rep(min = 1).!)
    val parserANC = P(parsertoutsaufancien.! ~ ("ANC. :" ~ AnyChar.rep).! |
      parsertoutsaufancien.! ~ "".!)
    parserANC.parse(str) match {
      case Parsed.Success((str1, ""),_) => Seq((str1,Option("")))
      case Parsed.Success((str1, str2),_) => Seq((str1, Option(str2)))
      case f:Parsed.Failure => Seq((s"Failure $str \n ${f.extra.traced.trace}",Option("")))

    }
  }

  def ParserDef (str: String): CCLexie ={
    val IP = new intermediateParser
    val UP = new UnitParser
    val parserlexie = P((IP.ExemplePicard.!.? ~ IP.Definitions.! ~ IP.ExemplePicard.!.rep).rep)
    parserlexie.parse(str) match {

      case Parsed.Success((seq: Seq[(Option[String],String,Seq[String])]),_) => CCLexie(seq)
      case f:Parsed.Failure => CCLexie(Seq((None,s"Failure $str \n ${f.extra.traced.trace}",Seq(""))))
      case Parsed.Success(_,_) => CCLexie(Seq((None,"ERROR",Seq(""))))
    }
  }
}

object FirstParsing {


  def main(args: Array[String]): Unit = {
    val classpath = new Configuration()
    val buff: Source = Source.fromFile(classpath.pathDaubyRouchi)
    val Parsing = new FirstParsing()
    val UP = new UnitParser()
    val tradtoxml = new toXML()
    val SubParsing = new ParsingDefinition()
    val liste1 = for (line <- buff
      .getLines.slice(1213, 7446)
      .filter(_.count(_.equals(' ')) >= 3)) yield {
      Parsing.Parser(line)
    }

    val liste2 = liste1.toList
    for (elements <- 'É'::('A' to 'Z').toList) {

      val listTrad = for (trad <- liste2 if
      List(s"$elements", s"* $elements").exists(trad.mot.startsWith)) yield {
        val tuple = (for(defs <- trad.definitions) yield {
          SubParsing.ParserDef(SubParsing.ANCParser(defs).head._1)
        },for(defs <- trad.definitions) yield {
          SubParsing.ANCParser(defs).head._2
        })

           val Traduction = new tradtoxml.Trad(trad.mot, trad.abreviation,
             tuple._1 ,tuple._2, trad.defentiere)
           Traduction.toXml
         }


        val listXml = <TEI  xml:lang="fr"  xmlns="http://www.tei-c.org/ns/1.0">
          {listTrad}
        </TEI>
        val dir = new File("./XMLFirstParser")
        dir.mkdir
      val printer = new scala.xml.PrettyPrinter(80,2)
      val str = printer.format(listXml)
      val fw = new FileWriter(s"./XMLFirstParser/$elements")
      fw.write(str)
      fw.close()
      /*
        XML.save(s"./XMLFirstParser/$elements", listXml, "utf-8", true, null)*/
      }
  println("FirstParsing DONE")
  }





}
