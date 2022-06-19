package components;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Component;
import modules.LambdaModule;
import services.WordValidatorService;

import javax.inject.Singleton;

@Singleton
@Component(modules = LambdaModule.class)
public interface ComponentBuilder {
    WordValidatorService buildWordValidatorService();
}
