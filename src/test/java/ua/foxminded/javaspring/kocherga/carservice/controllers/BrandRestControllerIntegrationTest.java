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
import ua.foxminded.javaspring.kocherga.carservice.repository.BrandRepository;

import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BrandRestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BrandRepository brandRepository;

    @Test
    public void testGetAllBrands() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/brand/all"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(64)))
            .andReturn();
    }

    @Test
    public void testGetExactBrand() throws Exception {

        long brandId = 1;

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/brand/" + brandId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Audi"))
            .andReturn();
    }

    @Test
    public void testCreateBrand() throws Exception {
        String testBrandName = "TestBrandName";
        String brandDtoJson = "{\"name\":\"" + testBrandName + "\"}";

        // Verify that the brand does not exist in the database
        assertFalse(brandRepository.findByName(testBrandName).isPresent());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/brand")
                .contentType(MediaType.APPLICATION_JSON)
                .content(brandDtoJson))
            .andExpect(status().isCreated())
            .andReturn();

        // Verify that the brand was added to the database
        assertNotNull(brandRepository.findByName(testBrandName));

        // Cleaning after test
        Optional<Brand> brandOptional = brandRepository.findByName(testBrandName);
        brandOptional.ifPresent(brand -> brandRepository.delete(brand));
    }

    @Test
    public void testUpdateBrand() throws Exception {
        String expectedName = "UpdatedBrandName";
        String brandDtoJson = "{\"name\":\"" + expectedName + "\"}";
        long existingBrandId = 1;


        assertTrue(brandRepository.findById(existingBrandId).isPresent());
        assertNotEquals(brandRepository.findById(existingBrandId).get().getName(), expectedName);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/brand/" + existingBrandId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(brandDtoJson))
            .andExpect(status().isNoContent())
            .andReturn();

        // Verify that the brand name was updated
        String actualName = brandRepository.findById(existingBrandId).get().getName();
        assertEquals(expectedName, actualName);

        //Reverse changes
        Brand brand = brandRepository.findById(existingBrandId).get();
        brand.setName("Audi");
        brandRepository.save(brand);
    }

    @Test
    public void testDeleteBrand() throws Exception {
        Brand brandToDelete = new Brand("TEST");
        brandRepository.save(brandToDelete);
        long existingBrandId = brandToDelete.getId();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/brand/" + existingBrandId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNoContent());

        // Verify that the brand is deleted
        assertFalse(brandRepository.existsById(existingBrandId));
    }
}