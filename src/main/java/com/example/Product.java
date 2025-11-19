package com.example;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private double price;
    private int stock;
    private String category;
    private String description;

    // Constructor vacío requerido por Hibernate
    public Product() {}

    // Constructor sin id
    public Product(String name, double price, int stock, String category, String description)
            throws NegativeInt, Nulability {
        setName(name);
        setPrice(price);
        setStock(stock);
        setCategory(category);
        setDescription(description);
    }

    // Constructor con id
    public Product(int id, String name, double price, int stock, String category, String description)
            throws NegativeInt, Nulability {
        NegativeInt.verificar(id, "id");
        setName(name);
        setPrice(price);
        setStock(stock);
        setCategory(category);
        setDescription(description);
        this.id = id;
    }

    // Getters y setters con validación
    public int getId() {
        return id;
    }

    public void setId(int id) throws NegativeInt {
        NegativeInt.verificar(id, "id");
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws Nulability {
        Nulability.verifyNulability(name, "nombre");
        this.name = name.trim();
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) throws NegativeInt {
        NegativeInt.verificar(price, "precio");
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) throws NegativeInt {
        NegativeInt.verificar(stock, "stock");
        this.stock = stock;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) throws Nulability {
        Nulability.verifyNulability(category, "categoria");
        this.category = category.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) throws Nulability {
        Nulability.verifyNulability(description, "descripcion");
        this.description = description.trim();
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
