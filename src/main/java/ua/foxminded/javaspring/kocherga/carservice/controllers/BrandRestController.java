package ua.foxminded.javaspring.kocherga.carservice.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.foxminded.javaspring.kocherga.carservice.models.dto.BrandDto;
import ua.foxminded.javaspring.kocherga.carservice.service.BrandService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/brand")
public class BrandRestController {

    private final BrandService brandService;

    public BrandRestController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<BrandDto>> getAllBrands() {
        List<BrandDto> brandsDto = brandService.findAll();
        return ResponseEntity.of(Optional.ofNullable(brandsDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandDto> getBrand(@PathVariable("id") Long id) {
        BrandDto brandDto = brandService.findById(id);
        return ResponseEntity.ok(brandDto);
    }

    @PostMapping
    public ResponseEntity<Void> createBrand(@RequestBody @Valid BrandDto brandDto) {
        brandService.create(brandDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateBrand(@PathVariable("id") Long id, @RequestBody @Valid BrandDto brandDto) {
        brandDto.setId(id);
        brandService.update(brandDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable("id") Long id) {
        brandService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
