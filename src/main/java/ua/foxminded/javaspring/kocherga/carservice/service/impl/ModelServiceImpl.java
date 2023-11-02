package ua.foxminded.javaspring.kocherga.carservice.service.impl;

import com.opencsv.bean.CsvToBeanBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ua.foxminded.javaspring.kocherga.carservice.models.Brand;
import ua.foxminded.javaspring.kocherga.carservice.models.Model;
import ua.foxminded.javaspring.kocherga.carservice.models.RawLine;
import ua.foxminded.javaspring.kocherga.carservice.models.Type;
import ua.foxminded.javaspring.kocherga.carservice.models.dto.ModelDto;
import ua.foxminded.javaspring.kocherga.carservice.models.dto.TypeDto;
import ua.foxminded.javaspring.kocherga.carservice.models.mappers.ModelMapper;
import ua.foxminded.javaspring.kocherga.carservice.repository.BrandRepository;
import ua.foxminded.javaspring.kocherga.carservice.repository.ModelRepository;
import ua.foxminded.javaspring.kocherga.carservice.service.Cache;
import ua.foxminded.javaspring.kocherga.carservice.service.ModelService;
import ua.foxminded.javaspring.kocherga.carservice.service.TypeService;
import ua.foxminded.javaspring.kocherga.carservice.service.exceptions.BadRequestException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ModelServiceImpl implements ModelService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int ID_LENGTH = 10;
    private static final String TYPE_SPLITTER = ",";
    private static final String FILE_TO_READ = "file.csv";
    private final Cache<String, Brand> brandCache = new Cache<>();
    private final Cache<String, Type> typeCache = new Cache<>();
    private final ModelRepository modelRepository;
    private final ModelMapper modelMapper;
    private final BrandRepository brandRepository;
    private final TypeService typeService;

    @PersistenceContext
    private EntityManager entityManager;

    public ModelServiceImpl(ModelRepository modelRepository, ModelMapper modelMapper,
                            BrandRepository brandRepository, TypeService typeService) {
        this.modelRepository = modelRepository;
        this.modelMapper = modelMapper;
        this.brandRepository = brandRepository;
        this.typeService = typeService;
    }

    @Override
    public void init() {
        List<Model> models = createModelsFromRawLines(readAllLines());
        saveModelsInBatch(models);
    }

    @Override
    @Transactional
    public Page<ModelDto> findAll(Pageable pageable) {
        return modelMapper.modelPageToModelDtoPage(modelRepository.findAll(pageable));
    }

    @Override
    public ModelDto findById(String id) {
        Model model = modelRepository.findById(id)
            .orElseThrow(() -> new BadRequestException("There's no such Model with id " + id));
        return modelMapper.modelToModelDto(model);
    }

    @Override
    @Transactional
    public ModelDto create(ModelDto modelDto) {
        checkIfModelExist(modelDto);
        Model newModel = new Model();
        updateModelFields(modelDto, newModel);
        newModel.setId(generateUniqueId());
        Model savedModel = modelRepository.save(newModel);
        return modelMapper.modelToModelDto(savedModel);
    }

    @Override
    @Transactional
    public void update(ModelDto modelDto) {
        Model modelToUpdate = modelRepository.findById(modelDto.getId())
            .orElseThrow(() -> new BadRequestException("There's no such Model with id " + modelDto.getId()));
        updateModelFields(modelDto, modelToUpdate);
        entityManager.clear();
        checkIfModelExist(modelMapper.modelToModelDto(modelToUpdate));
        modelRepository.save(modelToUpdate);
    }

    private void checkIfModelExist(ModelDto modelDto) {
        modelRepository.findByNameAndYearAndBrandName(modelDto.getName(), modelDto.getYear(), modelDto.getBrand().getName())
            .ifPresent(modelDb -> {
                throw new BadRequestException("Model with the same Brand, Name, and Year already exists");
            });
    }

    @Override
    @Transactional
    public void delete(String id) {
        modelRepository.deleteById(id);
    }

    @Override
    public Page<ModelDto> searchModels(String brandName, String modelName, Integer minYear, Integer maxYear, String typeNames, Pageable pageable) {
        return modelMapper.modelPageToModelDtoPage(modelRepository.findModelsByFilters(brandName, modelName, minYear, maxYear, typeNames, pageable));
    }

    @Override
    public void sortFieldValidation(String sortField) {
        if (!("id".equals(sortField) || "name".equals(sortField) || "year".equals(sortField) || "brand".equals(sortField) || "types".equals(sortField))) {
            throw new BadRequestException("Invalid sort value: " + sortField);
        }
    }

    @Override
    public String orderValidation(String sortOrder) {
        if ("desc".equals(sortOrder)) {
            return "desc";
        } else {
            return "asc";
        }
    }

    private void updateModelFields(ModelDto modelDto, Model model) {

        Optional.ofNullable(modelDto.getName())
            .ifPresent(model::setName);

        Optional.ofNullable(modelDto.getYear())
            .ifPresent(modelYear -> model.setYear(modelDto.getYear()));

        Optional.ofNullable(modelDto.getBrand())
            .ifPresent(brandDto -> {
                Brand b = brandRepository.findByName(brandDto.getName())
                    .orElseThrow(() -> new BadRequestException("There's no such Brand with name " + brandDto.getName()));
                model.setBrand(b);
            });

        Optional.ofNullable(modelDto.getTypes())
            .ifPresent(typesDto -> {
                List<String> typeNames = typesDto.stream()
                    .map(TypeDto::getName)
                    .map(String::trim)
                    .toList();

                List<Type> types = typeService.findByNameIn(typeNames);
                if (types.size() != typeNames.size()) {
                    throw new BadRequestException("Not all entered Type's were found");
                }

                model.setTypes(types);
            });
    }

    public String generateUniqueId() {
        String generatedId;
        do {
            generatedId = RandomStringUtils.randomAlphanumeric(ID_LENGTH);
        } while (modelRepository.findById(generatedId).isPresent());
        return generatedId;
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
