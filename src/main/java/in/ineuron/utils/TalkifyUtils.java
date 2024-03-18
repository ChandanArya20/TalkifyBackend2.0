package in.ineuron.utils;

import in.ineuron.models.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@AllArgsConstructor
public class TalkifyUtils {

    private HttpServletRequest request;

    public String getBaseURL(){
        // Get the base URL dynamically from the current request
        return request.getRequestURL().toString().replace(request.getRequestURI(), "");
    }

    public MediaCategory getMediaCategory(MultipartFile file){

        String contentType = file.getContentType();
        String category = contentType.split("/")[0];

        return switch (category) {
            case "image" -> MediaCategory.IMAGE;
            case "video" -> MediaCategory.VIDEO;
            case "audio" -> MediaCategory.AUDIO;
            default -> MediaCategory.OTHER;
        };
    }

}
