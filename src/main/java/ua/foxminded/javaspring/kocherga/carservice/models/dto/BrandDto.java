package ua.foxminded.javaspring.kocherga.carservice.models.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class BrandDto {

    private Long id;

    @NotEmpty
    @NotNull
    @Size(min = 2, max = 50, message = "Brand name should have at least 2 and max 50 characters")
    private String name;

    public BrandDto() {
    }

    public BrandDto(String name) {
        this.name = name;
    }

    public BrandDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
