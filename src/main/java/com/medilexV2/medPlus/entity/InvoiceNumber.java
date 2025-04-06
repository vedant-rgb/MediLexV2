package com.medilexV2.medPlus.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "InvoiceNumber")
public class InvoiceNumber {
    @Id
    private String id;

    private String invoiceNumber;

    public InvoiceNumber() {
    }

    public InvoiceNumber(String id, String invoiceNumber) {
        this.id = id;
        this.invoiceNumber = invoiceNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }
}
