package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Builder
@Jacksonized
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Definition {
    @JsonProperty("fl")
    private String category;
    @JsonProperty("shortdef")
    private List<String> definition;
    @JsonProperty("meta")
    private Meta meta;
}
