/**
  * Created by khamphousone on 6/22/17.
  */

import java.io.File

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
     case Parsed.Success(("",_,_),_) => TraductionPicard("","",Seq(""))
     case Parsed.Success((str1: String, str2: String, seq: Seq[String]), _) =>
       TraductionPicard(str1, str2, seq)

     case f: Parsed.Failure =>
       TraductionPicard(s"Failure $str \n ${f.extra.traced.trace}", s"${f.index}", Seq(""))
       /**TraductionPicard("","",Seq(""))*/

     case Parsed.Success(_,_) => TraductionPicard("Error","",Seq(""))

   }

  }

}


object Demo {


  def main(args: Array[String]): Unit = {

    /** /!\ Nom du path à changer si compilation sur autre machine */
    val buff: Source = Source.fromFile("/people/khamphousone/Documents/Dictionnaires/daub_rouchi_197S_CU.txt")
    /**val writer = new PrintWriter(new File("/people/khamphousone/Documents/ParsersScala/FileParsers.txt"))*/
    val Parsing = new FirstParsing()
    val UP = new UnitParser()
    val tradtoxml = new toXML()

    val liste1 = for (line <- buff.getLines.slice(1213, 7446)) yield {
      Parsing.Parser(line)
    }

    val liste2 = liste1.toList
    for (elements <- 'É'::('A' to 'Z').toList) {

      val listTrad = for (trad <- liste2 if
      List(s"$elements", s"* $elements").exists(trad.mot.startsWith)) yield {
           val Traduction = new tradtoxml.Trad(trad.mot, trad.abreviation, trad.definitions)
           Traduction.toXml
         }


        val listXml = <Nomenclature>
          {listTrad}
        </Nomenclature>
        val dir = new File("./XMLFirstParser")
        dir.mkdir
        XML.save(s"./XMLFirstParser/$elements", listXml, "utf-8", true, null)
      }
  println("FirstParsing DONE")
  }





}
