package ua.foxminded.javaspring.kocherga.carservice.models;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "models", uniqueConstraints = {
    @UniqueConstraint(name = "models_ck", columnNames = {"year", "name", "brand_id"})
})
public class Model {

    @Id
    private String id;

    @Column(name = "name", length = 50, unique = true, nullable = false)
    private String name;

    @Column(name = "year", length = 4, nullable = false)
    private Integer year;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "models_types",
        joinColumns = @JoinColumn(name = "model_id"),
        inverseJoinColumns = @JoinColumn(name = "type_id"))
    private List<Type> types;

    public Model() {
    }

    public Model(String id, String name, Integer year, Brand brand, List<Type> types) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Model model = (Model) o;
        return name.equals(model.name) && year.equals(model.year) && brand.equals(model.brand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, year, brand);
    }
}
