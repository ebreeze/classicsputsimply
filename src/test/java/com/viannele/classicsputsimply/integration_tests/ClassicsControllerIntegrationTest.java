package com.viannele.classicsputsimply.integration_tests;

import com.viannele.classicsputsimply.model.Classic;
import com.viannele.classicsputsimply.service.ClassicsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class ClassicsControllerIntegrationTest {

    private final MockMvc mockMvc;

    @org.mockito.Mock
    private ClassicsService classicsService;

    @Autowired
    public ClassicsControllerIntegrationTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void getAllClassics_shouldReturnListOfClassics() throws Exception {
        // Arrange
        Classic classic1 = new Classic();
        classic1.setSlug("little-red-riding-hood");
        classic1.setTitles(Map.of("en", "Little Red Riding Hood", "de", "Rotkäppchen"));

        Classic classic2 = new Classic();
        classic2.setSlug("the-three-little-pigs");
        classic2.setTitles(Map.of("en", "The Three Little Pigs", "de", "Die drei kleinen Schweinchen"));

        List<Classic> allClassics = Arrays.asList(classic1, classic2);
        when(classicsService.getAllClassics("en")).thenReturn(allClassics);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/classics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].slug", is("little-red-riding-hood")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", is("Little Red Riding Hood")));
    }

    @Test
    void getAllClassics_withLangParam_shouldReturnTranslatedTitles() throws Exception {
        // Arrange
        Classic classic1 = new Classic();
        classic1.setSlug("little-red-riding-hood");
        classic1.setTitles(Map.of("en", "Little Red Riding Hood", "de", "Rotkäppchen"));

        List<Classic> allClassicsDe = Collections.singletonList(classic1);
        when(classicsService.getAllClassics("de")).thenReturn(allClassicsDe);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/classics")
                        .param("lang", "de")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].slug", is("little-red-riding-hood")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", is("Rotkäppchen")));
    }


}