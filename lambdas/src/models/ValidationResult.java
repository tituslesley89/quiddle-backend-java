package models;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Jacksonized
@Builder
@Getter
public class ValidationResult {
    private Boolean isValid;
    private String reason;
    private List<ValidatedDefinition> validDefinition;
    private List<ValidatedDefinition> invalidDefinition;
}
