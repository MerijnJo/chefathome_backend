package com.example.chefbackend.model;

import jakarta.persistence.*;

// This annotation tells Hibernate this class should become a table
@Entity
@Table(name = "chefs") // optional, just names the table
public class Chef {

    @Id // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment
    private Long id;

    private String name;
    private String cuisine;
    private int pricePerPerson;

    // Default constructor (Hibernate needs this)
    public Chef() {}

    // Custom constructor for convenience
    public Chef(String name, String cuisine, int pricePerPerson) {
        this.name = name;
        this.cuisine = cuisine;
        this.pricePerPerson = pricePerPerson;
    }

    // Getters and setters — used by JPA and JSON serialization
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public int getPricePerPerson() {
        return pricePerPerson;
    }

    public void setPricePerPerson(int pricePerPerson) {
        this.pricePerPerson = pricePerPerson;
    }
}
