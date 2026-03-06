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

import java.util.Properties;

@Slf4j
@Service
public class MqttToKafkaService {

    private KafkaProducer<String, String> producer = null;

    public MqttToKafkaService(GatewayService gatewayService) {
        String kafkaBrokerUrl = gatewayService.getGatewayServiceConfiguration().kafkaBrokerUrl();
        if (kafkaBrokerUrl == null || kafkaBrokerUrl.isBlank()) {
            log.warn("Kafka broker URL is not configured. MqttToKafkaService will be initialized without a Kafka producer.");
            return;
        }

        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokerUrl);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        this.producer = new KafkaProducer<>(properties);
    }

    public void processAndSendMessage(@NotNull String source, @NotNull String kafkaTopic, String topic, String payload) {
        if (producer == null) {
            log.warn("Kafka producer is not initialized. Skipping MQTT->Kafka forwarding for source={} and topic={}", source, topic);
            return;
        }

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
        ProducerRecord<String, String> producerRecord =
                new ProducerRecord<>(kafkaTopic, key, updatedPayload);

        producer.send(producerRecord);
    }
}
