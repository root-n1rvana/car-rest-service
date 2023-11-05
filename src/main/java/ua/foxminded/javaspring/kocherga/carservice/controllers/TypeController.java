package ua.foxminded.javaspring.kocherga.carservice.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.foxminded.javaspring.kocherga.carservice.models.dto.TypeDto;
import ua.foxminded.javaspring.kocherga.carservice.service.TypeService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/type")
public class TypeController {

    private final TypeService typeService;

    public TypeController(TypeService typeService) {
        this.typeService = typeService;
    }

    @GetMapping("/all")
    public List<TypeDto> getAllTypes() {
        return typeService.findAll();
    }

    @GetMapping("/{id}")
    public TypeDto getType(@PathVariable("id") Long id) {
        return typeService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Void> createType(@RequestBody @Valid TypeDto typeDto) {
        typeService.create(typeDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping
    public ResponseEntity<Void> updateType(@RequestBody @Valid TypeDto typeDto) {
        typeService.update(typeDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteType(@PathVariable("id") Long id) {
        typeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
