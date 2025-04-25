package com.viannele.classicsputsimply.model;

public class Classic {
    private String id;
    private String name;

    public Classic() {
    }

    public Classic(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}