package clients;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class SetFileClient {
    private final ObjectMapper objectMapper;
    private final String fileKey;
    private final String bucketName;
    private final AmazonS3 s3Client;

    public SetFileClient(ObjectMapper objectMapper, String fileKey, String bucketName, AmazonS3 s3Client) throws JsonProcessingException {
        this.objectMapper = objectMapper;
        this.fileKey = fileKey;
        this.bucketName = bucketName;
        this.s3Client = s3Client;
        createFileIfNotExists();
    }

    public boolean container(final String string) throws IOException {
        return readFile().contains(string);
    }

    public Set<String> readFile() throws IOException {
        final S3Object object = s3Client.getObject(bucketName, fileKey);
        return objectMapper.readValue(object.getObjectContent(), new TypeReference<Set<String>>() {});
    }
    public void writeFile(final Set<String> content) throws JsonProcessingException {
        final String stringContent = objectMapper.writeValueAsString(content);
        s3Client.putObject(bucketName, fileKey, stringContent);
    }

    private void createFileIfNotExists() throws JsonProcessingException {
        if(!s3Client.doesObjectExist(bucketName, fileKey)) {
            writeFile(new HashSet<>());
        }
    }
}
