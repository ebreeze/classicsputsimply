package com.viannele.classicsputsimply;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viannele.classicsputsimply.model.Classic;
import com.viannele.classicsputsimply.model.ClassicCategory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/v1/classics")
@Tag(name = "Classics", description = "Operations related to simplified classic stories")
public class ClassicsController {

    @Value("classpath:data/fairy_tales.json")
    private Resource fairyTalesResource;

    @Value("classpath:data/legends.json")
    private Resource legendsResource;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "List all available simplified classics by category",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of classics",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ClassicCategory.class))))
            }
    )
    public ResponseEntity<List<ClassicCategory>> getAllClassics() {
        try {
            InputStream fairyTalesInputStream = fairyTalesResource.getInputStream();
            List<Classic> fairyTales = objectMapper.readValue(fairyTalesInputStream, new TypeReference<List<Classic>>() {});

            InputStream legendsInputStream = legendsResource.getInputStream();
            List<Classic> legends = objectMapper.readValue(legendsInputStream, new TypeReference<List<Classic>>() {});

            List<ClassicCategory> classicsByCategory = List.of(
                    new ClassicCategory("Fairy Tales", fairyTales),
                    new ClassicCategory("Legends", legends)
            );

            return new ResponseEntity<>(classicsByCategory, HttpStatus.OK);

        } catch (IOException e) {
            // Handle the error appropriately (e.g., log it, return an error response)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}