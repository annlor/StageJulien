<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="icon" href="favicon.ico">

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

		
        <!-- load jquery library -->
        <script src="./jQuery/jquery-ui.js"></script>
        <!-- load jquery ui js file -->
        <script src="./jQuery/jquery-ui.min.js"></script>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
  <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
<?php
/* ARC2 static class inclusion */ 
  include_once('semsol/ARC2.php'); 
$dbpconfig = array(
  "remote_store_endpoint" => "http://vmrestaure:3030/restaure/sparql",
   );
$store = ARC2::getRemoteStore($dbpconfig); 


$query = 'PREFIX ontolex: <http://www.w3.org/ns/lemon/ontolex#>
PREFIX restaure: <http://restaure.limsi.fr/2017/rdf#>
SELECT ?object1
WHERE {
    ?x ontolex:writtenRep ?object1.
    ?y ontolex:lexicalForm ?x
}';

/* execute the query */
  $rows = $store->query($query, 'rows'); 
 

$stack = [];

foreach($rows as $row) {
	$stack[] = $row['object1'];

}


?>
     <script>
        $(function() {
            var availableTags = <?php echo json_encode($stack); ?>;

		    $( "#inputpicard" ).autocomplete({
      source: availableTags,
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
  </script>

	
  </head>


  <body>



<script>
   function changeValue(o){
     document.getElementById('inputpicard').value=o.innerHTML;
    }

</script>
<script type="text/javascript">
var base = <?php echo json_encode($stack); ?>;


function check(field) {
  var name = field.value;
  var l = name.length;
  var last = name;
  function AC_indexOf()
  {
    var ctr=0;
    for(var i = 0; i < base.length; i++)
    {
      var next = base[i];
      if(name==next) return 1;
      if(name==next.substr(0, l)) { last=next; ctr++;}
    }
    return ctr;
  }
  var ctr = AC_indexOf();
  if(ctr != 1) return;
  field.value = last;
  var content = last + " trouvé.";
  document.getElementById("storage").innerHTML=content;
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

      <div class="starter-template">
        <h1>Lexique des langues du projet RESTAURE</h1>
        <p class="lead">Vous cherchez comment se dit un mot en picard, occitan ou alsacien ?</p>
      </div>
 	<form action ="action_page.php" method="get" id = "formulaire">
    <p id ="IntroAndCount">Indiquez ce mot :</p><div class="ui-widget">
<button type="reset" value="Reset" class="btn btn-secondary">Effacer</button>
    <input type="text" id ="inputpicard" name="mot" required value="Tapez un mot..." onKeyUp="check(this)" onChange="check(this)" onclick="changeValue(this)" >
    <button type ="submit" class="btn btn-success">Soumettre</button> </div>
  	</form>

    </div><!-- /.container -->




    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->

    <script>window.jQuery || document.write('<script src="bootstrap/docs/assets/js/vendor/jquery.min.js"><\/script>')</script>
    <script src="bootstrap/docs/dist/js/bootstrap.min.js"></script>
    <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
    <script src="bootssrap/docs/assets/js/ie10-viewport-bug-workaround.js"></script>
  </body>
</html>
