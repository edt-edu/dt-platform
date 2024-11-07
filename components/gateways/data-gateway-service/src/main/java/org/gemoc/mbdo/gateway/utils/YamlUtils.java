package org.gemoc.mbdo.gateway.utils;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class YamlUtils {

	
    /**
     * Converts a YAML file to an object of the specified class.
     * The YAML file is read from the classpath, and the first key's value
     * in the YAML structure is converted to a JSON string, which is then
     * mapped to an object of the specified class.
     *
     * @param yamlFilePath the path to the YAML file on the classpath
     * @param objectClass  the class of the object to be returned
     * @param <T>          the type of the object to be returned
     * @return an object of the specified class, populated with the data from the YAML file
     * @throws NullPointerException if yamlFilePath or objectClass is null
     * @throws RuntimeException     if an error occurs while reading the YAML file or converting it to the object
     */
    public static <T> T convertYamlToObject(final ApplicationContext applicationContext, @NotNull String yamlFilePath, @NotNull Class<T> objectClass) {
        Resource resource = applicationContext.getResource(yamlFilePath);

        try (InputStream inputStream = resource.getInputStream()) {
            // Create an ObjectMapper for YAML
            ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
            // Read the YAML content into a JsonNode
            JsonNode jsonNode = yamlReader.readTree(inputStream);
            // Get the value of the first key from the JsonNode
            JsonNode firstKeyValue = jsonNode.fields().next().getValue();
            // Convert the JsonNode to a JSON string
            String jsonString = new ObjectMapper().writeValueAsString(firstKeyValue);
            // Convert the JSON string to an object of the specified class
            return new ObjectMapper().readValue(jsonString, objectClass);
        } catch (IOException e) {
            // Log the error and throw a RuntimeException in case of an exception
            log.error("Error converting YAML file to object: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
