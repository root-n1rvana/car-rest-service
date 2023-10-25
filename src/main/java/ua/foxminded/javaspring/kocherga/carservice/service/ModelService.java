package ua.foxminded.javaspring.kocherga.carservice.service;

import jakarta.transaction.Transactional;
import ua.foxminded.javaspring.kocherga.carservice.models.Model;

import java.util.List;

public interface ModelService {

    void init();

    @Transactional
    void saveModelsInBatch(List<Model> models);
}
