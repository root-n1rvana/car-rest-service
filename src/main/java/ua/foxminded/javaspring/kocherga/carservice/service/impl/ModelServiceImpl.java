package ua.foxminded.javaspring.kocherga.carservice.service.impl;

import com.opencsv.bean.CsvToBeanBuilder;
import jakarta.transaction.Transactional;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import ua.foxminded.javaspring.kocherga.carservice.models.Brand;
import ua.foxminded.javaspring.kocherga.carservice.models.Model;
import ua.foxminded.javaspring.kocherga.carservice.models.RawLine;
import ua.foxminded.javaspring.kocherga.carservice.models.Type;
import ua.foxminded.javaspring.kocherga.carservice.repository.ModelRepository;
import ua.foxminded.javaspring.kocherga.carservice.service.Cache;
import ua.foxminded.javaspring.kocherga.carservice.service.ModelService;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ModelServiceImpl implements ModelService {

    private final static String TYPE_SPLITTER = ",\\s*|,";
    private final static String FILE_TO_READ = "file.csv";
    private final Cache<String, Brand> brandCache = new Cache<>();
    private final Cache<String, Type> typeCache = new Cache<>();

    private final ModelRepository modelRepository;

    public ModelServiceImpl(ModelRepository modelRepository) {
        this.modelRepository = modelRepository;
    }

    @Override
    public void init() {
        List<Model> models = createModelsFromRawLines(readAllLines());
        saveModelsInBatch(models);
    }

    @Transactional
    @Override
    public void saveModelsInBatch(List<Model> models) {
        for (int i = 0; i < models.size(); i += 500) {
            int endIndex = Math.min(i + 500, models.size());
            List<Model> batch = models.subList(i, endIndex);
            modelRepository.saveAll(batch);
        }
    }

    private List<RawLine> readAllLines() {
        List<RawLine> rawLines = Collections.emptyList();
        try {
            File file = new ClassPathResource(FILE_TO_READ).getFile();
            try (FileReader fileReader = new FileReader(file)) {
                rawLines = new CsvToBeanBuilder<RawLine>(fileReader)
                    .withType(RawLine.class)
                    .withSkipLines(1)
                    .build()
                    .parse();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        rawLines.remove(rawLines.size() - 1);
        return rawLines;
    }

    private List<Model> createModelsFromRawLines(List<RawLine> lines) {

        List<Model> models = new ArrayList<>();

        for (RawLine line : lines) {
            Model newModel = new Model();

            newModel.setId(line.getModelId());
            newModel.setName(line.getModelName());
            newModel.setYear(Integer.parseInt((line.getYear())));
            newModel.setBrand(getBrandOrMakeNewIfNotExist(line));
            newModel.setTypes(getTypeOrMakeNewIfNotExist(line));

            models.add(newModel);
        }
        return models;
    }

    private Brand getBrandOrMakeNewIfNotExist(RawLine line) {
        Brand brand = brandCache.getValue(line.getBrandName());
        if (brand == null) {
            brand = new Brand(line.getBrandName());
            brandCache.putValue(line.getBrandName(), brand);
        }
        return brand;
    }

    private List<Type> getTypeOrMakeNewIfNotExist(RawLine line) {
        List<Type> types = new ArrayList<>();
        String[] typeNames = line.getTypeName().split(TYPE_SPLITTER);
        int index = 0;
        while (index < typeNames.length) {
            Type type = typeCache.getValue(typeNames[index]);
            if (type == null) {
                type = new Type(typeNames[index]);
                typeCache.putValue(typeNames[index], type);
            }
            types.add(type);
            index++;
        }
        return types;
    }
}
