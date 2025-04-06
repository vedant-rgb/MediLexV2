package com.medilexV2.medPlus.service;

import com.medilexV2.medPlus.dto.BillingDTO;
import com.medilexV2.medPlus.dto.BillingInfo;
import com.medilexV2.medPlus.dto.Products;
import com.medilexV2.medPlus.entity.Medical;
import com.medilexV2.medPlus.exceptions.ResourceNotFoundException;
import com.medilexV2.medPlus.repository.MedicalRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicalService {

    private final MedicalRepository medicalRepository;

    public MedicalService(MedicalRepository medicalRepository) {
        this.medicalRepository = medicalRepository;
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





    private Medical getCurrentuser(){
        return (Medical) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
