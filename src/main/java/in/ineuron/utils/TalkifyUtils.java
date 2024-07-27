package in.ineuron.utils;

import in.ineuron.constant.MediaCategory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

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

    public String formatMap(Map<String, String> errorResults) {
        StringBuilder formattedString = new StringBuilder();

        for (var entry : errorResults.entrySet()) {
            formattedString.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append(", ");
        }

        // Remove the last comma and space
        if (!formattedString.isEmpty()) {
            formattedString.setLength(formattedString.length() - 2);
        }

        return formattedString.toString();
    }

}
