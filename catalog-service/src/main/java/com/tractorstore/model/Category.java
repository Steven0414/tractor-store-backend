package com.tractorstore.model;

public class Category {
    private String id;
    private String name;
    private String filter;
    private String imageUrl;
    private String description;

    public Category() {}

    public Category(String id, String name, String filter, String imageUrl, String description) {
        this.id = id;
        this.name = name;
        this.filter = filter;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getFilter() { return filter; }
    public void setFilter(String filter) { this.filter = filter; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
