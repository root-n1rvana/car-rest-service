package ua.foxminded.javaspring.kocherga.carservice.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.foxminded.javaspring.kocherga.carservice.models.Brand;
import ua.foxminded.javaspring.kocherga.carservice.repository.BrandRepository;

import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BrandControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtDecoder jwtDecoder;

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
    public void testGetExactBrand_NoSuchBrandIdError() throws Exception {
        long brandId = 0;

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/brand/" + brandId))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("There's no such brand with id 0"));
    }

    @Test
    public void testCreateBrand() throws Exception {
        String testBrandName = "TestBrandName";
        String brandDtoJson = String.format("{\"name\":\"%s\"}", testBrandName);

        // Verify that the brand does not exist in the database
        assertFalse(brandRepository.findByName(testBrandName).isPresent());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/brand")
                .with(SecurityMockMvcRequestPostProcessors.jwt())
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
        long existingBrandId = 1;
        String brandDtoJson = String.format("{\"id\": %d,\"name\":\"%s\"}", existingBrandId, expectedName);


        assertTrue(brandRepository.findById(existingBrandId).isPresent());
        assertNotEquals(brandRepository.findById(existingBrandId).get().getName(), expectedName);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/brand")
                .with(SecurityMockMvcRequestPostProcessors.jwt())
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
    public void testUpdateBrand_BrandExistError() throws Exception {
        String existingBrandName = "Dodge";
        long existingBrandId = 1;
        String brandDtoJson = String.format("{\"id\": %d,\"name\":\"%s\"}", existingBrandId, existingBrandName);

        assertTrue(brandRepository.findById(existingBrandId).isPresent());
        assertNotEquals(brandRepository.findById(existingBrandId).get().getName(), existingBrandName);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/brand")
                .with(SecurityMockMvcRequestPostProcessors.jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(brandDtoJson))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Brand with same 'name' already exist"));
    }

    @Test
    public void testUpdateBrand_NoSuchBrandIdError() throws Exception {
        String newBrandName = "newBrand";
        long notExistingBrandId = 0;
        String brandDtoJson = String.format("{\"id\": %d,\"name\":\"%s\"}", notExistingBrandId, newBrandName);

        assertFalse(brandRepository.findById(notExistingBrandId).isPresent());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/brand")
                .with(SecurityMockMvcRequestPostProcessors.jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(brandDtoJson))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("There's no such brand with id 0"));
    }

    @Test
    public void testDeleteBrand() throws Exception {
        Brand brandToDelete = new Brand("TEST");
        brandRepository.save(brandToDelete);
        long existingBrandId = brandToDelete.getId();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/brand/" + existingBrandId)
                .with(SecurityMockMvcRequestPostProcessors.jwt())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNoContent());

        // Verify that the brand is deleted
        assertFalse(brandRepository.existsById(existingBrandId));
    }
}