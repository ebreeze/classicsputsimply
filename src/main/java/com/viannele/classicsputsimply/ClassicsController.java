package com.viannele.classicsputsimply;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viannele.classicsputsimply.model.Classic;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/classics")
@Tag(name = "Classics", description = "Operations related to simplified classic stories")
public class ClassicsController {

    private static final Logger logger = LoggerFactory.getLogger(ClassicsController.class);

    @Value("classpath:data/fairy_tales.json")
    private Resource fairyTalesResource;

    @Value("classpath:data/legends.json")
    private Resource legendsResource;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "List all available classics",
            parameters = {
                    @Parameter(name = "lang", description = "Optional language code for preferred title (translations in 'titles' map)")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of classics",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Classic.class))))
            }
    )
    public ResponseEntity<List<Classic>> getAllClassics(@RequestParam(name = "lang", required = false, defaultValue = "en") String lang) {
        try {
            InputStream fairyTalesInputStream = fairyTalesResource.getInputStream();
            List<Classic> fairyTales = objectMapper.readValue(fairyTalesInputStream, new TypeReference<List<Classic>>() {});

            InputStream legendsInputStream = legendsResource.getInputStream();
            List<Classic> legends = objectMapper.readValue(legendsInputStream, new TypeReference<List<Classic>>() {});

            List<Classic> allClassics = Stream.concat(fairyTales.stream(), legends.stream())
                    .map(classic -> {
                        String translatedTitle = classic.getTitles().getOrDefault(lang, classic.getName());
                        // Create a new Classic object with the translated name
                        Classic translatedClassic = new Classic();
                        translatedClassic.setId(classic.getId());
                        translatedClassic.setName(translatedTitle);
                        translatedClassic.setTitles(classic.getTitles()); // Still include all titles
                        translatedClassic.setSlug(classic.getSlug());
                        return translatedClassic;
                    })
                    .collect(Collectors.toList());

            return new ResponseEntity<>(allClassics, HttpStatus.OK);

        } catch (IOException e) {
            logger.error("Error reading or processing classics data:", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/story/{slug}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Retrieve the content of a specific classic story by slug and language",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of story content",
                            content = @Content(schema = @Schema(implementation = Classic.class))),
                    @ApiResponse(responseCode = "404", description = "Story not found")
            }
    )
    public ResponseEntity<Classic> getClassicContentBySlug(
            @PathVariable @Parameter(description = "Slug of the classic story (e.g., little-red-riding-hood)") String slug,
            @RequestParam(name = "lang", defaultValue = "en") String lang) {
        try {
            String resourcePath = "data/stories/" + slug + "/" + lang + ".json";
            Resource storyResource = new ClassPathResource(resourcePath);

            if (!storyResource.exists()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            InputStream inputStream = storyResource.getInputStream();
            Classic classic = objectMapper.readValue(inputStream, Classic.class);

            return new ResponseEntity<>(classic, HttpStatus.OK);

        } catch (IOException e) {
            logger.error("Error reading or processing classics data:", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}