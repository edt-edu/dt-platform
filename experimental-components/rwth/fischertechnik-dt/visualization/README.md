# Fischertechnik visualization

With Gradle 7.6, start this visualization dashboard via
`gradle frontend:run` and
`gradle backend:bootRun`

Open http://localhost:4200/gui/TopicVisualization to see charts of the MQTT Topics and http://localhost:4200/gui/Start for a graphical representation of the current state.

To replay the sample MQTT data, install [MQTT Recorder](https://github.com/rpdswtk/mqtt_recorder) and execute
`mqtt-recorder --file backend/data/simple_process.csv --mode replay --host localhost` or
`python -m mqtt_recorder --file backend/data/simple_process.csv --mode replay --host localhost`