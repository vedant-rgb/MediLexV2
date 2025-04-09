package com.medilexV2.medPlus.service;

import com.medilexV2.medPlus.dto.*;
import com.medilexV2.medPlus.entity.Medical;
import com.medilexV2.medPlus.entity.MedicalLocation;
import com.medilexV2.medPlus.exceptions.ResourceNotFoundException;
import com.medilexV2.medPlus.repository.MedicalLocationRepository;
import com.medilexV2.medPlus.repository.MedicalRepository;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@Service
public class MedicalService {

    private final MongoTemplate mongoTemplate;
    private final PhotoUploadService photoUploadService;
    private final MedicalRepository medicalRepository;
    private final MedicalLocationRepository medicalLocationRepository;
    private final Logger logger = Logger.getLogger(MedicalService.class.getName());

    public MedicalService(MongoTemplate mongoTemplate, PhotoUploadService photoUploadService, MedicalRepository medicalRepository, MedicalLocationRepository medicalLocationRepository) {
        this.mongoTemplate = mongoTemplate;
        this.photoUploadService = photoUploadService;
        this.medicalRepository = medicalRepository;
        this.medicalLocationRepository = medicalLocationRepository;
    }

    public List<Products> getProducts() {
        Medical currentuser = getCurrentuser();
        Optional<Medical> medical = medicalRepository.findByEmail(currentuser.getEmail());

        if (medical.isEmpty()) {
            throw new ResourceNotFoundException("No medical found for email " + currentuser.getUsername());
        }

        return medical.get().getProducts();
    }

    public Products updateProductField(Products product) {
        Medical currentuser = getCurrentuser();
        Optional<Medical> medical = medicalRepository.findByEmail(currentuser.getEmail());

        if (medical.isEmpty()) {
            throw new ResourceNotFoundException("No medical found for email " + currentuser.getUsername());
        }

        Medical toBeUpdate = medical.get();
        List<Products> products = toBeUpdate.getProducts();

        Products[] updatedProduct = new Products[1]; // to capture result from lambda

        products.stream()
                .filter(entry ->
                        entry.getHsn().equals(product.getHsn()) &&
                                entry.getProductName().equals(product.getProductName()) &&
                                entry.getBatch().equals(product.getBatch()) &&
                                entry.getExp().equals(product.getExp())
                )
                .findFirst()
                .ifPresentOrElse(found -> {
                    found.setHsn(product.getHsn());
                    found.setProductName(product.getProductName());
                    found.setAmount(product.getAmount());
                    found.setBatch(product.getBatch());
                    found.setExp(product.getExp());
                    found.setGst(product.getGst());
                    found.setMrp(product.getMrp());
                    found.setMfg(product.getMfg());
                    found.setPtr(product.getPtr());
                    found.setQty(product.getQty());
                    found.setSch(product.getSch());
                    found.setUnit(product.getUnit());
                    found.setRate(product.getRate());
                    updatedProduct[0] = found;
                }, () -> {
                    throw new ResourceNotFoundException("No product with given constraints found");
                });

        medicalRepository.save(toBeUpdate);

        return updatedProduct[0];
    }

    public void deleteProduct(String hsn, String productName, String batch, String exp) {
        Medical currentuser = getCurrentuser();
        Optional<Medical> medicalOpt = medicalRepository.findByEmail(currentuser.getEmail());

        if (medicalOpt.isEmpty()) {
            throw new ResourceNotFoundException("No medical found for email " + currentuser.getUsername());
        }

        Medical medical = medicalOpt.get();
        List<Products> products = medical.getProducts();

        boolean removed = products.removeIf(product ->
                product.getHsn().equals(hsn) &&
                        product.getProductName().equals(productName) &&
                        product.getBatch().equals(batch) &&
                        product.getExp().equals(exp)
        );

        if (!removed) {
            throw new ResourceNotFoundException("No matching product found to delete");
        }

        // Save updated medical record
        medicalRepository.save(medical);
    }

    public void processBilling(BillingDTO billingDTO) {
        Medical currentUser = getCurrentuser();
        Optional<Medical> medicalOpt = medicalRepository.findByEmail(currentUser.getEmail());

        if (medicalOpt.isEmpty()) {
            throw new ResourceNotFoundException("No medical found for email " + currentUser.getUsername());
        }

        Medical medical = medicalOpt.get();
        List<Products> products = medical.getProducts();

        for (BillingInfo billingInfo : billingDTO.getBillingInfoList()) {
            boolean updated = false;

            for (Products product : products) {
                if (product.getProductName().equalsIgnoreCase(billingInfo.getProductName())) {
                    int currentQty = product.getQty() != null ? product.getQty() : 0;
                    int newQty = currentQty - billingInfo.getQty();

                    if (newQty < 0) {
                        throw new IllegalArgumentException("Insufficient stock for product: " + billingInfo.getProductName());
                    }

                    product.setQty(newQty);
                    updated = true;
                    break;
                }
            }

            if (!updated) {
                throw new ResourceNotFoundException("Product not found: " + billingInfo.getProductName());
            }
        }

        medicalRepository.save(medical);
    }

    public Medical getMedicalById(String id) {
        return medicalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medical store not found"));
    }

    public List<Medical> getAllMedicals() {
        List<Medical> medicals = medicalRepository.findAll();
        if (medicals.isEmpty()) {
            throw new ResourceNotFoundException("No medicals found");
        }
        return medicals;

    }



    public List<Medical> getAllMedical() {
        List<Medical> medicals = medicalRepository.findAll();
        if (medicals.isEmpty()) {
            throw new ResourceNotFoundException("No medicals found");
        }
        return medicals;
    }

    public Medical uploadMedicalPhotos(List<MultipartFile> files) throws IOException {
        Medical medical = medicalRepository.findById(getCurrentuser().getId()).orElseThrow(() -> new RuntimeException("Medical not found"));

        List<String> photoUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            String url = photoUploadService.uploadPhoto(file);
            photoUrls.add(url);
        }

        medical.setPhotos(photoUrls);
        return medicalRepository.save(medical);
    }

    public List<String> getAllPhotos() {
        Medical medical = medicalRepository.findById(getCurrentuser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Medical not found"));
        List<String> photos = medical.getPhotos();
        if (photos == null || photos.isEmpty()) {
            throw new ResourceNotFoundException("No photos found for this medical");
        }
        return photos;
    }




    private Medical getCurrentuser(){
        return (Medical) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
