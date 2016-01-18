#!/bin/bash

java -cp wiremock-manual-example-1.0-SNAPSHOT-jar-with-dependencies.jar -Dweather.application.properties=./weather-application.properties com.wbsoftwareconsutlancy.WeatherApplication
