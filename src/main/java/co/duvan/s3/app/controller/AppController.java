package co.duvan.s3.app.controller;

import co.duvan.s3.app.service.ServiceS3;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/s3")
public class AppController {

    //* Vars
    @Value("${spring.destination.folder}")
    private String destinationFolder;

    private final ServiceS3 service;

    //* Constructor
    public AppController(ServiceS3 service) {
        this.service = service;
    }

    //* Handler methods
    @PostMapping("/create")
    public ResponseEntity<String> createBucket(@RequestParam String bucketName) {
        return ResponseEntity.ok(this.service.createBucket(bucketName));
    }

    @GetMapping("/check/{bucketName}")
    public ResponseEntity<String> checkBucket(@PathVariable String bucketName) {
        return ResponseEntity.ok(this.service.checkIfBucketExist(bucketName));
    }

    @GetMapping("/list")
    public ResponseEntity<List<String>> listBuckets() {
        return ResponseEntity.ok(this.service.getAllbuckets());
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam String bucketName, @RequestParam String key, @RequestParam MultipartFile file) throws IOException {

        try {

            Path staticDir = Paths.get(destinationFolder);

            if (!Files.exists(staticDir)) { //* Crear directorio static si no existe
                Files.createDirectories(staticDir);
            }

            Path filePath = staticDir.resolve(Objects.requireNonNull(file.getOriginalFilename())); //* Agrega a la ruta el nombre del archivo
            Path finalPath = Files.write(filePath, file.getBytes()); //* Se escribe el archivo en la ruta

            Boolean result = this.service.uploadFile(bucketName, key, finalPath);

            if (result) {
                Files.delete(finalPath); //todo -> Se borra el archivo de la ruta ya que fue subido
                return ResponseEntity.ok("Archivo cargado correctamente");
            } else {
                return ResponseEntity.internalServerError().body("Error al cargar el archivo al bucket");
            }

        } catch (IOException exception) {
            throw new IOException("Error al procesar archivo");
        }

    }

    @PostMapping("/download")
    public ResponseEntity<String> downloadFile(@RequestParam String bucketName, @RequestParam String key) throws IOException {
        this.service.downloadFile(bucketName, key);
        return ResponseEntity.ok("Archivo descargado correctamente");
    }

}






























