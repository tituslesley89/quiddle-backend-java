package modules;

import clients.SetFileClient;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import lombok.SneakyThrows;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.inject.Named;
import javax.inject.Singleton;

@Module
public class LambdaModule {
    public static final String API_KEY_NAME = "API_KEY_NAME";
    public static final String EXCEPTION_WORDS = "EXCEPTION_WORDS";
    public static final String EXCEPTION_CATEGORY = "EXCEPTION_CATEGORY";
    public static final String ACCESS_KEY_FILE = "ACCESS_KEY_FILE";
    public static final String BUCKET_NAME_KEY = "BUCKET_NAME_KEY";

    final String exceptionWordFile = "exception_words.txt";
    final String exceptionCategoryFile = "exception_category.txt";
    final String accessKeyFile = "accessKeyFile.txt";
    final String bucketEnvironmentVariable = "BUCKET_NAME";

    @Provides
    @Singleton
    HttpClient providesHttpClient() {
        return HttpClientBuilder.create()
                .build();
    }

    @Provides
    @Singleton
    ObjectMapper providesObjectMapper() {
        return new ObjectMapper();
    }

    @Provides
    @Singleton
    @Named(API_KEY_NAME)
    String providesApiKey() {
        return "b20cb889-918c-40a4-8250-44ee3669b574";
    }

    @Provides
    @Singleton
    AmazonS3 providesAmazonS3Client() {
        return AmazonS3ClientBuilder.defaultClient();
    }

    @Provides
    @Singleton
    @Named(BUCKET_NAME_KEY)
    String providesBucketName() {
        return System.getenv(bucketEnvironmentVariable);
    }

    @SneakyThrows
    @Provides
    @Singleton
    @Named(EXCEPTION_WORDS)
    SetFileClient providesExceptionWordsClient(
            final ObjectMapper objectMapper,
            final AmazonS3 s3Client,
            @Named(BUCKET_NAME_KEY) final String bucketName
    ) {
        return new SetFileClient(
                objectMapper,
                exceptionWordFile,
                bucketName,
                s3Client
        );
    }

    @SneakyThrows
    @Provides
    @Singleton
    @Named(EXCEPTION_CATEGORY)
    SetFileClient providesExceptionCategoryClient(
            final ObjectMapper objectMapper,
            final AmazonS3 s3Client,
            @Named(BUCKET_NAME_KEY) final String bucketName
    ) {
        return new SetFileClient(
                objectMapper,
                exceptionCategoryFile,
                bucketName,
                s3Client
        );
    }

    @SneakyThrows
    @Provides
    @Singleton
    @Named(ACCESS_KEY_FILE)
    SetFileClient providesAccessKeyClient(
            final ObjectMapper objectMapper,
            final AmazonS3 s3Client,
            @Named(BUCKET_NAME_KEY) final String bucketName
    ) {
        return new SetFileClient(
                objectMapper,
                accessKeyFile,
                bucketName,
                s3Client
        );
    }
}
