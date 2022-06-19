package cdk;

import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Duration;
import software.amazon.awscdk.services.apigateway.LambdaIntegration;
import software.amazon.awscdk.services.apigateway.Resource;
import software.amazon.awscdk.services.apigateway.RestApi;
import software.amazon.awscdk.services.lambda.*;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.s3.BucketProps;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class QuiddlerConstruct extends Construct {
    private final Bucket quiddlerSource;
    private final String BUCKET_NAME = "quiddler-app-storage";
    private final Function validateWordLambdaFunction;
    private final Function toggleWordLambdaFunction;
    private final Function toggleCategoryLambdaFunction;
    private final RestApi restApi;

    public QuiddlerConstruct(software.constructs.@NotNull Construct scope, @NotNull String id) {
        super(scope, id);

        final LayerVersion layer = new LayerVersion(scope, "layer", LayerVersionProps.builder()
                .code(Code.fromAsset("../layer/target/bundle"))
                .compatibleRuntimes(Arrays.asList(Runtime.JAVA_8))
                .build()
        );

        quiddlerSource = new Bucket(scope, "quiddler-s3-bucket", BucketProps.builder()
                .bucketName(BUCKET_NAME)
                .build());

        final Map<String, String> environmentVariables = new HashMap<>();
        environmentVariables.put("BUCKET_NAME", quiddlerSource.getBucketName());

        validateWordLambdaFunction = new Function(scope, "ValidateWordLambda", FunctionProps.builder()
                .runtime(Runtime.JAVA_8)
                .code(Code.fromAsset("../lambdas/target/lambdas.jar"))
                .handler("lambdas.ValidateWordLambda")
                .layers(Collections.singletonList(layer))
                .memorySize(1024)
                .timeout(Duration.seconds(30))
                .logRetention(RetentionDays.ONE_WEEK)
                .environment(environmentVariables)
                .build());

        toggleWordLambdaFunction = new Function(scope, "ToggleWordLambda", FunctionProps.builder()
                .runtime(Runtime.JAVA_8)
                .code(Code.fromAsset("../lambdas/target/lambdas.jar"))
                .handler("lambdas.ToggleWordLambda")
                .layers(Collections.singletonList(layer))
                .memorySize(1024)
                .timeout(Duration.seconds(30))
                .logRetention(RetentionDays.ONE_WEEK)
                .environment(environmentVariables)
                .build());

        toggleCategoryLambdaFunction = new Function(scope, "ToggleCategoryLambda", FunctionProps.builder()
                .runtime(Runtime.JAVA_8)
                .code(Code.fromAsset("../lambdas/target/lambdas.jar"))
                .handler("lambdas.ToggleCategoryLambda")
                .layers(Collections.singletonList(layer))
                .memorySize(1024)
                .timeout(Duration.seconds(30))
                .logRetention(RetentionDays.ONE_WEEK)
                .environment(environmentVariables)
                .build());

        quiddlerSource.grantReadWrite(validateWordLambdaFunction);
        quiddlerSource.grantReadWrite(toggleWordLambdaFunction);
        quiddlerSource.grantReadWrite(toggleCategoryLambdaFunction);

        restApi = new RestApi(scope, "RestApi");
        final Resource validate = restApi.getRoot().addResource("validate");
        final Resource validateWord = validate.addResource("{word}");
        validateWord.addMethod("GET", new LambdaIntegration(validateWordLambdaFunction));

        final Resource exception = restApi.getRoot().addResource("exception");
        final Resource exceptionWord = exception.addResource("word");
        final Resource exceptionWordPath = exceptionWord.addResource("{word}");
        final Resource exceptionWordAccess = exceptionWordPath.addResource("{accessKey}");
        exceptionWordAccess.addMethod("POST", new LambdaIntegration(toggleWordLambdaFunction));

        final Resource exceptionCategory = exception.addResource("category");
        final Resource exceptionCategoryPath = exceptionCategory.addResource("{category}");
        final Resource exceptionCategoryAccess = exceptionCategoryPath.addResource("{accessKey}");
        exceptionCategoryAccess.addMethod("POST", new LambdaIntegration(toggleCategoryLambdaFunction));
    }
}
