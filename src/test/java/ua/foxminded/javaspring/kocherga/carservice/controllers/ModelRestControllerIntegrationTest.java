package ua.foxminded.javaspring.kocherga.carservice.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.foxminded.javaspring.kocherga.carservice.models.Brand;
import ua.foxminded.javaspring.kocherga.carservice.models.Model;
import ua.foxminded.javaspring.kocherga.carservice.models.Type;
import ua.foxminded.javaspring.kocherga.carservice.repository.BrandRepository;
import ua.foxminded.javaspring.kocherga.carservice.repository.ModelRepository;
import ua.foxminded.javaspring.kocherga.carservice.repository.TypeRepository;
import ua.foxminded.javaspring.kocherga.carservice.service.exceptions.BadRequestException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ModelRestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private TypeRepository typeRepository;

    @Test
    public void testGetAllModels() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/model/all"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(10)))
            .andExpect(jsonPath("$.totalElements", is(9836)))
            .andExpect(jsonPath("$.totalPages", is(984)))
            .andReturn();
    }

    @Test
    public void testGetExactModel() throws Exception {

        String modelId = "cptB1C1NSL";

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/model/" + modelId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Malibu"))
            .andExpect(jsonPath("$.brand.name").value("Chevrolet"))
            .andExpect(jsonPath("$.year").value(2020))
            .andExpect(jsonPath("$.types[*].name", hasItem("Sedan")))
            .andReturn();
    }

    @Test
    public void testCreateBrand() throws Exception {

        String modelDtoJson = readJSONFile("json/createNewModel.json");
        String testModelNameFromJson = "TestModelName";


        // Verify that the brand does not exist in the database
        assertFalse(modelRepository.findByName(testModelNameFromJson).isPresent());

        assert modelDtoJson != null;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/model")
                .contentType(MediaType.APPLICATION_JSON)
                .content(modelDtoJson))
            .andExpect(status().isCreated())
            .andReturn();

        // Verify that the brand was added to the database
        assertTrue(modelRepository.findByName(testModelNameFromJson).isPresent());


        // Cleaning after test
        Optional<Model> modelOptional = modelRepository.findByName(testModelNameFromJson);
        modelOptional.ifPresent(model -> modelRepository.delete(model));
        assertFalse(modelRepository.findByName(testModelNameFromJson).isPresent());
    }

    @Test
    public void testUpdateModel() throws Exception {
        // ToDo need to fix  tested method first
    }

    @Test
    public void testDeleteModel() throws Exception {
        String modelIdToDelete = "testToDelete";
        Model modelToDelete = new Model();

        modelToDelete.setId(modelIdToDelete);
        modelToDelete.setName("TEST");
        modelToDelete.setYear(2024);

        Brand brand = brandRepository.findByName("Audi").orElseThrow(
            () -> new BadRequestException("Brand with this name does not exist"));
        modelToDelete.setBrand(brand);

        List<Type> types = new ArrayList<>();
        Type type = typeRepository.findByName("Sedan").orElseThrow(
            () -> new BadRequestException("Type with this name does not exist"));
        types.add(type);
        modelToDelete.setTypes(types);

        modelRepository.save(modelToDelete);

        // Check what new model was saved
        assertTrue(modelRepository.existsById(modelIdToDelete));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/model/" + modelIdToDelete)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNoContent());

        // Verify that the brand is deleted
        assertFalse(modelRepository.existsById(modelIdToDelete));
    }

    private String readJSONFile(String filePath) {
        ClassLoader classLoader = getClass().getClassLoader();
        try {
            InputStream inputStream = classLoader.getResourceAsStream(filePath);
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                return stringBuilder.toString();
            } else {
                throw new IllegalArgumentException("File not found: " + filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}