package com.mygdx.quest.utils;

public class Fish {
    
    private String name, description, location;
    private Rarity rarity;
    private float weight;
    private int price;
    private int quantity;

    enum Rarity {
        COMMON,
        RARE,
        LEGENDARY
    }

    public Fish(String name, String description, String location, Rarity rarity, float weight) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.rarity = rarity;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public void setRarity(Rarity rarity) {
        this.rarity = rarity;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
