package tn.enit.tp4.kafka;

import java.io.Serializable;

public class InventoryData implements Serializable {

    private String skuCode;
    private String designNo;
    private int stock;
    private String category;
    private String size;
    private String color;

    public InventoryData() {}

    public InventoryData(String skuCode, String designNo, int stock, String category, String size, String color) {
        this.skuCode = skuCode;
        this.designNo = designNo;
        this.stock = stock;
        this.category = category;
        this.size = size;
        this.color = color;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public String getDesignNo() {
        return designNo;
    }

    public int getStock() {
        return stock;
    }

    public String getCategory() {
        return category;
    }

    public String getSize() {
        return size;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "InventoryData{" +
                "skuCode='" + skuCode + '\'' +
                ", designNo='" + designNo + '\'' +
                ", stock=" + stock +
                ", category='" + category + '\'' +
                ", size='" + size + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
