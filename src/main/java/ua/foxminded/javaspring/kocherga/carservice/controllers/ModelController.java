package ua.foxminded.javaspring.kocherga.carservice.controllers;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.foxminded.javaspring.kocherga.carservice.models.dto.ModelDto;
import ua.foxminded.javaspring.kocherga.carservice.service.ModelService;

@RestController
@RequestMapping("/api/v1/model")
public class ModelController {

    private final ModelService modelService;

    public ModelController(ModelService modelService) {
        this.modelService = modelService;
    }

    @GetMapping("/all")
    public Page<ModelDto> getAllModels(@RequestParam(value = "page", defaultValue = "0") int page,
                                       @RequestParam(value = "size", defaultValue = "10") int size,
                                       @RequestParam(value = "sort", defaultValue = "id") String sort,
                                       @RequestParam(value = "order", defaultValue = "asc") String order) {
        return modelService.findAll(page, size, sort, order);
    }

    @GetMapping("/{id}")
    public ModelDto getModel(@PathVariable("id") String id) {
        return modelService.findById(id);
    }

    @PostMapping
    public ResponseEntity<ModelDto> createModel(@RequestBody @Valid ModelDto modelDto) {
        ModelDto newModel = modelService.create(modelDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newModel);
    }

    @PutMapping
    public ResponseEntity<Void> updateModel(@RequestBody @Valid ModelDto modelDto) {
        modelService.update(modelDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModel(@PathVariable("id") String id) {
        modelService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public Page<ModelDto> searchModels(@RequestParam(value = "brandName", required = false) String brandName,
                                       @RequestParam(value = "modelName", required = false) String modelName,
                                       @RequestParam(value = "minYear", required = false) Integer minYear,
                                       @RequestParam(value = "maxYear", required = false) Integer maxYear,
                                       @RequestParam(value = "typesName", required = false) String typesName,
                                       @RequestParam(value = "page", defaultValue = "0") int page,
                                       @RequestParam(value = "sort", defaultValue = "id") String sort,
                                       @RequestParam(value = "size", defaultValue = "10") int size,
                                       @RequestParam(value = "order", defaultValue = "asc") String order) {
        return modelService.searchModels(brandName, modelName, minYear, maxYear, typesName, page, size, sort, order);
    }
}
