package cdk;

import software.amazon.awscdk.core.*;

import software.amazon.awscdk.services.lambda.*;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.s3.assets.AssetOptions;

import java.util.Arrays;

public class Stack extends software.amazon.awscdk.core.Stack {

    public Stack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public Stack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        new QuiddlerConstruct(this, "quiddler-stack");
    }

    public static void main(final String[] args) {
        final App app = new App();
        new Stack(app, "Stack", StackProps.builder().build());
        app.synth();
    }
}