package com.medilexV2.medPlus.repository;

import com.medilexV2.medPlus.entity.InvoiceNumber;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface InvoiceNumberRepository extends MongoRepository<InvoiceNumber,String>{
    Optional<InvoiceNumber> findByInvoiceNumber(String invoiceNumber);
}
