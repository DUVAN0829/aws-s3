package co.duvan.s3.app.service;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

public interface ServiceS3 {

    String createBucket(String bucketName);

    String checkIfBucketExist(String bucketName);

    List<String> getAllbuckets();

    Boolean uploadFile(String bucketName, String key, Path fileLocation);

    void downloadFile(String bucket, String key) throws IOException;

    //* Permite subir archivos por tiempo limitado o de manera temporal
    String generatePresignedUploadUrl(String bucketName, String key, Duration duration);

    //* Permite descargar archivos por tiempo limitado o de manera temporal
    String generatePresignedDownloadUrl(String bucketName, String key, Duration duration);

}
