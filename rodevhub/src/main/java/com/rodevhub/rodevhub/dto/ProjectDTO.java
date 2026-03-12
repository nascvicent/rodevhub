package com.rodevhub.rodevhub.dto;

import java.time.LocalDate;

public class ProjectDTO {

    private Long id;
    private String name;
    private String description;
    private String link;
    private LocalDate productionDate;

    public ProjectDTO() {}

    public ProjectDTO(Long id, String name, String description, String link, LocalDate productionDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.link = link;
        this.productionDate = productionDate;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public LocalDate getProductionDate() { return productionDate; }
    public void setProductionDate(LocalDate productionDate) { this.productionDate = productionDate; }
}
