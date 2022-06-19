package services;

import clients.MerriamWebsterClient;
import clients.SetFileClient;
import models.Definition;
import models.DictionaryDefinition;
import models.ValidatedDefinition;
import models.ValidationResult;
import modules.LambdaModule;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WordValidatorService {

    final SetFileClient categoryClient;
    final SetFileClient wordClient;
    final SetFileClient accessClient;
    final MerriamWebsterClient merriamWebsterClient;

    @Inject
    public WordValidatorService(
            @Named(LambdaModule.EXCEPTION_CATEGORY) SetFileClient categoryClient,
            @Named(LambdaModule.EXCEPTION_WORDS) SetFileClient wordClient,
            @Named(LambdaModule.ACCESS_KEY_FILE) SetFileClient accessClient,
            MerriamWebsterClient merriamWebsterClient) {
        this.categoryClient = categoryClient;
        this.wordClient = wordClient;
        this.accessClient = accessClient;
        this.merriamWebsterClient = merriamWebsterClient;
    }

    public ValidationResult validateWord(final String word) throws IOException {
        final DictionaryDefinition definition = merriamWebsterClient.getDefinition(word);
        if(!definition.hasDefinitions()) {
            return ValidationResult.builder()
                    .isValid(false)
                    .reason(String.format("No definition found for [%s]", word))
                    .build();
        }

        final Set<String> invalidWords = wordClient.readFile();
        final Set<String> invalidCategories = categoryClient.readFile();

        if(invalidWords.contains(word)) {
            final List<ValidatedDefinition> invalidDefinitions = definition.getDefinitions().stream().map(
                    def -> ValidatedDefinition.builder()
                            .isValid(false)
                            .reason("Exception word")
                            .definition(def)
                            .build()
            ).collect(Collectors.toList());
            return ValidationResult.builder()
                    .invalidDefinition(invalidDefinitions)
                    .isValid(false)
                    .reason(String.format("Word [%s] is invalid because of house rules.", word))
                    .build();
        }

        final List<ValidatedDefinition> validatedDefinitions = new ArrayList<>();
        final List<ValidatedDefinition> invalidatedDefinitions = new ArrayList<>();
        definition.getDefinitions().forEach(def -> {
            if(invalidCategories.contains(def.getCategory())) {
                invalidatedDefinitions.add(
                        ValidatedDefinition.builder()
                                .isValid(false)
                                .reason(String.format("Invalid category [%s]", def.getCategory()))
                                .definition(def)
                                .build());
            } else {
                validatedDefinitions.add(
                        ValidatedDefinition.builder()
                                .isValid(true)
                                .definition(def)
                                .build());
            }
        });

        if(validatedDefinitions.isEmpty()) {
            return ValidationResult.builder()
                    .invalidDefinition(invalidatedDefinitions)
                    .validDefinition(validatedDefinitions)
                    .isValid(false)
                    .reason("No definition found with a valid category.")
                    .build();
        }
    return ValidationResult.builder()
            .validDefinition(validatedDefinitions)
            .invalidDefinition(invalidatedDefinitions)
            .isValid(true)
            .build();
    }

    public void toggleWord(final String word) throws IOException {
        toggleSetObject(wordClient, word);
    }


    public void toggleCategory(final String category) throws IOException {
        toggleSetObject(categoryClient, category);
    }

    private void toggleSetObject(final SetFileClient client, final String word) throws IOException {
        final Set<String> wordSet = client.readFile();
        if(wordSet.contains(word)) {
            wordSet.remove(word);
        } else {
            wordSet.add(word);
        }
        client.writeFile(wordSet);
    }

    public boolean isValidAccessKey(final String accessKey) throws IOException {
        return accessClient.container(accessKey);
    }
}
