package cdk;

import software.amazon.awscdk.core.App;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.StackProps;

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