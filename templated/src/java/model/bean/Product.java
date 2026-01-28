package model.bean;

public class Product {
    private int id;
    private String name;
    private double price;
    private String imagePath;
    private String category;

    // ✅ No-arg constructor (required for JSP/EL)
    public Product() {
        
    }

    // ✅ Full constructor
    public Product(int id, String name, double price, String imagePath, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imagePath = imagePath;
        this.category = category;
    }

    // ✅ Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getCategory() {
        return category;
    }

    // ✅ Optionally: Setters if you need to populate objects dynamically
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
