<!DOCTYPE html>
<html>
<head>
<title>Projet RESTAURE : Lexique et carte</title>
<meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="icon" href="favicon.ico">

<link rel="stylesheet" href="https://unpkg.com/leaflet@1.1.0/dist/leaflet.css" integrity="sha512-wcw6ts8Anuw10Mzh9Ytw4pylW8+NAD4ch3lqm9lzAsTxg0GFeJgoAtxuCLREZSC5lUXdVyo/7yfsqFjQ4S+aKw==" crossorigin=""/>

<script src="https://unpkg.com/leaflet@1.1.0/dist/leaflet.js" integrity="sha512-mNqn2Wg7tSToJhvHcqfzLMU6J4mkOImSPTxVZAdo+lcPlk+GhZmYgACEe0x35K7YzW1zJ7XyJV/TT1MrdXvMcA==" crossorigin=""></script>
    <!-- Bootstrap core CSS -->
    <link href="bootstrap/docs/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
    <link href="bootstrap/docs/assets/css/ie10-viewport-bug-workaround.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="./starter-template.css" rel="stylesheet">
<script src="bootstrap/docs/assets/js/ie-emulation-modes-warning.js"></script>

<script type="text/javascript">
      var nombreOnglets = 2;
      function changeOnglet(numero)
      {
        // On commence par tout masquer
        for (var i = 0; i < nombreOnglets; i++)
          document.getElementById("contenuOnglet" + i).style.display = "none";

        // Puis on affiche celui qui a été sélectionné
        document.getElementById("contenuOnglet" + numero).style.display = "block";
      }

 </script>

	<script src="unmot.js" type="text/javascript"></script>

</head>
<body>
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

<div class="container-fluid">

      <div class="starter-template">
        <p class="lead">Vous avez cherché :</p><?php
$output = $_GET["mot"];
  echo $output;
?><br><br>

  <span onclick="changeOnglet(0)"><button type="button" class="btn btn-primary">Traductions</button></span>
<span onclick="changeOnglet(1)"><button type="button" class="btn btn-info">Carte</button>
</span>

      
    </div>

    <div id="contenuOnglet0" style="display:block;">
	<ul style="list-style-type:circle">
	<li>Picard : </li> <p id ="output"></p>
	<li>Alsacien :</li>
	<li>Occitan :</li>
	</ul>
    </div>
    <div id="contenuOnglet1" style="display:block;">

            
<div id="map">
</div>

</div> <br>

    </div><!-- /.container -->


<script src="unmot.js" type="text/javascript"></script>

<script>
	// coordonnées d'Amiens
	var amiens=[49.895, 2.3022];
	// création de la carte, centrée autour d'Amiens pour le moment
	var map = L.map('map').setView(amiens, 7);

	// création du fond de la carte
	L.tileLayer( //'https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw', {
		'https://{s}.tile.openstreetmap.fr/osmfr/{z}/{x}/{y}.png', {
		//'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
		maxZoom: 18,
		attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, ' +
			'<a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, ' +
			'Imagery © <a href="http://mapbox.com">Mapbox</a>',
		id: 'mapbox.light'
	}).addTo(map);

	function onEachFeature(feature, layer) {
	layer.bindPopup(feature.properties.popupContent);
	}

	// récupération des données JSON qui sont dans le fichier unmot.js
	// et ajout de ces données à la carte
	L.geoJSON([unmot], {

		style: function (feature) {
			return feature.properties && feature.properties.style;
		},

		onEachFeature: onEachFeature,

		pointToLayer: function (feature, latlng) {
			return L.circleMarker(latlng, {
				radius: 6,
				fillColor: "#0000ff",
				color: "#0000ff",
				weight: 1,
				opacity: 1,
				fillOpacity: 0.8
			});
		}
	}).addTo(map);


changeOnglet(0);
</script>



</body>
</html>


