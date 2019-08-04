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
        center: {lat: 38.923769, lng: -77.236934},
        zoom: 15
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

var ViolationsTable = new Tabulator("#violations_table", {
    height: 400,
    columns: [
        {title: "Driver ID", field: "driverId"},
        {title: "Violation Count", field: "violationCount"}
    ]
     });

function RefreshViolationsTable( )
{

    var policyEndpointUrl = "http://localhost:8080/iotdemo_war_exploded/policy";
    var xhr = new XMLHttpRequest( );
    xhr.open("GET", policyEndpointUrl);
    xhr.onreadystatechange = function (ev) {
        if (xhr.readyState == 4 && xhr.status == 200) {
            var responseObject = JSON.parse(xhr.responseText);
            ViolationsTable.setData(responseObject);
        }
    };
    xhr.send(null);
}

var AverageSpeedData = { };
var MAX_AVERAGE_SPEED_DATA_POINTS = 16;

function ShuffleDown(array)
{
    for (var i = 1; i < array.length; i++)
        array[i - 1] = array[i];
}

function RefreshAverageSpeedChart( )
{
    var avgSpeedEndpointUrl = "http://localhost:8080/iotdemo_war_exploded/speed";
    var xhr = new XMLHttpRequest( );
    xhr.open("GET", avgSpeedEndpointUrl);
    xhr.onreadystatechange = function (ev) {
        if (xhr.readyState == 4 && xhr.status == 200) {
            ParseAverageSpeedUpdate(xhr.responseText);
            RedrawAverageSpeedChart( );
        }
    };
    xhr.send(null);
}

function ParseAverageSpeedUpdate(response)
{
    var updateArray = JSON.parse(response);
    for (var i = 0; i < updateArray.length; i++) {
        var currDriverId = updateArray[i].driverId;

        if (!AverageSpeedData.hasOwnProperty(currDriverId)) {
            AverageSpeedData[currDriverId] = [ ];
        }

        if (AverageSpeedData[currDriverId].length < MAX_AVERAGE_SPEED_DATA_POINTS) {
            AverageSpeedData[currDriverId].push(updateArray[i].averageSpeed);
        } else {
            ShuffleDown(AverageSpeedData[currDriverId]);
            var len = AverageSpeedData[currDriverId].length;
            AverageSpeedData[currDriverId][len - 1] = updateArray[i].averageSpeed;
        }
    }
}

function RedrawAverageSpeedChart( )
{
    var plotDiv = document.getElementById("speed-chart");

    var xAxis = [ ];
    for (var i = 0; i < MAX_AVERAGE_SPEED_DATA_POINTS; i++)
        xAxis.push(i + 1);

    var traces = [ ];
    for (prop in AverageSpeedData) {
        if (AverageSpeedData.hasOwnProperty(prop)) {
            var trace = { };
            trace.x = xAxis;
            trace.y = [ ];
            trace.name = prop;

            var len = AverageSpeedData[prop].length;
            for (var j = 0; j < len; j++)
                trace.y.push(AverageSpeedData[prop][j]);
            trace.type = "scatter";

            traces.push(trace);
        }
    }

    var layout = {
      yaxis: {
          title: "Average Speed",
          range: [0, 50]
      },

      xaxis: {
        range: [0, MAX_AVERAGE_SPEED_DATA_POINTS]
      }
    };

    Plotly.newPlot(plotDiv, traces, layout);
}

var MAP_REFRESH_INTERVAL_MSEC = 67;
var VIOLATIONS_TABLE_REFRESH_INTEVAL_MSEC = 100;
var AVERAGE_SPEED_CHART_REFRESH_INTERVAL_MSEC = 67;

setInterval(refreshMap, MAP_REFRESH_INTERVAL_MSEC);
setInterval(RefreshViolationsTable, VIOLATIONS_TABLE_REFRESH_INTEVAL_MSEC);
setInterval(RefreshAverageSpeedChart, VIOLATIONS_TABLE_REFRESH_INTEVAL_MSEC);
