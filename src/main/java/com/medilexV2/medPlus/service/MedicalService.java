package com.medilexV2.medPlus.service;

import com.medilexV2.medPlus.dto.*;
import com.medilexV2.medPlus.entity.Medical;
import com.medilexV2.medPlus.exceptions.ResourceNotFoundException;
import com.medilexV2.medPlus.repository.MedicalLocationRepository;
import com.medilexV2.medPlus.repository.MedicalRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class MedicalService {

    private final MongoTemplate mongoTemplate;
    private final PhotoUploadService photoUploadService;
    private final MedicalRepository medicalRepository;
    private final MedicalLocationRepository medicalLocationRepository;
    private final Logger logger = Logger.getLogger(MedicalService.class.getName());
    private final ModelMapper modelMapper;

    public MedicalService(MongoTemplate mongoTemplate, PhotoUploadService photoUploadService, MedicalRepository medicalRepository, MedicalLocationRepository medicalLocationRepository, ModelMapper modelMapper) {
        this.mongoTemplate = mongoTemplate;
        this.photoUploadService = photoUploadService;
        this.medicalRepository = medicalRepository;
        this.medicalLocationRepository = medicalLocationRepository;
        this.modelMapper = modelMapper;
    }

    public List<Products> getProducts() {
        Medical currentuser = getCurrentuser();
        Optional<Medical> medical = medicalRepository.findByEmail(currentuser.getEmail());

        if (medical.isEmpty()) {
            throw new ResourceNotFoundException("No medical found for email " + currentuser.getUsername());
        }

        return medical.get().getProducts();
    }

    public Integer getProductsCount() {
        Medical currentuser = getCurrentuser();
        Optional<Medical> medical = medicalRepository.findByEmail(currentuser.getEmail());

        if (medical.isEmpty()) {
            throw new ResourceNotFoundException("No medical found for email " + currentuser.getUsername());
        }

        return medical.get().getProducts().size();
    }

    public List<AllProducts> getAllProducts() {
        List<Medical> medicals = medicalRepository.findAll();
        List<AllProducts> allProducts = new ArrayList<>();
        for (Medical medical : medicals) {
            List<Products> products = medical.getProducts();
            products.stream()
                    .map(entry->modelMapper.map(entry, AllProducts.class))
                    .forEach(allProducts::add);
        }
        if (allProducts.isEmpty()) {
            throw new ResourceNotFoundException("No products found");
        }
        return allProducts;
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
                    int currentQty = product.getCurrentStock() != null ? product.getCurrentStock() : 0;
                    int newQty = currentQty - billingInfo.getQty();

                    if (newQty < 0) {
                        throw new IllegalArgumentException("Insufficient stock for product: " + billingInfo.getProductName());
                    }

                    product.setCurrentStock(newQty);
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

    public List<LowStockDTO> getLowStockItems(){
        Medical currentuser = getCurrentuser();
        Optional<Medical> medical = medicalRepository.findByEmail(currentuser.getEmail());
        if (medical.isEmpty()) {
            throw new ResourceNotFoundException("No medical found for email " + currentuser.getUsername());
        }
        List<Products> products = medical.get().getProducts();
        return products.stream()
                .filter(product->givePercentageValue(product.getCurrentStock(),product.getQty()) <20.00 )
                .map(products1 -> {
                    LowStockDTO lowStockDTO = new LowStockDTO();
                    lowStockDTO.setProductName(products1.getProductName());
                    lowStockDTO.setCurrentStockQty(products1.getCurrentStock());
                    lowStockDTO.setOriginalStocksQty(products1.getQty());
                    return lowStockDTO;
                })
                .toList();
    }

    public List<ExpiringProductDTO> getExpiredAndExpiringProductsNext10Days() {
        Medical currentuser = getCurrentuser();
        Optional<Medical> optionalMedical = medicalRepository.findByEmail(currentuser.getEmail());

        if (optionalMedical.isEmpty()) {
            throw new ResourceNotFoundException("No medical found for email " + currentuser.getUsername());
        }

        Medical medical = optionalMedical.get();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        LocalDate today = LocalDate.now();
        LocalDate tenDaysLater = today.plusDays(10);

        List<ExpiringProductDTO> result = new ArrayList<>();

        for (Products product : medical.getProducts()) {
            try {
                LocalDate expDate = LocalDate.parse(product.getExp(), formatter).withYear(today.getYear());

                // Adjust expired logic if needed (e.g., Dec → Jan)
                if (!expDate.isAfter(tenDaysLater)) {
                    result.add(new ExpiringProductDTO(medical.getMedicalName(), product));
                }
            } catch (Exception e) {
                logger.warning("Invalid date format for: " + product.getProductName() + " - " + product.getExp());
            }
        }

        return result;
    }



    private Double givePercentageValue(int currentStock, int totalStock){
        if(currentStock == 0){
            return 0.0;
        }
        return ((double) currentStock / totalStock) * 100;
    }

    private Medical getCurrentuser(){
        return (Medical) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }


    public Products addProduct(Products product) {
        Medical currentuser = getCurrentuser();
        Optional<Medical> medical = medicalRepository.findByEmail(currentuser.getEmail());

        if (medical.isEmpty()) {
            throw new ResourceNotFoundException("No medical found for email " + currentuser.getUsername());
        }

        Medical toBeUpdate = medical.get();
        List<Products> products = toBeUpdate.getProducts();

        products.add(product);
        toBeUpdate.setProducts(products);

        medicalRepository.save(toBeUpdate);

        return product;
    }
}
