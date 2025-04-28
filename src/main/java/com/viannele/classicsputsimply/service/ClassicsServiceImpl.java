package com.viannele.classicsputsimply.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viannele.classicsputsimply.model.Classic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ClassicsServiceImpl implements ClassicsService {

    private static final Logger logger = LoggerFactory.getLogger(ClassicsServiceImpl.class);

    @Value("classpath:data/fairy_tales.json")
    private Resource fairyTalesResource;

    @Value("classpath:data/legends.json")
    private Resource legendsResource;

    private final ObjectMapper objectMapper;

    public ClassicsServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public List<Classic> getAllClassics(String lang) throws IOException {
        InputStream fairyTalesInputStream = fairyTalesResource.getInputStream();
        List<Classic> fairyTales = objectMapper.readValue(fairyTalesInputStream, new TypeReference<List<Classic>>() {});

        InputStream legendsInputStream = legendsResource.getInputStream();
        List<Classic> legends = objectMapper.readValue(legendsInputStream, new TypeReference<List<Classic>>() {});

        return Stream.concat(fairyTales.stream(), legends.stream())
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
}