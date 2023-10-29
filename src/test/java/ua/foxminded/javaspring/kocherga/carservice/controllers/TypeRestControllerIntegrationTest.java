package ua.foxminded.javaspring.kocherga.carservice.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.foxminded.javaspring.kocherga.carservice.models.Type;
import ua.foxminded.javaspring.kocherga.carservice.repository.TypeRepository;

import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TypeRestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TypeRepository typeRepository;

    @Test
    public void testGetAllTypes() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/type/all"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(10)))
            .andReturn();
    }

    @Test
    public void testGetExactType() throws Exception {

        long typeId = 1;

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/type/" + typeId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("SUV"))
            .andReturn();
    }

    @Test
    public void testCreateType() throws Exception {
        String testTypeName = "TestTypeName";
        String typeDtoJson = "{\"name\":\"" + testTypeName + "\"}";

        // Verify that the brand does not exist in the database
        assertFalse(typeRepository.findByName(testTypeName).isPresent());


        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/type")
                .contentType(MediaType.APPLICATION_JSON)
                .content(typeDtoJson))
            .andExpect(status().isCreated())
            .andReturn();

        // Verify that the brand was added to the database
        assertNotNull(typeRepository.findByName(testTypeName));

        // Cleaning after test
        Optional<Type> typeOptional = typeRepository.findByName(testTypeName);
        typeOptional.ifPresent(type -> typeRepository.delete(type));
    }

    @Test
    public void testUpdateType() throws Exception {
        String expectedName = "UpdatedTypeName";
        String typeDtoJson = "{\"name\":\"" + expectedName + "\"}";
        long existingTypeId = 1;


        assertTrue(typeRepository.findById(existingTypeId).isPresent());
        assertNotEquals(typeRepository.findById(existingTypeId).get().getName(), expectedName);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/type/" + existingTypeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(typeDtoJson))
            .andExpect(status().isNoContent())
            .andReturn();

        // Verify that the brand name was updated
        String actualName = typeRepository.findById(existingTypeId).get().getName();
        assertEquals(expectedName, actualName);

        //Reverse changes
        Type type = typeRepository.findById(existingTypeId).get();
        type.setName("SUV");
        typeRepository.save(type);
    }

    @Test
    public void testDeleteType() throws Exception {
        Type typeToDelete = new Type("TEST");
        typeRepository.save(typeToDelete);
        long existingTypeId = typeToDelete.getId();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/type/" + existingTypeId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNoContent());

        // Verify that the brand is deleted
        assertFalse(typeRepository.existsById(existingTypeId));
    }
}