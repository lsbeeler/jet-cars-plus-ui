# Hazelcast Connected Car Demo

## Overview

This demo combines the [Hazelcast Jet](https://jet.hazelcast.org/) stream
processing engine with the [Hazelcast IMDG](https://hazelcast.org/)
in-memory data grid to demonstrate how data from IoT sensors can be aggregated
and enriched to create rolling-average, time-series dashboards and to make
business policy decisions.

It uses data from the [US Department of Transportation's Advanced Messaging
Concept Probe (AMCP) data set](https://catalog.data.gov/dataset/advanced-messaging-concept-development-probe-vehicle-data).

## Packaging

The demo embeds Hazelcast Jet and IMDG inside of an Apache Tomcat application
server. The application server serves up a modern, HTML5 web user interface
with [Google Maps](https://cloud.google.com/maps-platform/),
[tabulator.js](http://tabulator.info/), and [Plotly](https://plot.ly/) widgets.

The github repo for this project contains the source code and a complete Tomcat
runtime. To make running the demo as easy as possible, the source code has
already been built and packaged as a WAR file and this WAR has been deployed into
the included Tomcat runtime. You don't need to deploy any WAR artifacts
yourself. Just clone the repo and use the included control scripts to start and
stop the demo.

## Getting the Demo

Getting the demo is as simple as executing:

```bash
git clone https://github.com/lsbeeler/jet-cars-plus-ui.git
```

## Preparing to Run the Demo

**NOTE:** In order to run the demo, you will need a Google Maps API key. If
you're a Hazelcast employee, ping @lucasbeeler on Slack and I'll happily share
mine with you. Once you have a Google Maps API key, you will need to paste it
into the static HTML for the main application page. The procedure for this is
simple. After cloning the repository, navigate into the repository root
directory, and edit the file
`runtime/base/webapps/iotdemo/index.html` in your favorite editor. On line 12,
you will see the HTML markup:

```html
<script src="https://maps.googleapis.com/maps/api/js?key=XXX&callback=InitMap"
```
Simply replace the `XXX` after `key=` with your Google Maps API key.

## Running the Demo

Running the Demo is simple. Ensure you've cloned the repo and added your
Google Maps API key as per the instructions above. Next, navigate into the
cloned repo root directory and execute:

```bash
./start-demo.sh
```

This will launch the embedded Tomcat runtime. After Tomcat has started,
point your browser to
[http://localhost:8080/iotdemo/](http://localhost:8080/iotdemo) and press the
"Start" button in the upper-left-hand corner of the page. The Jet pipeline may
take a few moments so spin up, so be patient. Once car icons appear on the
Google Maps widget, the demo is running.

To stop the demo, simply execute:

```bash
./stop-demo.sh
```

from the cloned repo root directory.
