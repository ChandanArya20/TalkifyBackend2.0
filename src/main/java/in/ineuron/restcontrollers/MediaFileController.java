package in.ineuron.restcontrollers;

import in.ineuron.models.MediaFile;
import in.ineuron.repositories.MediaFileRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/media")
@AllArgsConstructor
public class MediaFileController {

    private MediaFileRepository mediaFileRepo;

    @GetMapping("/{id}")
    public ResponseEntity<?> getMediaById(@PathVariable Long id) {

        // Attempt to find the media file by ID
        Optional<MediaFile> mediaFileOptional = mediaFileRepo.findById(id);

        if (mediaFileOptional.isPresent()) {

            MediaFile mediaFile = mediaFileOptional.get();
            // Return the media file with appropriate content type
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf(mediaFile.getFileType()))
                    .body(mediaFile.getMediaContent());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Media not found for this ID");
        }
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<?> downloadMediaById(@PathVariable Long id) {

        // Attempt to find the media file by ID
        Optional<MediaFile> mediaFileOptional = mediaFileRepo.findById(id);

        if (mediaFileOptional.isPresent()) {

            MediaFile mediaFile = mediaFileOptional.get();

            // Set content disposition header to suggest filename for download
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+mediaFile.getFileName()+"\"")
                    .contentType(MediaType.valueOf(mediaFile.getFileType()))
                    .body(mediaFile.getMediaContent());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Media not found for this ID");
        }
    }
}
