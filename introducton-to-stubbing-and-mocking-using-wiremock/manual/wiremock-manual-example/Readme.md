# Record
java -jar wiremock-1.57-standalone.jar --proxy-all="https://api.forecast.io" --record-mappings --verbose

# Playback
java -jar wiremock-1.57-standalone.jar --verbose

