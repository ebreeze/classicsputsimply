package com.viannele.classicsputsimply.model;

import java.util.*;

public class Classic {
    private String id;
    private String name;
    private String slug;
    private List<Page> pages = new ArrayList<>();
    private Map<String, String> titles = new HashMap<>();

    public Classic(String id, String name, String slug, Map<String, String> titles) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.titles = new HashMap<>(titles); // Create a defensive copy
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
        return pages == null ? Collections.emptyList() : Collections.unmodifiableList(pages);
    }

    public void setPages(List<Page> pages) {
        this.pages = new ArrayList<>(pages); // Create a defensive copy
    }

    public Map<String, String> getTitles() {
        return Collections.unmodifiableMap(titles); // Return an immutable view
    }

    public void setTitles(Map<String, String> titles) {
        this.titles = new HashMap<>(titles); // Create a defensive copy
    }

    public String getTitle(String lang) {
        return titles.getOrDefault(lang, titles.get("en")); // Default to English if not found
    }
}