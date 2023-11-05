package ua.foxminded.javaspring.kocherga.carservice.service.impl;

import com.opencsv.bean.CsvToBeanBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import ua.foxminded.javaspring.kocherga.carservice.repository.TypeRepository;
import ua.foxminded.javaspring.kocherga.carservice.service.Cache;
import ua.foxminded.javaspring.kocherga.carservice.service.ModelService;
import ua.foxminded.javaspring.kocherga.carservice.service.exceptions.BadRequestException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class ModelServiceImpl implements ModelService {

    private static final String NO_SUCH_MODEL_ID_MSG = "There's no such Model with id %s";
    private static final String NO_SUCH_BRAND_NAME_MSG = "There's no such brand with name %s";
    private static final String VALID_NAME = "^.{2,50}$";
    private static final String ASC = "asc";
    private static final String DESC = "desc";
    private static final int ID_LENGTH = 10;
    private static final String TYPE_SPLITTER = ",";
    private static final String FILE_TO_READ = "file.csv";
    private final Cache<String, Brand> brandCache = new Cache<>();
    private final Cache<String, Type> typeCache = new Cache<>();
    private final ModelRepository modelRepository;
    private final ModelMapper modelMapper;
    private final BrandRepository brandRepository;
    private final TypeRepository typeRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public ModelServiceImpl(ModelRepository modelRepository, ModelMapper modelMapper,
                            BrandRepository brandRepository, TypeRepository typeRepository) {
        this.modelRepository = modelRepository;
        this.modelMapper = modelMapper;
        this.brandRepository = brandRepository;
        this.typeRepository = typeRepository;
    }

    @Override
    public void init() {
        List<Model> models = createModelsFromRawLines(readAllLines());
        saveModelsInBatch(models);
    }

    @Override
    @Transactional
    public Page<ModelDto> findAll(int page, int size, String sort, String order) {
        sortValidation(sort);
        Sort.Direction direction = Sort.Direction.fromString(orderValidation(order));
        Sort sortObj = Sort.by(direction, sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);
        return modelMapper.modelPageToModelDtoPage(modelRepository.findAll(pageable));
    }

    @Override
    public ModelDto findById(String id) {
        Model model = modelRepository.findById(id)
            .orElseThrow(() -> new BadRequestException(String.format(NO_SUCH_MODEL_ID_MSG, id)));
        return modelMapper.modelToModelDto(model);
    }

    @Override
    @Transactional
    public ModelDto create(ModelDto modelDto) {
        validateModelFields(modelDto);
        Model newModel = new Model();
        updateModelFields(modelDto, newModel);
        checkIfModelExist(newModel);
        newModel.setId(generateUniqueId());
        Model savedModel = modelRepository.save(newModel);
        return modelMapper.modelToModelDto(savedModel);
    }

    private void validateModelFields(ModelDto modelDto) {
        if (modelDto.getName() == null || !modelDto.getName().matches(VALID_NAME)) {
            throw new BadRequestException("Name is required and should be from 2 to 50 characters");
        }

        if (modelDto.getYear() == null || modelDto.getYear() < 1900) {
            throw new BadRequestException("Year is required and should be more than 1900");
        }

        if (modelDto.getBrand() == null || !modelDto.getBrand().getName().matches(VALID_NAME)) {
            throw new BadRequestException("Brand Name is required and should be from 2 to 50 characters");
        }

        if (modelDto.getTypes() == null || modelDto.getTypes().isEmpty()) {
            throw new BadRequestException("At least one Type is required");
        } else {
            modelDto.getTypes().forEach(type -> {
                if (!type.getName().matches(VALID_NAME)) {
                    throw new BadRequestException("Type Name is required and should be from 2 to 50 characters");
                }
            });
        }
    }

    private String generateUniqueId() {
        String generatedId;
        do {
            generatedId = RandomStringUtils.randomAlphanumeric(ID_LENGTH);
        } while (modelRepository.findById(generatedId).isPresent());
        return generatedId;
    }

    @Override
    @Transactional
    public void update(ModelDto modelDto) {
        Model modelToUpdate = modelRepository.findById(modelDto.getId())
            .orElseThrow(() -> new BadRequestException(String.format(NO_SUCH_MODEL_ID_MSG, modelDto.getId())));
        updateModelFields(modelDto, modelToUpdate);
        entityManager.detach(modelToUpdate);
        checkIfModelExist(modelToUpdate);
        entityManager.merge(modelToUpdate);
    }

    private void checkIfModelExist(Model model) {
        modelRepository.findByNameAndYearAndBrandName(model.getName(), model.getYear(), model.getBrand().getName())
            .ifPresent(fetchedModel -> {
                throw new BadRequestException(String.format("Model with the same name '%s', year '%s' and brand '%s' already exists",
                    model.getName(), model.getYear(), model.getBrand().getName()));
            });
    }

    private void updateModelFields(ModelDto modelDto, Model model) {

        Optional.ofNullable(modelDto.getName())
            .ifPresent(model::setName);

        Optional.ofNullable(modelDto.getYear())
            .ifPresent(modelYear -> model.setYear(modelDto.getYear()));

        Optional.ofNullable(modelDto.getBrand())
            .ifPresent(brandDto -> {
                Brand b = brandRepository.findByName(brandDto.getName())
                    .orElseGet(() -> brandRepository.save(new Brand(brandDto.getName())));
                model.setBrand(b);
            });

        Optional.ofNullable(modelDto.getTypes())
            .ifPresent(typesDto -> {
                List<String> typeNames = typesDto.stream()
                    .map(TypeDto::getName)
                    .map(String::trim)
                    .toList();

                List<Type> types = new ArrayList<>();

                for (String typeName : typeNames) {
                    types.add(typeRepository.findByName(typeName)
                        .orElseGet(() -> typeRepository.save(new Type(typeName))));
                }
                model.setTypes(types);
            });
    }

    @Override
    @Transactional
    public void delete(String id) {
        modelRepository.deleteById(id);
    }

    @Override
    public Page<ModelDto> searchModels(String brandName, String modelName, Integer minYear, Integer maxYear, String typeNames, int page, int size, String sort, String order) {
        sortValidation(sort);
        Sort.Direction direction = Sort.Direction.fromString(orderValidation(order));
        Sort sortObj = Sort.by(direction, sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);
        return modelMapper.modelPageToModelDtoPage(modelRepository.findModelsByFilters(brandName, modelName, minYear, maxYear, typeNames, pageable));
    }

    private String orderValidation(String sortOrder) {
        if (DESC.equals(sortOrder)) {
            return DESC;
        } else {
            return ASC;
        }
    }

    private void sortValidation(String sortField) {
        List<String> validSortFields = Arrays.asList("id", "name", "year", "brand", "types");

        if (!validSortFields.contains(sortField)) {
            throw new BadRequestException("Invalid sort value: " + sortField);
        }
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
