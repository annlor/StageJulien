/**
  * Created by khamphousone on 6/26/17.
  */

import scala.io.Source
import java.io._

import fastparse.all._

import scala.xml._
import commons._

/**
import scala.collection.JavaConverters._
*/




class SecondParsing {

  def Parser2(str: String): TraductionFrançaise = {
    val UP = new UnitParser()

    val MotFrançais = P(UP.LowerCaseLetter.rep(sep = "'").!)
    val ComplementMot = P(("(" ~ "'".? ~ UP.LowerCaseLetter.rep(sep = "'") ~ "'".? ~ ")").!)

    val DeuxiemeParser: Parser[(String,Option[String],String,Seq[String])] = P(MotFrançais.! ~ " ".? ~ ComplementMot.!.?
      ~ ", ".? ~ UP.Abreviation.?.! ~ " : ".? ~ UP.Definitions.!.rep)




    DeuxiemeParser.parse(str) match {
      case Parsed.Success(("", _, _, _), _) =>
        TraductionFrançaise("", None, "", Seq(Seq("")), Seq(""))
      case Parsed.Success((str1: String, str2: Option[String], str3: String, seq: Seq[String]), _) =>
        TraductionFrançaise(str1, str2, str3, for (elements <- seq) yield {
          elements.split("[,;]").toSeq
        },seq)

      case f: Parsed.Failure =>
        TraductionFrançaise(s"Failure $str \n ${f.extra.traced.trace}", None, s"${f.index}", Seq(Seq("")), Seq(""))

      case Parsed.Success(_, _) =>
        TraductionFrançaise("Error",None, "", Seq(Seq("")), Seq(""))
    }
  }

}



object SecondParsing {


  def main(args: Array[String]): Unit = {
/*/people/khamphousone/Documents/Dictionnaires/daub_rouchi_197S_CU.txt*/
    println("Entrez le chemin du dictionnaire Dauby Rouchi")
    val path = scala.io.StdIn.readLine()
    val buff: Source = Source.fromFile(path)
    val Parsing = new SecondParsing()

    val tradtoxml = new toXML()


    val liste1 = for (line <- buff.getLines.slice(7456, 9650)) yield {
      Parsing.Parser2(line)
    }

    val liste2 = liste1.toList
    for (elements <- 'é'::('a' to 'z').toList) {
      val listTrad = for (trad <- liste2 if
      List(s"$elements", s"* $elements").exists(trad.mot.startsWith)) yield {

            val Traduction = new tradtoxml.Trad2(trad.mot, trad.complement,
              trad.abreviation, trad.traduction, trad.lexie)
            Traduction.toXml


        }

      val listXml = <Nomenclature>
        {listTrad}
      </Nomenclature>
      val dir = new File("./XMLSecondParser")
      dir.mkdir
      XML.save(s"./XMLSecondParser/$elements", listXml, "utf-8", true, null)
    }
    println("SecondParsing.scala DONE")
  }
}










