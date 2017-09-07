<?php
$traductions=array();


foreach (array_unique($CoordinateWR) as $cWRar){
	$i = 0;

	echo "<div id = \"$cWRar\" class=\"list-group-item\" style=\"text-align:left;border-width:5px;border-radius: 15px;\">
		<div class=\"row\">
		<div class=\"col-sm-8\"><p style = \"font-weight:bold;font-size:150%;\">${cWRar} </p></div>
		<div class = \"col-sm-4\" style=\"text-align:left;border-width:5px;border-radius: 15px;\"></div>
		</div>";
	if (count($speechkeys[$cWRar]) > 0){
		echo "<p><span class=\"bold\">Partie du discours :</span> $speechkeys[$cWRar]</p>";
	}

	if (count($genderkeys[$cWRar]) > 0){
		echo "<p><span class=\"bold\">Genre :</span> $genderkeys[$cWRar]</p>";
	}
	/*if (count(array_filter($wrDef)) > 0 && $cWRar =='Valenciennes')  {
		echo "<p style=\"font-weight:bold;\"> ${str2} </p>";
		foreach($wrDef as $value){
			$content = "<p class =\"Valenciennes\"> $value</p>";
			echo $content;
			$traductions['Valenciennes'].= $content;
			$i = 1;
		}
	}	*/
	if (count(array_filter($wrDef)) > 0){
		foreach (array_filter($wrDefkeys) as $key => $value) {
			if ($value == $cWRar && $i == 0){
				echo "<p style=\"font-weight:bold;\"> ${str2} </p>";
				if (strlen(trim($value)) > 2 && !empty($key) && strlen(trim($key)) > 3){
					if(!array_key_exists($value,$traductions))
						$traductions[$value]="";
					$content="<p class =\"$value\">$key</p>";
					echo $content;
					$traductions[$value].=$content;

				}
			}		
		}
	}

	if (count(array_filter($verbatim)) > 0 && $cWRar == 'Amiens'){
		echo "<p style=\"font-weight:bold;\">${str2}</p>";
		foreach($verbatimkeys as $key => $value){
			if ($value == $cWRar){
				echo "<p>".$key."</p>";
				$traductions[$cWRar].=$key;
			}
		}
	}

	if ($cWRar == 'Valenciennes'){
		if(count(array_filter($wrExPicard))>1){
			echo "<p style=\"font-weight:bold;\"> ${str3} </p>";
			echo '<ul>';
			echo '<li>' . implode( '</li><li>', array_filter($wrExPicard)) . '</li>';
			echo '</ul><br>';
		}
		else{
			echo "<p style=\"font-weight:bold;\"> ${str3} </p>";
			echo implode('<br>',array_filter($wrExPicard));
		}
		if (count(array_filter($ANC)) >0 && strlen(implode(array_filter($ANC))) > 1){
			echo "<p style = \"font-weight:bold;\">${str4} </p>".implode($ANC)."<br>";
		}

		if (count(array_filter($wrLink)) >0){
			echo "<p style = \"font-weight:bold;\">${strLink} </p>".implode(', ',$wrLink)."<br>";
		}
	}



echo '</div><br>';
}

foreach ($dataLinks as $row){
	echo "<div class=\"list-group-item\" style=\"text-align:left;font-style: italic;border-radius: 15px;\">";
	if ($row[CoordinateWR] == 'Valenciennes'){
		echo "<p  style = \"font-weight:bold;font-size:140%;\"> $row[wrPicard]</p>";
	$traductions["Valenciennes"].= "<p class =\"Valenciennes\" style=\"font-style: italic;\"> $row[wrPicard]</p>";
	}
	else if ($row[CoordinateWR] == 'Amiens'){
		echo "<p  style = \"font-weight:bold;font-size:140%;\"> $row[wrPicard]</p>";
	$traductions["Amiens"].= "<p class =\"Amiens\" style=\"font-style: italic;\"> $row[wrPicard]</p>";
	}
	if (count($row[gender2]) > 0){
		echo "<p><span class=\"boldsmall\">Partie du discours :</span> $row[speech2]</p>";
	}
	
	if (count($row[speech2])>0){
		echo "<p><span class=\"boldsmall\">Genre :</span> $row[gender2]</p>";
	}
	if(count($row[wrTraductionFrancaiseConcat])>0){
		echo "<p style=\"font-weight:bold;\"> Traduction Française :</p><p>".$row[wrTraductionFrancaiseConcat]."</p>";
	}
	if(count($row[wrExPicardConcat])>0){
		echo "<p style=\"font-weight:bold;\"> Exemple Picard : </p><p>".$row[wrExPicardConcat]."</p>";
	}
	if(count($row[ANCConcat])>0 && strlen($row[ANCConcat]) > 2){
		echo "<p style=\"font-weight:bold;\"> Ancienne étymologie : </p><p>".$row[ANCConcat]."</p>";
	}
	echo "</div>";
}



?>
