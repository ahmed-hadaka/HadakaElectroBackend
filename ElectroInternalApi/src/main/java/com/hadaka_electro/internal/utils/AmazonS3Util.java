package com.hadaka_electro.internal.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

public class AmazonS3Util {

    private static final Logger LOGGER = LoggerFactory.getLogger(AmazonS3Util.class);

    private static final String BUCKET_NAME;

    static {
        BUCKET_NAME = System.getenv("AWS_BUCKET_NAME");
    }

    public static List<String> listFolder(String folderName) {
        S3Client client = S3Client.builder().build();
        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(BUCKET_NAME).prefix(folderName).build();

        ListObjectsV2Response response = client.listObjectsV2(listRequest);

        List<S3Object> contents = response.contents();
        ListIterator<S3Object> listIterator = contents.listIterator();
        List<String> listKeys = new ArrayList<>();

        while (listIterator.hasNext()) {
            S3Object object = listIterator.next();
            listKeys.add(object.key());
        }

        return listKeys;
    }

    public static void uploadFile(String folderName, String fileName, InputStream inputStream) {
        S3Client client = S3Client.builder().build();

        PutObjectRequest request = PutObjectRequest.builder().bucket(BUCKET_NAME)
                .key(folderName + "/" + fileName).acl("public-read").build();
        try (inputStream) {
            int contentLength = inputStream.available();
            client.putObject(request, RequestBody.fromInputStream(inputStream, contentLength));
        } catch (IOException ex) {
            LOGGER.error("Could not upload file to Amazon S3", ex);
        }
    }

    public static void deleteFile(String fileName) {
        S3Client client = S3Client.builder().build();

        DeleteObjectRequest request = DeleteObjectRequest.builder().bucket(BUCKET_NAME)
                .key(fileName).build();
        client.deleteObject(request);
    }


    public static void removeFolder(String folderName) {
        S3Client client = S3Client.builder().build();

        String prefix = folderName.endsWith("/") ? folderName : folderName + "/";

        try {
            ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                    .bucket(BUCKET_NAME)
                    .prefix(prefix)
                    .build();

            ListObjectsV2Iterable listResponses = client.listObjectsV2Paginator(listRequest);

            listResponses.stream().forEach(page -> {
                List<ObjectIdentifier> objectsToDelete = page.contents().stream()
                        .map(s3Object -> ObjectIdentifier.builder().key(s3Object.key()).build())
                        .collect(Collectors.toList());

                if (!objectsToDelete.isEmpty()) {
                    Delete delete = Delete.builder()
                            .objects(objectsToDelete)
                            .build();

                    DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
                            .bucket(BUCKET_NAME)
                            .delete(delete)
                            .build();

                    client.deleteObjects(deleteRequest);
                }
            });

            LOGGER.info("Successfully removed S3 folder: {}", prefix);

        } catch (S3Exception ex) {
            LOGGER.error("Failed to remove S3 folder: {}", prefix, ex);
        }
    }
}
