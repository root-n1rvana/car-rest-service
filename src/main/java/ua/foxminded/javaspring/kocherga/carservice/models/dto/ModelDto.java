package ua.foxminded.javaspring.kocherga.carservice.models.dto;

import ua.foxminded.javaspring.kocherga.carservice.models.Brand;
import ua.foxminded.javaspring.kocherga.carservice.models.Type;

import java.util.List;

public class ModelDto {

    private String id;

    private String name;

    private int year;

    private Brand brand;

    private List<Type> types;

    public ModelDto() {
    }

    public ModelDto(String id, String name, int year, Brand brand, List<Type> types) {
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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public List<Type> getTypes() {
        return types;
    }

    public void setTypes(List<Type> types) {
        this.types = types;
    }
}
