<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="">
    <meta name="author" content="">


    <title>Lexiques de langues régionales de France - Projet RESTAURE</title>

    <!-- Bootstrap core CSS -->
    <link href="bootstrap/docs/dist/css/bootstrap.min.css" rel="stylesheet">



    <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
    <link href="bootstrap/docs/assets/css/ie10-viewport-bug-workaround.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="./starter-template.css" rel="stylesheet">

	<style>
  .ui-autocomplete {
    max-height: 50%;
    overflow-y: auto;
    /* prevent horizontal scrollbar */
    overflow-x: hidden;
  }
  /* IE 6 doesn't support max-height
   * we use height instead, but this forces the menu to always be this tall
   */
  * html .ui-autocomplete {
    height: 50%;
  }
</style>
    <!-- Just for debugging purposes. Don't actually copy these 2 lines! -->
    <!--[if lt IE 9]><script src="../../assets/js/ie8-responsive-file-warning.js"></script><![endif]-->
    <script src="bootstrap/docs/assets/js/ie-emulation-modes-warning.js"></script>

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->

		


    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
  <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
<?php
/* ARC2 static class inclusion */ 
  include_once('semsol/ARC2.php'); 
echo "include ok";
$dbpconfig = array(
  "remote_store_endpoint" => "http://localhost:3030/restaureallwords/query",
   );
echo $dbpconfig;
$store = ARC2::getRemoteStore($dbpconfig); 

$query = 'PREFIX ontolex: <http://www.w3.org/ns/lemon/ontolex#>
PREFIX restaure: <http://restaure.limsi.fr/2017/rdf#>
PREFIX lime: <http://www.w3.org/ns/lemon/lime#>
SELECT DISTINCT ?FR ?PCD
WHERE {
  {
    ?x ontolex:writtenRep ?FR.
    ?y ontolex:lexicalForm ?x.
 	restaure:LexiqueDaubyRouchiFra lime:entry ?y.
  }
  UNION
  {
  ?x2 ontolex:writtenRep ?PCD.
    ?y2 ontolex:lexicalForm ?x2.
    {restaure:LexiqueDaubyRouchiPicard lime:entry ?y2.}
    UNION
    {restaure:LexiqueDebrie lime:entry ?y2}
  }
}';

/* execute the query */
  $rows = $store->query($query, 'rows'); 
 

$stack = [];

foreach($rows as $row) {
	$stackpcd[] = $row['PCD'];
	$stackfr[] = $row['FR'];

}

?>

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.12.4/css/bootstrap-select.min.css">

<!-- Latest compiled and minified JavaScript -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.12.4/js/bootstrap-select.min.js"></script>
	
  </head>


  <body>



<script>
   function changeValue(o){
     document.getElementById('inputpicard').value=o.innerHTML;
    }


</script>
    <nav class="navbar navbar-inverse navbar-fixed-top">
      <div class="container">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="index.html">RESTAURE</a>
        </div>
        <div id="navbar" class="collapse navbar-collapse">
          <ul class="nav navbar-nav">
            <li class="active"><a href="index.html">Home</a></li>
            <li><a href="#about">About</a></li>
            <li><a href="#contact">Contact</a></li>

          </ul>
        </div><!--/.nav-collapse -->
      </div>
    </nav>

    <div class="container">

      <div style ="center" class="starter-template">
        <h1 width="100%">Lexique des langues du projet  <img src="bootstrap/restaure.png" alt="LOGOREstaure" ></h1>
        <p class="lead">Vous cherchez comment se dit un mot en picard, occitan ou alsacien ?</p>
      </div>
<div id ="errors" style ="text-align:center"><br></div>
 	<form action ="action_page.php" method="post" id = "formulaire" name="myForm" onsubmit="return validateForm()">
    <p id ="IntroAndCount">Indiquez ce mot :</p><div class="ui-widget">
<select class="selectpicker show-menu-arrow" data-width="fit" onchange="getval(this);">
  <option value="1" selected="selected">Français</option>
  <option data-divider="true"></option>
  <option value="2">Picard</option>
  <option value="3">Occitan</option>
  <option value="4">Alsacien</option>
</select>
    <input type="text" id ="inputpicard" name="mot" required value="Tapez un mot..." onclick="changeValue(this)" size = "30">
    <button type ="submit" class="btn btn-success">Soumettre</button> </div>
  	</form>

    </div>
<!-- /.container -->

     <script>
function getval(sel){
var availableTags = [];
if (sel == "load"){
    availableTags = <?php echo json_encode(array_filter($stackfr)); ?>;
}
else if (sel.value == "1"){
	availableTags = <?php echo json_encode(array_filter($stackfr)); ?>;
}
else if (sel.value == "2"){
	availableTags = <?php echo json_encode(array_filter($stackpcd)); ?>;
}


        $(function() {


		    $( "#inputpicard" ).autocomplete({
autoFocus: true,

      source: function(request, response) {

var matches = $.map(availableTags, function (acItem) {
            if (acItem.toUpperCase().indexOf(request.term.toUpperCase()) === 0) {
                return acItem;
            }
        });
        response(matches.slice(0,100));	

    },

open: function(event,ui){
    var len = $('.ui-autocomplete > li').length;
if (len >= 100){
document.getElementById("IntroAndCount").innerHTML = 'Il y a plus de 100 résultats';
}
else{
document.getElementById("IntroAndCount").innerHTML = 'Il y a '+len+' résultat(s)';
}
  }
    });
  } );
}

getval("load");
  </script>
<script>
function validateForm() {
	var availableTags = <?php echo json_encode(array_merge(array_filter($stackfr),array_filter($stackpcd))); ?>;

    var x = document.forms["myForm"]["mot"].value;
    if (availableTags.indexOf(x) < 0) {
document.getElementById('errors').innerHTML="Le mot entré n'est pas présent dans notre liste";
        return false;
    }
}

</script>


    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->

    <script>window.jQuery || document.write('<script src="bootstrap/docs/assets/js/vendor/jquery.min.js"><\/script>')</script>
    <script src="bootstrap/docs/dist/js/bootstrap.min.js"></script>
    <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
    <script src="bootstrap/docs/assets/js/ie10-viewport-bug-workaround.js"></script>
  </body>
</html>
