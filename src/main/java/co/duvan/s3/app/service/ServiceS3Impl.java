package co.duvan.s3.app.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class ServiceS3Impl implements ServiceS3 {

    //* Vars
    @Value("${spring.destination.folder}")
    private String destinationFolder;
    private final S3Client s3Client;

    //* Constructor
    public ServiceS3Impl(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    //todo -> Methods Implements
    @Override
    public String createBucket(String bucketName) {
        CreateBucketResponse bucketResponse = this.s3Client.createBucket(buckerBuilder -> buckerBuilder.bucket(bucketName));
        return "Bucket creado con exito en la ubicación: " + bucketResponse.location();
    }

    @Override
    public String checkIfBucketExist(String bucketName) {

        try {

            this.s3Client.headBucket(headBucket -> headBucket.bucket(bucketName));

            return "El bucket " + bucketName + " si existe";

        } catch (S3Exception exception) {

            return "El bucket " + bucketName + " no existe";

        }

    }

    @Override
    public List<String> getAllbuckets() {

        ListBucketsResponse bucketsResponse = this.s3Client.listBuckets();

        if (bucketsResponse.hasBuckets()) {
            return bucketsResponse.buckets()
                    .stream()
                    .map(Bucket::name)
                    .toList();
        }

        return List.of();

    }

    @Override
    public Boolean uploadFile(String bucketName, String key, Path fileLocation) {

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        PutObjectResponse putObjectResponse = this.s3Client.putObject(putObjectRequest, fileLocation);

        return putObjectResponse.sdkHttpResponse().isSuccessful();

    }

    @Override
    public void downloadFile(String bucket, String key) throws IOException {

        // Validación de entrada
        if (bucket == null || bucket.isEmpty()) {
            throw new IllegalArgumentException("El bucket no puede ser nulo o vacío.");
        }

        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("La clave del objeto no puede ser nula o vacía.");
        }

        if (this.s3Client == null) {
            throw new IllegalStateException("El cliente S3 no está inicializado.");
        }

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        // Obtener los bytes del objeto
        ResponseBytes<GetObjectResponse> objectBytes = this.s3Client.getObjectAsBytes(getObjectRequest);

        // Extraer el nombre del archivo
        String filename;
        if (key.contains("/")) {
            filename = key.substring(key.lastIndexOf("/") + 1); //* Extrae el nombre 1 posición después de '/'
        } else {
            filename = key;
        }

        // Construir la ruta de destino
        String filePath = Paths.get(destinationFolder, filename).toString();

        //* Crear el archivo y el directorio
        File file = new File(filePath);
        file.getParentFile().mkdirs();

        // Escribir los bytes en el archivo
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(objectBytes.asByteArray());
        } catch (IOException exception) {
            throw new IOException("Error al descargar el archivo: " + exception.getMessage(), exception);
        }
    }

    @Override
    public String generatePresignedUploadUrl(String bucketName, String key, Duration duration) {
        return null;
    }

    @Override
    public String generatePresignedDownloadUrl(String bucketName, String key, Duration duration) {
        return null;
    }

}
