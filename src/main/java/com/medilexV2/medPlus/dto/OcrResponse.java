package com.medilexV2.medPlus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class OcrResponse {
    @JsonProperty("invoice_number") // Maps "invoice_number" from JSON to invoiceNumber in Java
    private String invoiceNumber;
    private List<Products> products;

    // Getters and Setters
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public List<Products> getProducts() {
        return products;
    }

    public void setProducts(List<Products> products) {
        this.products = products;
    }

    @Override
    public String toString() {
        return "OcrResponse{" +
                "invoiceNumber='" + invoiceNumber + '\'' +
                ", products=" + products +
                '}';
    }
}