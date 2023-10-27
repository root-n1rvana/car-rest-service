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

import java.io.*;
import java.util.*;

@Service
public class ModelServiceImpl implements ModelService {

    private final static String TYPE_SPLITTER = ",";
    private final static String FILE_TO_READ = "file.csv"; //todo read file from jar
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

    private List<RawLine> readAllLines() {
        try (InputStream inputStream = new ClassPathResource(FILE_TO_READ).getInputStream()) {
            List<RawLine> rawLines = new CsvToBeanBuilder<RawLine>(new InputStreamReader(inputStream))
                .withType(RawLine.class)
                .withSkipLines(1)
                .build()
                .parse();
            if (!rawLines.isEmpty()) {
                rawLines.remove(rawLines.size() - 1);
            }
            return rawLines;
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
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
        return Optional.ofNullable(brandCache.getValue(line.getBrandName()))
            .orElseGet(() -> {
                Brand brand = new Brand(line.getBrandName());
                brandCache.putValue(line.getBrandName(), brand);
                return brand;
            });
    }

    private List<Type> getTypeOrMakeNewIfNotExist(RawLine line) {
        List<Type> types = new ArrayList<>();
        String[] typeNames = line.getTypeName().split(TYPE_SPLITTER);
        int index = 0;
        while (index < typeNames.length) {
            Type type = typeCache.getValue(typeNames[index].trim());
            if (type == null) {
                type = new Type(typeNames[index].trim());
                typeCache.putValue(typeNames[index].trim(), type);
            }
            types.add(type);
            index++;
        }
        return types;
    }

    @Transactional
    @Override
    public void saveModelsInBatch(List<Model> models) {
            modelRepository.saveAll(models);
    }
}
