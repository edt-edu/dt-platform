package org.gemoc.mbdo.gateway.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MqttToKafkaService {

    private KafkaProducer<String, String> producer = null;

    public MqttToKafkaService(GatewayService gatewayService) {

        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, gatewayService.getGatewayServiceConfiguration().kafkaBrokerUrl());
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        this.producer = new KafkaProducer<>(properties);
    }

    public void processAndSendMessage(@NotNull String source, @NotNull String kafkaTopic, String topic, String payload) {

        // Extract key from topic
        String[] elements = topic.split("/");
        String key = (elements.length > 0 ? elements[elements.length - 1] : topic);

        log.debug("kafka record: kafkaTopic={}, key={}, payload={}", kafkaTopic, key, payload);

        // Parse payload JSON
        JsonElement jsonElement = JsonParser.parseString(payload);

        if (!jsonElement.isJsonObject()) {
            throw new IllegalArgumentException("Payload must be a JSON object");
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();

        // Add "key" field to JSON payload (string literal)
        jsonObject.addProperty("source", source);
        jsonObject.addProperty("key", key);

        String updatedPayload = jsonObject.toString();

        // Send to Kafka (optionally reuse the same key as Kafka record key)
        ProducerRecord<String, String> record =
                new ProducerRecord<>(kafkaTopic, key, updatedPayload);

        producer.send(record);
    }
}
