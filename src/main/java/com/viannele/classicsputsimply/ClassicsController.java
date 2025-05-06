package com.viannele.classicsputsimply;

import com.viannele.classicsputsimply.model.Classic;
import com.viannele.classicsputsimply.service.ClassicsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/classics")
@Tag(name = "Classics", description = "Operations related to simplified classic stories")
public class ClassicsController {

    private static final Logger logger = LoggerFactory.getLogger(ClassicsController.class);

    private final ClassicsService classicsService;

    public ClassicsController(ClassicsService classicsService) {
        this.classicsService = classicsService;
    }

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
            List<Classic> allClassics = classicsService.getAllClassics(lang);
            return new ResponseEntity<>(allClassics, HttpStatus.OK);
        } catch (IOException e) {
            logger.error("Error retrieving classics:", e);
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
            @PathVariable @Parameter(
                    description = "Slug of the classic story (e.g., little-red-riding-hood)",
                    example = "little-red-riding-hood",
                    name = "slug"
            ) String slug,
            @RequestParam(name = "lang", defaultValue = "en") String lang) {
        try {
            Classic classic = classicsService.getClassicContentBySlug(slug, lang);
            if (classic != null) {
                return new ResponseEntity<>(classic, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            logger.error("Error retrieving classic content:", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/pdf/{slug}")
    public ResponseEntity<byte[]> generatePdf(@PathVariable String slug) {
        // Implementation to generate PDF using classicsService
        try {
            byte[] pdfBytes = classicsService.generatePdf(slug); //  Make sure this method exists
            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .body(pdfBytes);
        } catch (Exception e) {
            logger.info("PDF Generation Exception: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}