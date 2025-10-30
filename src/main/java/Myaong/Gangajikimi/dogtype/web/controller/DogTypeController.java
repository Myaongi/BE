package Myaong.Gangajikimi.dogtype.web.controller;

import Myaong.Gangajikimi.dogtype.service.DogTypeService;
import Myaong.Gangajikimi.dogtype.web.docs.DogTypeControllerDocs;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DogTypeController implements DogTypeControllerDocs {

    private final DogTypeService dogTypeService;

    @GetMapping("/api/dog-types/search")
    public List<String> searchDogTypes(@RequestParam String keyword) {
        return dogTypeService.searchDogTypes(keyword);
    }

    @GetMapping("/api/dog-types/all")
    public List<String> getAllDogTypes() {
        return dogTypeService.getAllDogTypes();
    }

    @GetMapping("/api/dogbreed")
    public String getDogBreeds(@RequestPart MultipartFile image) {

        return dogTypeService.getDogBreed(image);

    }
}