package com.viannele.classicsputsimply.tests;

import com.viannele.classicsputsimply.ClassicsController;
import com.viannele.classicsputsimply.model.Classic;
import com.viannele.classicsputsimply.service.ClassicsService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ClassicsController.class)
public class ClassicsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClassicsService classicsService;

    @Test
    @WithMockUser
    void getAllClassicsReturnsOkAndListOfClassics() throws Exception {

        Map<String, String> titles1 = new HashMap<>();
        titles1.put("en", "Little Red Riding Hood");
        Classic classic1 = new Classic("first_id", "Little Red Riding Hood", "little-red-riding-hood", titles1 );

        Map<String, String> titles2 = new HashMap<>();
        titles2.put("en", "The Sword in the Stone");
        Classic classic2 = new Classic("second_id", "The Sword in the Stone", "sword-in-the-stone", titles2);

        List<Classic> classics = Arrays.asList(classic1, classic2);

        when(classicsService.getAllClassics("en")).thenReturn(classics);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/classics")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Little Red Riding Hood")))
                .andExpect(jsonPath("$[1].name", is("The Sword in the Stone")));
    }

    @Test
    @WithMockUser
    void getAllClassicsWithLangParamReturnsTranslatedTitles() throws Exception {
        Map<String, String> titles1 = new HashMap<>();
        titles1.put("en", "Little Red Riding Hood");
        titles1.put("de", "Rotkäppchen");
        Classic classic1 = new Classic("first_id", "Rotkäppchen", "little-red-riding-hood", titles1); // Name is now "Rotkäppchen"

        List<Classic> classics = Collections.singletonList(classic1);

        when(classicsService.getAllClassics("de")).thenReturn(classics);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/classics")
                        .param("lang", "de")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Rotkäppchen")));
    }

    @Test
    @WithMockUser
    void getAllClassicsServiceThrowsExceptionAndReturnsInternalServerError() throws Exception {
        when(classicsService.getAllClassics(Mockito.anyString())).thenThrow(new IOException("Failed to read data"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/classics")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    @WithMockUser
    void getClassicContentBySlugReturnsOkAndClassic() throws Exception {
        Classic classic = new Classic("first_id", "Little Red Riding Hood", "little-red-riding-hood", Collections.emptyMap());
        when(classicsService.getClassicContentBySlug("little-red-riding-hood", "en")).thenReturn(classic);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/classics/story/little-red-riding-hood")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.name", is("Little Red Riding Hood")))
                .andExpect(jsonPath("$.slug", is("little-red-riding-hood")));
    }

    @Test
    @WithMockUser
    void getClassicContentBySlugStoryNotFoundReturnsNotFound() throws Exception {
        when(classicsService.getClassicContentBySlug("non-existent-slug", "en")).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/classics/story/non-existent-slug")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser
    void getClassicContentBySlugServiceThrowsExceptionReturnsInternalServerError() throws Exception {
        when(classicsService.getClassicContentBySlug(Mockito.anyString(), Mockito.anyString()))
                .thenThrow(new IOException("Failed to read story"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/classics/story/some-slug")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }
}