package in.ineuron.restcontrollers;

import in.ineuron.models.MediaFile;
import in.ineuron.repositories.MediaFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
public class MediaFileController {

    @Autowired
    private MediaFileRepository mediaFileRepo;

    @GetMapping("/{id}")
    public ResponseEntity<?> getMediaById(@PathVariable Long id) {

        Optional<MediaFile> mediaFileOptional = mediaFileRepo.findById(id);

        if (mediaFileOptional.isPresent()) {

            MediaFile mediaFile = mediaFileOptional.get();

            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf(mediaFile.getFileType()))
                    .body(mediaFile.getMediaContent());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Media not found for this ID");
        }
    }
}
