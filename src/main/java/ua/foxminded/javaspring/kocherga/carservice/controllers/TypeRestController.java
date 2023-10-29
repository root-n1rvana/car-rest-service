package ua.foxminded.javaspring.kocherga.carservice.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.foxminded.javaspring.kocherga.carservice.models.dto.TypeDto;
import ua.foxminded.javaspring.kocherga.carservice.service.TypeService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/type")
public class TypeRestController {

    private final TypeService typeService;

    public TypeRestController(TypeService typeService) {
        this.typeService = typeService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<TypeDto>> getAllTypes() {
        List<TypeDto> typesDto = typeService.findAll();
        return ResponseEntity.of(Optional.ofNullable(typesDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TypeDto> getType(@PathVariable("id") Long id) {
        TypeDto typeDto = typeService.findById(id);
        return ResponseEntity.ok(typeDto);
    }

    @PostMapping
    public ResponseEntity<Void> createType(@RequestBody @Valid TypeDto typeDto) {
        typeService.create(typeDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateType(@PathVariable("id") Long id, @RequestBody @Valid TypeDto typeDto) {
            typeDto.setId(id);
            typeService.update(typeDto);
            return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteType(@PathVariable("id") Long id) {
            typeService.delete(id);
            return ResponseEntity.noContent().build();
    }
}
