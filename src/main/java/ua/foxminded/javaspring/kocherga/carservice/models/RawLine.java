package ua.foxminded.javaspring.kocherga.carservice.models;

import com.opencsv.bean.CsvBindByPosition;

import java.util.Objects;

public class RawLine {

    @CsvBindByPosition(position = 0)
    private String modelId;

    @CsvBindByPosition(position = 1)
    private String brandName;

    @CsvBindByPosition(position = 2)
    private String year;

    @CsvBindByPosition(position = 3)
    private String modelName;

    @CsvBindByPosition(position = 4)
    private String typeName;

    public RawLine() {
    }

    public RawLine(String modelId, String brandName, String year, String modelName, String typeName) {
        this.modelId = modelId;
        this.brandName = brandName;
        this.year = year;
        this.modelName = modelName;
        this.typeName = typeName;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RawLine rawLine = (RawLine) o;
        return modelId.equals(rawLine.modelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modelId);
    }
}
