package lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import components.ComponentBuilder;
import components.DaggerComponentBuilder;
import models.ValidationResult;
import org.apache.commons.lang3.StringUtils;
import services.WordValidatorService;
import utils.HeaderUtils;

import java.io.IOException;

public class ValidateWordLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    final WordValidatorService wordValidatorService;
    final ObjectMapper objectMapper;

    public ValidateWordLambda() {
        final ComponentBuilder componentBuilder = DaggerComponentBuilder.create();
        wordValidatorService = componentBuilder.buildWordValidatorService();
        objectMapper = new ObjectMapper();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent event, final Context context) {
        System.out.println("Received event: " + event.toString());
        final String word = event.getPathParameters().get("word");
        if (StringUtils.isEmpty(word)) {
           System.out.println("Word not defined");
           return new APIGatewayProxyResponseEvent()
                   .withHeaders(HeaderUtils.getCorsHeader())
                   .withStatusCode(400);
        }
        try {
            ValidationResult validationResult = wordValidatorService.validateWord(word);
            final APIGatewayProxyResponseEvent apiGatewayProxyResponseEvent = new APIGatewayProxyResponseEvent()
                    .withBody(objectMapper.writeValueAsString(validationResult))
                    .withHeaders(HeaderUtils.getCorsHeader())
                    .withStatusCode(200);
            return apiGatewayProxyResponseEvent;
        } catch (IOException e) {
            e.printStackTrace();
            return new APIGatewayProxyResponseEvent()
                    .withHeaders(HeaderUtils.getCorsHeader())
                    .withStatusCode(500);
        }
    }
}