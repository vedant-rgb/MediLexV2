package com.medilexV2.medPlus.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "InvoiceNumber")
public class InvoiceNumber {
    @Id
    private String id;

    private String invoiceNumber;

    private String email;

    public InvoiceNumber() {
    }

    public InvoiceNumber(String id, String invoiceNumber,String email) {
        this.id = id;
        this.invoiceNumber = invoiceNumber;
        this.email=email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
