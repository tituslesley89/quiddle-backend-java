package lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import components.ComponentBuilder;
import components.DaggerComponentBuilder;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import services.WordValidatorService;

public class ToggleWordLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    final WordValidatorService wordValidatorService;
    final ObjectMapper objectMapper;

    public ToggleWordLambda() {
        final ComponentBuilder componentBuilder = DaggerComponentBuilder.create();
        wordValidatorService = componentBuilder.buildWordValidatorService();
        objectMapper = new ObjectMapper();
    }

    @SneakyThrows
    @Override
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        final String accessKey = input.getPathParameters().get("accessKey");
        final String word = input.getPathParameters().get("word");
        if(StringUtils.isEmpty(accessKey) || StringUtils.isEmpty(word)) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400);
        }

        if(!wordValidatorService.isValidAccessKey(accessKey)) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(403);
        }

        wordValidatorService.toggleWord(word);
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200);
    }
}
