package com.example.application.common.utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.MapType;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class JsonUtil {

    private static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
    }

    public static <T> String objectToJson(final T object) {
        Objects.requireNonNull(object);
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T jsonToObject(final String json, final Class<T> clazz) {
        Objects.requireNonNull(json);
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> T jsonNodeToObject(final JsonNode json, final Class<T> clazz) {
        Objects.requireNonNull(json);
        try {
            return mapper.treeToValue(json, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static JsonNode jsonToJsonNode(final String json) {
        Objects.requireNonNull(json);
        try {
            return mapper.readTree(json);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> JsonNode objectToJsonNode(final T object) {
        Objects.requireNonNull(object);
        return mapper.valueToTree(object);
    }

    public static ObjectNode createObjectNode() {
        return mapper.createObjectNode();
    }


    public static ObjectNode empty() {
        return mapper.createObjectNode();
    }

    public static ArrayNode createArrayNode() {
        return mapper.createArrayNode();
    }

    public static <T> T toMap(final MapType mapType, final String json) {
        Objects.requireNonNull(json);
        try {
            return mapper.readValue(json, mapType);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <K, V> Map<K, V> toMap(
            final Class<? extends Map> mapClass,
            final Class<K> keyClass,
            final Class<V> valueClass,
            final String jsonString) {
        MapType mapType = mapper.getTypeFactory().constructMapType(mapClass, keyClass, valueClass);
        try {
            return mapper.readValue(jsonString, mapType);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> T fileToObject(final String fileName, final Class<T> clazz) {
        String json = readFile(fileName);
        Objects.requireNonNull(json);
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String readFile(final String fileName) {
        InputStream is = Optional
                .ofNullable(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName))
                .orElseThrow(() -> new RuntimeException("File: " + fileName + " not found."));
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("File: " + fileName + "could not be read.");
        }
        return sb.toString();
    }

    private static String toCompact(final String source) throws IOException {
        final JsonFactory factory = new JsonFactory();
        final JsonParser parser = factory.createParser(source);
        final StringWriter out = new StringWriter();
        try (JsonGenerator gen = factory.createGenerator(out)) {
            while (parser.nextToken() != null) {
                gen.copyCurrentEvent(parser);
            }
        }
        return out.getBuffer().toString();
    }

    public static String merge(final String mainJson, final String updateJson) {
        Objects.requireNonNull(mainJson);
        Objects.requireNonNull(updateJson);
        try {
            final JsonNode mainNode = mapper.readTree(mainJson);
            final JsonNode updateNode = mapper.readTree(updateJson);
            final JsonNode mergedJson = merge(mainNode, updateNode);
            return mapper.writeValueAsString(mergedJson);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static JsonNode merge(final JsonNode mainNode, final JsonNode updateNode) {

        final Iterator<String> fieldNames = updateNode.fieldNames();

        while (fieldNames.hasNext()) {
            final String updatedFieldName = fieldNames.next();
            final JsonNode valueToBeUpdated = mainNode.get(updatedFieldName);
            final JsonNode updatedValue = updateNode.get(updatedFieldName);
            // If the node is an @ArrayNode
            if (valueToBeUpdated != null && valueToBeUpdated.isArray() && updatedValue.isArray()) {
                // running a loop for all elements of the updated ArrayNode
                for (int i = 0; i < updatedValue.size(); i++) {
                    final JsonNode updatedChildNode = updatedValue.get(i);
                    // Create a new Node in the node that should be updated, if there was no corresponding node in it
                    // Use-case - where the updateNode will have a new element in its Array
                    if (valueToBeUpdated.size() <= i) {
                        ((ArrayNode) valueToBeUpdated).add(updatedChildNode);
                    }
                    // getting reference for the node to be updated
                    final JsonNode childNodeToBeUpdated = valueToBeUpdated.get(i);
                    merge(childNodeToBeUpdated, updatedChildNode);
                }
                // if the Node is an @ObjectNode
            } else if (valueToBeUpdated != null && valueToBeUpdated.isObject()) {
                merge(valueToBeUpdated, updatedValue);
            } else {
                if (mainNode instanceof ObjectNode) {
                    ((ObjectNode) mainNode).replace(updatedFieldName, updatedValue);
                }
            }
        }
        return mainNode;
    }

    public static String getText(final String fieldName, final JsonNode jsonData) {
        try {
            final JsonNode jsonNode = jsonData.get(fieldName);
            if (jsonNode != null) {
                return jsonNode.asText();
            } else {
                return null;
            }
        } catch (Exception e) {
        }
        return null;
    }

    public static Boolean getBoolean(final String fieldName, final JsonNode jsonData) {
        try {
            final JsonNode jsonNode = jsonData.get(fieldName);
            if (jsonNode != null) {
                return jsonNode.asBoolean();
            } else {
                return null;
            }
        } catch (Exception e) {
        }
        return null;
    }
}
