package com.medilexV2.medPlus.service;

import com.medilexV2.medPlus.dto.*;
import com.medilexV2.medPlus.entity.Medical;
import com.medilexV2.medPlus.entity.Users;
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
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
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
        Users currentuser = getCurrentUser();
        Optional<Medical> medical = medicalRepository.findByEmail(currentuser.getEmail());

        if (medical.isEmpty()) {
            throw new ResourceNotFoundException("No medical found for email " + currentuser.getUsername());
        }

        return medical.get().getProducts();
    }

    public Integer getProductsCount() {
        Users currentuser = getCurrentUser();
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
        Users currentuser = getCurrentUser();
        Optional<Medical> medicalOpt = medicalRepository.findByEmail(currentuser.getEmail());

        if (medicalOpt.isEmpty()) {
            throw new ResourceNotFoundException("No medical found for email " + currentuser.getUsername());
        }

        Medical medical = medicalOpt.get();
        List<Products> products = medical.getProducts();

        boolean updated = false;

        for (Products existingProduct : products) {
            if (existingProduct.getHsn().equals(product.getHsn()) &&
                    existingProduct.getProductName().equals(product.getProductName()) &&
                    existingProduct.getBatch().equals(product.getBatch()) &&
                    existingProduct.getExp().equals(product.getExp())) {

                // Set all fields from input
                existingProduct.setMfg(product.getMfg());
                existingProduct.setUnit(product.getUnit());
                existingProduct.setQty(product.getQty());
                existingProduct.setCurrentStock(product.getCurrentStock());
                existingProduct.setSch(product.getSch());
                existingProduct.setMrp(product.getMrp());
                existingProduct.setRate(product.getRate());
                existingProduct.setPtr(product.getPtr());
                existingProduct.setGst(product.getGst());
                existingProduct.setAmount(product.getAmount());

                updated = true;
                break;
            }
        }

        if (!updated) {
            throw new ResourceNotFoundException("Product not found for update");
        }

        medicalRepository.save(medical);
        return product;
    }


    public void deleteProduct(String hsn, String productName, String batch, String exp) {
        Users currentuser = getCurrentUser();
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

    public List<RecentOrders> getRecentOrders() {
        Users currentUser = getCurrentUser();
        Medical medical = medicalRepository.findByEmail(currentUser.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No medical found for email " + currentUser.getUsername()));

        List<RecentOrders> recentOrders = medical.getRecentOrders();

        if (recentOrders == null) {
            return new ArrayList<>();
        }

        // Sort by createdAt descending
        recentOrders.sort(Comparator.comparing(RecentOrders::getCreatedAt).reversed());
        return recentOrders;
    }



    public void processBilling(BillingDTO billingDTO) {
        Users currentuser = getCurrentUser();
        Optional<Medical> medicalOpt = medicalRepository.findByEmail(currentuser.getEmail());

        if (medicalOpt.isEmpty()) {
            throw new ResourceNotFoundException("No medical found for email " + currentuser.getUsername());
        }

        Medical medical = medicalOpt.get();
        List<Products> products = medical.getProducts();
        List<RecentOrders> recentOrders = medical.getRecentOrders();
        RecentOrders orders = new RecentOrders(billingDTO.getCustomerName(), billingDTO.getTotalAmount());
        orders.setCreatedAt(LocalDateTime.now());
        recentOrders.add(orders);

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
        Medical medical = medicalRepository.findById(getCurrentUser().getId()).orElseThrow(() -> new RuntimeException("Medical not found"));

        List<String> photoUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            String url = photoUploadService.uploadPhoto(file);
            photoUrls.add(url);
        }

        medical.setPhotos(photoUrls);
        return medicalRepository.save(medical);
    }

    public List<String> getAllPhotos() {
        Medical medical = medicalRepository.findById(getCurrentUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Medical not found"));
        List<String> photos = medical.getPhotos();
        if (photos == null || photos.isEmpty()) {
            throw new ResourceNotFoundException("No photos found for this medical");
        }
        return photos;
    }

    public List<Products> getLowStockItems() {
        Users currentuser = getCurrentUser();
        Medical medical = medicalRepository.findByEmail(currentuser.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No medical found for email " + currentuser.getUsername()));

        List<Products> products = medical.getProducts();

        return products.stream()
                .filter(product -> {
                    System.out.println(product.getProductName());
                    System.out.println(product.getCurrentStock());
                    System.out.println(product.getQty());
                    return calculateStockPercentage(product.getCurrentStock(), product.getQty()) < 20.0;
                })
                .toList();
    }

    private Double calculateStockPercentage(int currentStock, int totalStock) {
        if (totalStock == 0) return 0.0;
        return ((double) currentStock / totalStock) * 100;
    }


    public List<Products> getExpiredAndExpiringProductsNext10Days() {
        Users currentuser = getCurrentUser();
        Optional<Medical> optionalMedical = medicalRepository.findByEmail(currentuser.getEmail());

        if (optionalMedical.isEmpty()) {
            throw new ResourceNotFoundException("No medical found for email " + currentuser.getUsername());
        }

        Medical medical = optionalMedical.get();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yy");
        LocalDate today = LocalDate.now();
        LocalDate tenDaysLater = today.plusDays(10);

        List<Products> result = new ArrayList<>();

        for (Products product : medical.getProducts()) {
            try {
                YearMonth yearMonth = YearMonth.parse(product.getExp(), formatter);
                LocalDate expDate = yearMonth.atEndOfMonth(); // Sets expiry as last day of that month

                // Match expired or expiring within 10 days
                if (!expDate.isAfter(tenDaysLater)) {
                    result.add(product);
                }
            } catch (Exception e) {
                logger.warning("Invalid expiry format for product: " + product.getProductName() + " - " + product.getExp());
            }
        }

        return result;
    }


    private Users getCurrentUser() {
        return (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }



    public Products addProduct(Products product) {
        Users currentuser = getCurrentUser();
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
