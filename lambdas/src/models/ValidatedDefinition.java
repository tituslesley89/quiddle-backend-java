package models;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@Getter
public class ValidatedDefinition {
    private Definition definition;
    private boolean isValid;
    private String reason;
}
