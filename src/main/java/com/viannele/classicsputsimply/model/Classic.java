package com.viannele.classicsputsimply.model;

import java.util.List;
import java.util.Map;

public class Classic {
    private String id;
    private String name;
    private String slug;
    private List<Page> pages; // For storing content when fetching a specific story
    private Map<String, String> titles;

    public Classic(String id, String name, String slug, Map<String, String> titles) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.titles = titles;
    }

    public Classic(String id, String name, String slug) {
        this.id = id;
        this.name = name;
        this.slug = slug;
    }

    public Classic() {
        // placeholder to be filled later
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }

    public Map<String, String> getTitles() {
        return titles;
    }

    public void setTitles(Map<String, String> titles) {
        this.titles = titles;
    }

    public String getTitle(String lang) {
        return titles.getOrDefault(lang, titles.get("en")); // Default to English if the requested language is not found
    }
}