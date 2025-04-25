package com.viannele.classicsputsimply.model;

import java.util.List;

public class ClassicCategory {
    private String category;
    private List<Classic> classics;

    public ClassicCategory() {
    }

    public ClassicCategory(String category, List<Classic> classics) {
        this.category = category;
        this.classics = classics;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<Classic> getClassics() {
        return classics;
    }

    public void setClassics(List<Classic> classics) {
        this.classics = classics;
    }
}