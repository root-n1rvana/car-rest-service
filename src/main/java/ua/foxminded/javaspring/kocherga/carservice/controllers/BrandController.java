package ua.foxminded.javaspring.kocherga.carservice.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.foxminded.javaspring.kocherga.carservice.models.dto.BrandDto;
import ua.foxminded.javaspring.kocherga.carservice.service.BrandService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/brand")
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping("/all")
    public List<BrandDto> getAllBrands() {
        return brandService.findAll();
    }

    @GetMapping("/{id}")
    public BrandDto getBrand(@PathVariable("id") Long id) {
        return brandService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Void> createBrand(@RequestBody @Valid BrandDto brandDto) {
        brandService.create(brandDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping
    public ResponseEntity<Void> updateBrand(@RequestBody @Valid BrandDto brandDto) {
        brandService.update(brandDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable("id") Long id) {
        brandService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
