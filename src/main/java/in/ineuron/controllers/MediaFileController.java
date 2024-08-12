package in.ineuron.controllers;

import in.ineuron.constant.ErrorConstant;
import in.ineuron.exception.MediaException;
import in.ineuron.models.MediaFile;
import in.ineuron.repositories.MediaFileRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
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
    @Cacheable(value = "mediaCache", key = "#id")
    public ResponseEntity<byte[]> getMediaById(@PathVariable Long id) {

        // Attempt to find the media file by ID
        Optional<MediaFile> mediaFileOptional = mediaFileRepo.findById(id);

        if (mediaFileOptional.isPresent()) {

            MediaFile mediaFile = mediaFileOptional.get();
            // Return the media file with appropriate content type
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf(mediaFile.getFileType()))
                    .body(mediaFile.getMediaContent());
        } else {
            throw new MediaException(
                    ErrorConstant.MEDIA_NOT_FOUND_ERROR.getErrorCode(),
                    ErrorConstant.MEDIA_NOT_FOUND_ERROR.getErrorMessage(),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    @GetMapping("/{id}/download")
    @Cacheable(value = "mediaDownloadCache", key = "#id")
    public ResponseEntity<byte[]> downloadMediaById(@PathVariable Long id) {

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
            throw new MediaException(
                    ErrorConstant.MEDIA_NOT_FOUND_ERROR.getErrorCode(),
                    ErrorConstant.MEDIA_NOT_FOUND_ERROR.getErrorMessage(),
                    HttpStatus.NOT_FOUND
            );
        }
    }
}
