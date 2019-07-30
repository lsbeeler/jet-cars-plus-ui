function requestStart( )
{
    var startEndpointUrl = "http://localhost:8080/iotdemo_war_exploded/start";
    var requestJSON = JSON.stringify({ "actionId": "START" });

    var xhr = new XMLHttpRequest( );
    xhr.open("POST", startEndpointUrl);
    xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    xhr.send(requestJSON);
}

var map = null;

function initMap( )
{
    map = new google.maps.Map(document.getElementById('map'), {
        center: {lat: 38.9296165, lng: -77.2478723},
        zoom: 14
    });
}

var markers = [ ];

function refreshMap( )
{
    var geolocationEndpointUrl = "http://localhost:8080/iotdemo_war_exploded/geolocation";

    var xhr = new XMLHttpRequest( );
    xhr.open("GET", geolocationEndpointUrl);
    xhr.onreadystatechange = function (ev) {
        if (xhr.readyState == 4 && xhr.status == 200)
            ParseGeolocationResponse(xhr);
    };
    xhr.send(null);
}

function ParseGeolocationResponse(xhr)
{
    var responseObject = JSON.parse(xhr.responseText);

    for (var i = 0; i < responseObject.length; i++) {
        var driverId = responseObject[i].driverId;
        var name = "Vehicle " + driverId;
        var latitude = responseObject[i].latitude;
        var longitude = responseObject[i].longitude;

        var gPos = new google.maps.LatLng(latitude, longitude);


        if (markers[driverId]) {
            markers[driverId].setPosition(gPos);
        } else {
            markers[driverId] = new google.maps.Marker({
                position: gPos,
                title: name,
                icon: "http://maps.google.com/mapfiles/kml/shapes/cabs.png"
            });
        }

        markers[driverId].setMap(map);
    }
}

var MAP_REFRESH_INTERVAL_MSEC = 67;

setInterval(refreshMap, MAP_REFRESH_INTERVAL_MSEC);
