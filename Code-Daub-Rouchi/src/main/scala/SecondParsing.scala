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

    val MotFrançais = P(UP.LowerCaseLetter.!)
    val ComplementMot = P(("(" ~ "'".? ~ UP.LowerCaseLetter.rep(sep = "'") ~ "'".? ~ ")").!)

    val DeuxiemeParser = P(MotFrançais.! ~ " ".? ~ ComplementMot.!.?
      ~ ", ".? ~ UP.Abreviation.?.! ~ " : ".? ~ UP.PoncDefinitions.!.rep)

    DeuxiemeParser.parse(str) match {
      case Parsed.Success(("", _, _, _), _) =>
        TraductionFrançaise("", None, "", Seq(""))
      case Parsed.Success((str1: String, str2: Option[String], str3: String, seq: Seq[String]), _) =>
        TraductionFrançaise(str1, str2, str3, seq)

      case f: Parsed.Failure =>
        TraductionFrançaise(s"Failure $str \n ${f.extra.traced.trace}", None, s"${f.index}", Seq(""))

      case Parsed.Success(_, _) =>
        TraductionFrançaise("Error",None, "", Seq(""))
    }
  }

}



object Demo2 {


  def main(args: Array[String]): Unit = {

    /** /!\ Nom du path à changer si compilation sur autre machine */
    val buff: Source = Source.fromFile("/people/khamphousone/Documents/Dictionnaires/daub_rouchi_197S_CU.txt")
    /**
    val writer = new PrintWriter(new File("/people/khamphousone/Documents/ParsersScala/XML/SecondParser/FileParsers.txt"))
    */
    val Parsing = new SecondParsing()

    val tradtoxml = new toXML()


    val liste1 = for (line <- buff.getLines.slice(7456, 9650)) yield {
      Parsing.Parser2(line)
    }

    val liste2 = liste1.toList
    for (elements <- 'é'::('a' to 'z').toList) {
      val listTrad = for (trad <- liste2 if
      List(s"$elements", s"* $elements").exists(trad.mot.startsWith)) yield {

            val Traduction = new tradtoxml.Trad2(trad.mot, trad.complement, trad.abreviation, trad.traduction)
            Traduction.toXml


        }

      val listXml = <Nomenclature>
        {listTrad}
      </Nomenclature>
      val dir = new File("./XMLSecondParser")
      dir.mkdir
      XML.save(s"./XMLSecondParser/$elements", listXml, "utf-8", true, null)
    }
  }
}










