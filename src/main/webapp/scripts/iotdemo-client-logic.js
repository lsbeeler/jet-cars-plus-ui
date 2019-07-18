function requestStart( )
{
    var startEndpointUrl = "http://localhost:8080/iotdemo_war_exploded/start";
    var requestJSON = JSON.stringify({ "actionId": "START" });

    var xhr = new XMLHttpRequest( );
    xhr.open("POST", startEndpointUrl);
    xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    xhr.send(requestJSON);
}
