package Config

class Configuration {

    /*Chemin du dictionnaire Dauby-Rouchi*/
    val pathDaubyRouchi = "./Dictionnaires/daub_rouchi_197S_CU.txt"

    /*Chemin du dictionnaire René Debrie*/
    val pathReneDebrie = "./Dictionnaires/a_debr_oues_84S_A_utf8.txt"

    /*Chemin de sortie du dictionnaire René Debrie modifié, du parser ParserDebrieV2*/
    val pathOutputReneDebrie = "./Dictionnaires/DebrieModif.txt"

    /*Chemin contenant le dossier XML de sortie du programme FirstParsing*/
    val pathOutputFirstParsing = "./XMLFirstParser/"

    /*Chemin contenant le GLAWI filtré*/
    val pathGLAWI =  "./Dictionnaires/GLAWImodif.txt"

    /*Chemin contenant l'XML du deuxième parser*/
    val pathXMLtoRDFSecondParser= "./XMLSecondParser/all.xml"

    /*Chemin contenant le RDF sorti pour le deuxième parser*/
    val pathOutputRDFSecondParser ="./RDFSecondParser/RDFresult.ttl"

    /*Chemin contenant le résultat du parser René Debrie version 2.0*/
    val pathOutputXMLDebrie2 = "./XMLDebrieV2/ResultDebrie"

    /*Chemin contenant le RDF sorti pour le parser Debrie*/
    val pathOutputRDFReneDebrie = "./RDFDebrie/RDFDebrieResults.ttl"

    /*Chemin l'entrée du programme générant le RDF à partir du XML du premier parser*/
    val pathInputXMLFirstParsingEnrichment = "./XMLFirstParser/Enrichment/TEI"

    /*Chemon contenant la sortie du RDF pour le premier parser*/
    val pathOutputRDFFrstParsing = "./RDFFirstParser/"
}
