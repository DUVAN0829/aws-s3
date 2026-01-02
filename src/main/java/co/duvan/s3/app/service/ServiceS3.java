package co.duvan.s3.app.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface ServiceS3 {

    String createBucket(String bucketName);

    String checkIfBucketExist(String bucketName);

    List<String> getAllbuckets();

    Boolean uploadFile(String bucketName, String key, Path fileLocation);

    void downloadFile(String bucket, String key) throws IOException;

}
