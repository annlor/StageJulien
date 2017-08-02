package languagedetector

import org.apache.tika.langdetect.OptimaizeLangDetector

object Mainlanguagedetector{

  def main(args:Array[String]):Unit={
    println("Entrez la CharSeq :")
    var path=scala.io.StdIn.readLine()
    val LD = new OptimaizeLangDetector
    LD.loadModels()
    LD.setShortText(true)
    var res = LD.detect(path).getRawScore
    var res2 = LD.detect(path).getLanguage
    println(res + res2)
  }
}