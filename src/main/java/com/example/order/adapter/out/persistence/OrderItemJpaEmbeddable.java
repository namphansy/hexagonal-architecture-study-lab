package com.example.order.adapter.out.persistence;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class OrderItemJpaEmbeddable {

    private String productId;
    private int quantity;
    private BigDecimal price;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
