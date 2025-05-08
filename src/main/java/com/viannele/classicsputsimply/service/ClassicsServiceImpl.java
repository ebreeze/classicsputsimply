package com.viannele.classicsputsimply.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.TextAlignment;
import com.viannele.classicsputsimply.model.Classic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Image;
import com.itextpdf.io.image.ImageDataFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ClassicsServiceImpl implements ClassicsService {

    private static final Logger logger = LoggerFactory.getLogger(ClassicsServiceImpl.class);

    @Value("classpath:data/fairy_tales.json")
    private Resource fairyTalesResource;

    @Value("classpath:data/legends.json")
    private Resource legendsResource;

    @Value("classpath:data/fables.json")
    private Resource fablesResource;

    private final ObjectMapper objectMapper;

    private static final String STORIES_BASE_PATH = "data/stories/";
    private static final String IMAGES_BASE_PATH = "data/images/";

    public ClassicsServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public List<Classic> getAllClassics(String lang) throws IOException {
        InputStream fairyTalesInputStream = fairyTalesResource.getInputStream();
        List<Classic> fairyTales = objectMapper.readValue(fairyTalesInputStream, new TypeReference<List<Classic>>() {});

        InputStream legendsInputStream = legendsResource.getInputStream();
        List<Classic> legends = objectMapper.readValue(legendsInputStream, new TypeReference<List<Classic>>() {});

        InputStream fablesInputStream = fablesResource.getInputStream();
        List<Classic> fables = objectMapper.readValue(fablesInputStream, new TypeReference<List<Classic>>() {});
        return
            Stream.of(fairyTales.stream(), legends.stream(), fables.stream())
                .flatMap(Function.identity()) // Or .flatMap(s -> s)
                .map(classic -> {
                    String translatedTitle = classic.getTitles().getOrDefault(lang, classic.getName());
                    Classic translatedClassic = new Classic();
                    translatedClassic.setId(classic.getId());
                    translatedClassic.setName(translatedTitle);
                    translatedClassic.setTitles(classic.getTitles());
                    translatedClassic.setSlug(classic.getSlug());
                    return translatedClassic;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Classic getClassicContentBySlug(String slug, String lang) throws IOException {
        String resourcePath = "data/stories/" + slug + "/" + lang + ".json";
        Resource storyResource = new ClassPathResource(resourcePath);

        if (!storyResource.exists()) {
            return null; // Or throw an exception, depending on desired behavior
        }

        InputStream inputStream = storyResource.getInputStream();
        return objectMapper.readValue(inputStream, Classic.class);
    }

    @Override
    public byte[] generatePdf(String slug) throws IOException {
        return generatePdf(slug, "en");
    }

    public byte[] generatePdf(String slug, String lang) throws IOException {
        // 1. Read JSON data from file
        JsonNode storyData = readStoryData(slug, lang);

        // 2. Create PDF document
        try (
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf)
        ) {

            // 3. Process JSON data and add content to PDF
            addStoryContentToPdf(document, storyData, slug);

            // 4. Return PDF as byte array
            document.close();
            return baos.toByteArray();
        }
    }

    private JsonNode readStoryData(String slug, String lang) throws IOException {
        // Construct the file path
        String filePath = STORIES_BASE_PATH + slug + "/" + lang + ".json";
        ClassPathResource resource = new ClassPathResource(filePath); // Use ClassPathResource

        // Use InputStream to read from classpath
        try (InputStream inputStream = resource.getInputStream()) {
            return objectMapper.readTree(inputStream);
        }
    }


    private void addStoryContentToPdf(Document document, JsonNode storyData, String slug) throws IOException {
        // Get story title
        String storyName = storyData.get("name").asText();
        document.add(new Paragraph(storyName).setFontSize(22).setBold().setTextAlignment(TextAlignment.CENTER));

        // Get pages array
        JsonNode pages = storyData.get("pages");
        int numPages = pages.size();
        int currentPage = 0;

        if (pages.isArray()) {
            for (JsonNode page : pages) {
                String title = page.get("title").asText();
                String text = page.get("text").asText();
                String imageUrl = page.get("imageUrl").asText();
                currentPage++;

                document.add(new Paragraph(title).setFontSize(16).setBold().setFontSize(18).setMarginTop(20).setMarginBottom(20).setFirstLineIndent(20));
                document.add(new Paragraph(text).setMarginBottom(20).setFontSize(16).setFirstLineIndent(20));

                // Add image if imageUrl is present
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    addImageToPdf(document, slug, imageUrl);
                }
                if(currentPage < numPages) {
                    document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
                }
            }
        }
    }

    private void addStoryContentToPdf1(Document document, JsonNode storyData, String slug) throws IOException {
        // Get story title
        String storyName = storyData.get("name").asText();
        document.add(new Paragraph(storyName).setFontSize(20).setBold());

        // Get pages array
        JsonNode pages = storyData.get("pages");

        if (pages.isArray()) {
            for (JsonNode page : pages) {
                String title = page.get("title").asText();
                String text = page.get("text").asText();
                String imageUrl = page.get("imageUrl").asText();

                document.add(new Paragraph(title).setFontSize(16).setBold());
                document.add(new Paragraph(text));

                if (imageUrl != null && !imageUrl.isEmpty()) {
                    addImageToPdf(document, slug, imageUrl);
                    document.add(new AreaBreak(AreaBreakType.NEXT_PAGE)); // insert page break
                }
            }
        }
    }

    private void addImageToPdf(Document document, String slug, String imageUrl) throws IOException {
        String imagePath = IMAGES_BASE_PATH + slug + "/" + imageUrl;
        ClassPathResource resource = new ClassPathResource(imagePath);
        try (InputStream inputStream = resource.getInputStream()) {
            ImageData imageData = ImageDataFactory.create(inputStream.readAllBytes());
            Image image = new Image(imageData);
            image.setAutoScaleWidth(true);
            document.add(image);
        }
    }
}