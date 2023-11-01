package ua.foxminded.javaspring.kocherga.carservice.models.dto;

import jakarta.validation.constraints.*;

import java.util.List;

public class ModelDto {

    private String id;

    @Size(min = 2, max = 50, message = "Model name should have at least 2 and max 50 characters")
    private String name;

    @Min(value = 1900, message = "Year must be a four-digit number more than 1900")
    @Max(value = 2050, message = "Year must be a four-digit number less than 2050")
    private Integer year;

    private BrandDto brand;

    private List<TypeDto> types;

    public ModelDto() {
    }

    public ModelDto(String id, String name, Integer year, BrandDto brand, List<TypeDto> types) {
        this.id = id;
        this.name = name;
        this.year = year;
        this.brand = brand;
        this.types = types;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public BrandDto getBrand() {
        return brand;
    }

    public void setBrand(BrandDto brand) {
        this.brand = brand;
    }

    public List<TypeDto> getTypes() {
        return types;
    }

    public void setTypes(List<TypeDto> types) {
        this.types = types;
    }
}
