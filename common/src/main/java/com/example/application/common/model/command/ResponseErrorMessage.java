package com.example.application.common.model.command;

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
public class ResponseErrorMessage {
    String code;
    String message;
}
