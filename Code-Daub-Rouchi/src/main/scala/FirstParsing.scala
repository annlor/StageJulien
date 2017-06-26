/**
  * Created by khamphousone on 6/22/17.
  */

import scala.io.Source
/**
import java.io._
*/
import fastparse.all._

import scala.xml._
import commons.UnitParser

import scala.collection.JavaConverters._

/**
  * Cette case classe contriendra les éléments de chaque ligne
  */
case class TraductionPicard(mot : String, abreviation : String, definitions : Seq[String])
case class ListTraductionPicard(ListTraductions : List[TraductionPicard])

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

    val MotPicard = P(("* ".? ~ UP.Letters.rep(sep = UP.Parenthesis) ~ " ".? ~ UP.Parenthesis).!)



    val Debut: Parser[(String, String, Seq[String])] = P(MotPicard.! ~ ", ".? ~ UP.Abreviation.?.! ~ " : ".?
  ~ (UP.PoncDefinitions | UP.NumbersDef ~ UP.PoncDefinitions).!.rep)


   Debut.parse(str) match {
     case Parsed.Success(("",_,_),_) => TraductionPicard("","",Seq(""))
     case Parsed.Success((str1: String, str2: String, seq: Seq[String]), _) => TraductionPicard(str1, str2, seq)

     case f: Parsed.Failure =>  TraductionPicard(s"Failure $str \n ${f.extra.traced.trace}", s"${f.index}", Seq(""))
       /**TraductionPicard("","",Seq(""))*/

     case Parsed.Success(_,_) => TraductionPicard("Error","",Seq(""))

   }

  }
  class Trad(mot : String, abreviation : String, definitions : Seq[String]) {

    def SeqtoXml(seq: Seq[String]): Seq[Elem] = {
      val res = for (elements <- seq) yield {
        <definition>
          {elements}
        </definition>
      }
      res
    }

    def toXml : Elem =
    <TraductionPicard>
      <mot>{mot}</mot>
      <abreviation>{abreviation}</abreviation>
      {SeqtoXml(definitions)}
    </TraductionPicard>

  }
}


object Demo {


  def main(args: Array[String]): Unit = {

    val buff: Source = Source.fromFile("/people/khamphousone/Documents/Dictionnaires/daub_rouchi_197S_CU.txt")
    /**val writer = new PrintWriter(new File("/people/khamphousone/Documents/ParsersScala/FileParsers.txt"))*/
    val Parsing = new FirstParsing()
    val UP = new UnitParser()


    val liste1 = for (line <- buff.getLines.slice(1213, 7446)) yield {
      Parsing.Parser(line)
    }

    val liste2 = liste1.toList
    for (elements <- 'É'::('A' to 'Z').toList) {
      val listTrad = for (trad <- liste2 if
      List(s"$elements", s"* $elements", s"${UP.Numbers.rep(min = 1)}").exists(trad.mot.startsWith))
       yield {
         if (trad.mot.isEmpty | trad.mot.startsWith(s"${UP.Numbers.rep(min = 1)}")) <Empty/>

         else {
           val Traduction = new Parsing.Trad(trad.mot, trad.abreviation, trad.definitions)
           Traduction.toXml
         }
      }

        val listXml = <Traduction>
          {listTrad}
        </Traduction>

        XML.save(s"/people/khamphousone/Documents/ParsersScala/XML/$elements", listXml, "utf-8", true, null)
      }
  }





}
