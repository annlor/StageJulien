/**
  * Created by khamphousone on 6/28/17.
  *
  * Version 1.0
  *
  */

import scala.io.Source
import java.io._

import fastparse.all._

import scala.xml._
import commons._


class ParserDebrie {

  def ParserD(str: String): TraductionPicard = {
    val UP = new UnitParser()


    val MotPicard = P(" ".rep ~ (UP.LowerCaseLetter.rep(sep = "'") ~ " ".? ~
      "(".? ~ UP.LowerCaseLetter.? ~ "'".? ~ ")".?).!)

    val AbreviationDebrie = P((UP.LowerCaseLetter ~ ".").rep(min = 1, sep = " ".?).!)
    val Nombre = P(UP.Numbers.rep(min = 1, max = 2).!)
    val Numerotation = P((UP.Numbers ~ ")").!)

    val DefinitionDebrie = P(UP.Ponctuation.? ~
      UP.Letters.rep(min = 1, sep = (UP.Ponctuation | Nombre).rep).! ~
      (UP.Ponctuation | Nombre).rep)

    val ParserLexique = P(MotPicard.! ~
      (" " | "\t" ).rep ~ ",".? ~ (" " | "\t").rep ~
      AbreviationDebrie.?.! ~ ",".? ~
      (DefinitionDebrie | (Numerotation ~ DefinitionDebrie)).!.rep)

    ParserLexique.parse(str) match {
      case Parsed.Success(("",_,_),_) => TraductionPicard("","",Seq(""))
      case Parsed.Success((str1: String, str2: String, seq: Seq[String]), _) =>
        TraductionPicard(str1, str2, seq)

      case f: Parsed.Failure =>
        TraductionPicard(s"Failure $str \n ${f.extra.traced.trace}", s"${f.index}", Seq(""))
      /**TraductionPicard("","",Seq(""))*/




    }
  }
}

object MainObjectDebrie {


  def main(args: Array[String]): Unit = {

    /** /!\ Nom du path à changer si compilation sur autre machine */
    val buff: Source = Source.fromFile("/people/khamphousone/Documents/Dictionnaires/a_debr_oues_84S_A_utf8.txt")
    val Parsing = new ParserDebrie()
    val tradtoxml = new toXML()
    val UP = new UnitParser()
    val writer = new PrintWriter(new File("/people/khamphousone/Documents/ParsersScala/Failures/DebrieFailures.txt"))

    val liste1 = for (line <- buff.getLines.slice(11, 21709)) yield {
      Parsing.ParserD(line)
    }

    val liste2 = liste1.toList

    for (elements <- 'ë'::'ê'::'è'::'é'::('a' to 'z').toList) {

      val listTrad = for (trad <- liste2 if
      List(s"$elements").exists(trad.mot.startsWith)) yield {
        val Traduction = new tradtoxml.Trad(trad.mot, trad.abreviation, trad.definitions)
        Traduction.toXml
      }


      val listXml = <Nomenclature>
        {listTrad}
      </Nomenclature>

      XML.save(s"/people/khamphousone/Documents/ParsersScala/XML/ParserDebrie/$elements", listXml, "utf-8", true, null)
    }
    println("ParserDebrie.scala DONE")
  }





}
