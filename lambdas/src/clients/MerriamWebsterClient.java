package clients;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Definition;
import models.DictionaryDefinition;
import modules.LambdaModule;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.List;

public class MerriamWebsterClient {

    private final HttpClient httpClient;
    private final String apiKey;
    private final ObjectMapper objectMapper;

    @Inject
    public MerriamWebsterClient(final HttpClient httpClient,
                                @Named(LambdaModule.API_KEY_NAME) final String apiKey,
                                final ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.apiKey = apiKey;
        this.objectMapper = objectMapper;
    }

    public DictionaryDefinition getDefinition(final String word) throws IOException {
        System.out.println(String.format("Fetching definition of word [%s]", word));
        final List<Definition> definitions = requestWordDefinition(word);
        return DictionaryDefinition.builder()
                .definitions(definitions)
                .word(word)
                .build();
    }

    private List<Definition> requestWordDefinition(final String word) throws IOException {
        final String uri = String.format("https://dictionaryapi.com/api/v3/references/collegiate/json/%s?key=%s",
                word,
                apiKey);
        System.out.println(String.format("Uri: [%s]", uri));
        final HttpResponse response = httpClient.execute(new HttpGet(uri));
        System.out.println(String.format("Response received: [%s]", response.getEntity().getContent().toString()));
        List<Definition> definitions = objectMapper.readValue(response.getEntity().getContent(),
                new TypeReference<List<Definition>>() {});
        return definitions;
    }
}
