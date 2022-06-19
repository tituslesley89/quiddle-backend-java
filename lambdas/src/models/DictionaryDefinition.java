package models;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Builder
@Jacksonized
@Getter
public class DictionaryDefinition {
    private String word;
    private List<Definition> definitions;

    public boolean hasDefinitions() {
        return !definitions.isEmpty();
    }
}
