/**
 * Root path of all backend RESTful servlet URLs
 *
 * @constant
 */
var ENDPOINT_URL_ROOT = "http://localhost:8080/iotdemo_war_exploded";

/**
 * Google Maps widget animation refresh interval, in milliseconds.
 * Set by default to 1/15th of a second (i.e., 15 frames per second)
 * to achieve relatively smooth motion.
 *
 * @constant
 */
var MAP_REFRESH_INTERVAL_MSEC = Math.floor(1000 / 15);

/**
 * Refresh interval, in milliseconds, of the Plotly horizontally scrolling
 * time-series line chart of average vehicle speeds. Like the Google Maps
 * widget, is set to 15 frames per second by default for relatively smooth
 * animation.
 *
 * @constant
 */
var AVERAGE_SPEED_CHART_REFRESH_INTERVAL_MSEC = Math.floor(1000 / 15);

/**
 * Violations table widget data refresh interval, in milliseconds.
 * Set by default to 1/10th of a second (i.e., 10 frames per second).
 * This is less than the 15 frames per second update interval of the
 * Google Maps widget and the Plotly chart widget since smooth
 * animation isn't called for here -- we just want fresh data
 * reasonably quickly.
 *
 * @constant
 */
var VIOLATIONS_TABLE_REFRESH_INTEVAL_MSEC = Math.floor(1000 / 15);

/**
 * Maximum number of average speed data points to show simultaneously
 * on the Plotly.js side-scrolling time series plot. Higher numbers
 * will result in smoother motion but a messier plot. The best results
 * I've seen occur when this value is set between 12 and 32.
 *
 * @constant
 */
var MAX_AVERAGE_SPEED_DATA_POINTS = 24;

/**
 * The Google Maps widget
 */
var gMap = null;

/**
 * The set of vehicle markers drawn on top of the Google Maps widget
 */
var gMarkers = [ ];

/**
 * The tabulator.js widget that constitutes the driver information and
 * violations count table.
 */
var gViolationsTable = new Tabulator("#violations_table", {
    height: 400,
    columns: [
        {title: "Driver", field: "driverId"},
        {title: "Violation Count", field: "violationCount"},
        {title: "Age", field: "driverAge"},
        {title: "Gender", field: "driverGender"},
        {title: "Make", field: "vehicleMake"},
        {title: "Model", field: "vehicleModel"},
        {title: "Year", field: "vehicleYear"}
    ]
});

/**
 * Key-value table of average speed data that is fetched regularly from
 * the AverageSpeedServlet on the server side. The keys are strings that
 * uniquely identify drivers/vehicles (e.g., "Driver 86") and the values
 * are arrays that represent time-series average speed data.
 */
var gAverageSpeedData = { };

/**
 * Requests that the backend Tomcat server start the Hazelcast Jet pipeline.
 * Should be invoked as the "onclick" action of the "Start" button in the
 * web UI.
 */
function RequestStart( )
{
    var startEndpointUrl = ENDPOINT_URL_ROOT + "/start";
    var requestJSON = JSON.stringify({ "actionId": "START" });

    var xhr = new XMLHttpRequest( );
    xhr.open("POST", startEndpointUrl);
    xhr.setRequestHeader("Content-Type", "application/json");

    xhr.send(requestJSON);
}

/**
 * The Google Maps map widget initialization callback. Must be specified
 * as part of the Google Maps JavaScript library import in the HTML for the
 * main application page. Sets the map focus location and zoom level.
 */
function InitMap( )
{
    gMap = new google.maps.Map(document.getElementById('map'), {
        center: {lat: 38.923769, lng: -77.236934},
        zoom: 15
    });
}

/**
 * Refreshes the location of vehicles on the Google Maps widget. Should
 * generally be called every 1/15th of a second or more (i.e., 15 frames
 * per second) to achieve a smooth animation of the map.
 */
function RefreshMap( )
{
    var geolocationEndpointUrl = ENDPOINT_URL_ROOT + "/geolocation";

    var xhr = new XMLHttpRequest( );
    xhr.open("GET", geolocationEndpointUrl);
    xhr.onreadystatechange = function (ev) {
        if (xhr.readyState == 4 && xhr.status == 200)
            ParseGeolocationResponse(xhr);
    };
    xhr.send(null);
}

/**
 * Given an AJAX request object that represents the response of the
 * backend Tomcat server to a geolocation query, parse the query,
 * and update on-map positions of the vehicle markers. This is function
 * that actually causes the vehicle icons to move on the map.
 *
 * @param xhr an XMLHttpReqest object representing an HTTP
 *            request-response transaction run against backend's
 *            geolocation servlet
 */
function ParseGeolocationResponse(xhr)
{
    var responseObject = JSON.parse(xhr.responseText);

    for (var i = 0; i < responseObject.length; i++) {
        var driverId = responseObject[i].driverId;
        var name = "Vehicle " + driverId;
        var latitude = responseObject[i].latitude;
        var longitude = responseObject[i].longitude;

        var gPos = new google.maps.LatLng(latitude, longitude);


        if (gMarkers[driverId]) {
            gMarkers[driverId].setPosition(gPos);
        } else {
            gMarkers[driverId] = new google.maps.Marker({
                position: gPos,
                title: name,
                icon: "http://maps.google.com/mapfiles/kml/shapes/cabs.png"
            });
        }

        gMarkers[driverId].setMap(gMap);
    }
}

/**
 * Refreshes the Tabulator.js driver information and policy violations widget.
 * Should be invoked regularly on a timer to ensure continues updates
 */
function RefreshViolationsTable( )
{
    var policyEndpointUrl = ENDPOINT_URL_ROOT + "/policy";
    var xhr = new XMLHttpRequest( );
    xhr.open("GET", policyEndpointUrl);
    xhr.onreadystatechange = function (ev) {
        if (xhr.readyState == 4 && xhr.status == 200) {
            var responseObject = JSON.parse(xhr.responseText);
            gViolationsTable.setData(responseObject);
        }
    };
    xhr.send(null);
}

/**
 * Utility function that shuffles down the contents of an array, shifting
 * every element one index location to the left and discarding the
 * element at index 0. Used to maintain continually refreshed, time-series
 * data for the Plotly.js rolling average chart.
 *
 * @param array the array to shuffle down
 */
function ShuffleDown(array)
{
    for (var i = 1; i < array.length; i++)
        array[i - 1] = array[i];
}

/**
 * Refreshes the Plotly.js side-scrolling, average speed time-series
 * chart by fetching the latest data from the average speed servlet on
 * the backend. Should be called at intervals of 1/15th of a second or so
 * to ensure regular animation updates. Parses the response data from
 * the server and redraws the Plotly chart on-screen after the request
 * to fetch new data from the server returns.
 */
function RefreshAverageSpeedChart( )
{
    var avgSpeedEndpointUrl = ENDPOINT_URL_ROOT + "/speed";
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

/**
 * Given the response body of an HTTP request-response transaction run against
 * the average speed servlet on the backend, parse the response returned
 * by the server and update the average speed data tables.
 */
function ParseAverageSpeedUpdate(response)
{
    var updateArray = JSON.parse(response);
    for (var i = 0; i < updateArray.length; i++) {
        var currDriverId = updateArray[i].driverId;

        if (!gAverageSpeedData.hasOwnProperty(currDriverId)) {
            gAverageSpeedData[currDriverId] = [ ];
        }

        if (gAverageSpeedData[currDriverId].length < MAX_AVERAGE_SPEED_DATA_POINTS) {
            gAverageSpeedData[currDriverId].push(updateArray[i].averageSpeed);
        } else {
            ShuffleDown(gAverageSpeedData[currDriverId]);
            var len = gAverageSpeedData[currDriverId].length;
            gAverageSpeedData[currDriverId][len - 1] = updateArray[i].averageSpeed;
        }
    }
}

/**
 * Redraws the Plotly.js side-scrolling, average speed time series data chart
 * based on the contents of the global 'gAverageSpeedData' table. This
 * function should be called whenever that data is updated.
 */
function RedrawAverageSpeedChart( )
{
    var plotDiv = document.getElementById("speed-chart");

    var xAxis = [ ];
    for (var i = 0; i < MAX_AVERAGE_SPEED_DATA_POINTS; i++)
        xAxis.push(i + 1);

    var traces = [ ];
    for (prop in gAverageSpeedData) {
        if (gAverageSpeedData.hasOwnProperty(prop)) {
            var trace = { };
            trace.x = xAxis;
            trace.y = [ ];
            trace.name = prop;

            var len = gAverageSpeedData[prop].length;
            for (var j = 0; j < len; j++)
                trace.y.push(gAverageSpeedData[prop][j]);
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

setInterval(RefreshMap, MAP_REFRESH_INTERVAL_MSEC);
setInterval(RefreshViolationsTable, VIOLATIONS_TABLE_REFRESH_INTEVAL_MSEC);
setInterval(RefreshAverageSpeedChart,
    AVERAGE_SPEED_CHART_REFRESH_INTERVAL_MSEC);
