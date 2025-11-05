package Myaong.Gangajikimi.ai.web.controller;

import Myaong.Gangajikimi.ai.service.AiService;
import Myaong.Gangajikimi.ai.web.docs.AiControllerDocs;
import Myaong.Gangajikimi.ai.web.dto.request.DogInfoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai-images")
public class AiController implements AiControllerDocs {

    private final AiService aiService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public byte[] createAiImage(@RequestBody DogInfoRequest request) {
        return aiService.generateDogImage(request);
    }
}
