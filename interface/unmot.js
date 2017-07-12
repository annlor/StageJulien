var unmot = {
    "type": "FeatureCollection",
    "features": [
        {
            "geometry": {
                "type": "Point",
                "coordinates": [
                    2.3022,
                    49.895
                ]
            },
            "type": "Feature",
            "properties": {
                "popupContent": "Amiens: " + <?php echo json_encode($_GET["mot"]); ?>
            },
            "id": 51
        },
        
    ]
};
