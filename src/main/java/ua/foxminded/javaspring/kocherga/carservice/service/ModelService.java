package ua.foxminded.javaspring.kocherga.carservice.service;

import org.springframework.data.domain.Page;
import ua.foxminded.javaspring.kocherga.carservice.models.Model;
import ua.foxminded.javaspring.kocherga.carservice.models.dto.ModelDto;

import java.util.List;

public interface ModelService {

    void init();

    Page<ModelDto> findAll(int page, int size, String sort, String order);

    ModelDto findById(String id);

    ModelDto create(ModelDto modelDto);

    void update(ModelDto modelDto);

    void delete(String id);

    Page<ModelDto> searchModels(String brandName, String modelName, Integer minYear, Integer maxYear, String typeNames, int page, int size, String sort, String order);

    void saveModelsInBatch(List<Model> models);
}
