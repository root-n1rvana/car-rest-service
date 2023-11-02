package ua.foxminded.javaspring.kocherga.carservice.service;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.foxminded.javaspring.kocherga.carservice.models.Model;
import ua.foxminded.javaspring.kocherga.carservice.models.dto.ModelDto;

import java.util.List;

public interface ModelService {

    void init();

    @Transactional
    Page<ModelDto> findAll(Pageable pageable);

    ModelDto findById(String id);

    @Transactional
    ModelDto create(ModelDto modelDto);

    @Transactional
    void update(ModelDto modelDto);

    @Transactional
    void delete(String id);

    Page<ModelDto> searchModels(String brandName, String modelName, Integer minYear, Integer maxYear, String typeNames, Pageable pageable);

    void sortFieldValidation(String sortField);

    String orderValidation(String sortOrder);

    @Transactional
    void saveModelsInBatch(List<Model> models);
}
