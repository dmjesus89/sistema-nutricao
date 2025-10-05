package com.nutrition.infrastructure.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class ApiErrorResponse {

    @JsonProperty("messageToDisplay")
    private String messageToDisplay;
    private String message;
    private String path;
}
