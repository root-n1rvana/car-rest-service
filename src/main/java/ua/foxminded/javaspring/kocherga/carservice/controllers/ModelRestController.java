package ua.foxminded.javaspring.kocherga.carservice.controllers;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.foxminded.javaspring.kocherga.carservice.models.dto.ModelDto;
import ua.foxminded.javaspring.kocherga.carservice.service.ModelService;

@RestController
@RequestMapping("/api/v1/model")
public class ModelRestController {

    private final ModelService modelService;

    public ModelRestController(ModelService modelService) {
        this.modelService = modelService;
    }

    @GetMapping("/all")
    public ResponseEntity<Page<ModelDto>> getAllModels(
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ModelDto> modelDtoPage = modelService.findAll(pageable);
        return ResponseEntity.ok(modelDtoPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModelDto> getModel(@PathVariable("id") String id) {
        ModelDto modelDto = modelService.findById(id);
        return ResponseEntity.ok(modelDto);
    }

    @PostMapping
    public ResponseEntity<ModelDto> createModel(@RequestBody @Valid ModelDto modelDto) {
        ModelDto newModel = modelService.create(modelDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateBrand(@PathVariable("id") String id, @RequestBody @Valid ModelDto modelDto) {
        modelDto.setId(id);
        modelService.update(modelDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModel(@PathVariable("id") String id) {
        modelService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
