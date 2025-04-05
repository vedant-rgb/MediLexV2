package com.medilexV2.medPlus.dto;

import lombok.Data;
import java.util.List;

public class ProductsResponse {
    private List<Products> products;

    public ProductsResponse() {
    }

    public ProductsResponse(List<Products> products) {
        this.products = products;
    }

    public List<Products> getProducts() {
        return products;
    }

    public void setProducts(List<Products> products) {
        this.products = products;
    }
}
