package com.example.application.common.model.command;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : Andrey Kolchev
 * @since : 05/05/2025
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMessage {
    String id;
    JsonNode context;
    ResponseErrorMessage error;
    JsonNode data;
}

