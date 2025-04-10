package com.medilexV2.medPlus.controller;

import com.medilexV2.medPlus.dto.BillingDTO;
import com.medilexV2.medPlus.dto.Products;
import com.medilexV2.medPlus.entity.Medical;
import com.medilexV2.medPlus.exceptions.ResourceNotFoundException;
import com.medilexV2.medPlus.repository.MedicalRepository;
import com.medilexV2.medPlus.service.ExcelSheetDownloadService;
import com.medilexV2.medPlus.service.MedicalService;
import com.medilexV2.medPlus.thirdPartyService.OcrService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/medical")
public class MedicalController {
    private final OcrService ocrService;
    private final MedicalService medicalService;
    private final ExcelSheetDownloadService excelSheetDownloadService;
    private final MedicalRepository medicalRepository;
    Logger logger = org.apache.logging.log4j.LogManager.getLogger(MedicalController.class);

    public MedicalController(OcrService ocrService, MedicalService medicalService, ExcelSheetDownloadService excelSheetDownloadService, MedicalRepository medicalRepository) {
        this.ocrService = ocrService;
        this.medicalService = medicalService;
        this.excelSheetDownloadService = excelSheetDownloadService;
        this.medicalRepository = medicalRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<List<Products>> uploadFile(@RequestParam("file") MultipartFile file) throws Exception {

//        logger.info(file.getName());
        logger.info(file.isEmpty());
        if (file.isEmpty()) {
            logger.error("File is empty");
            return ResponseEntity.badRequest().body(null); // Or an empty list with a message
        }
        try {
            logger.info("File name: " + file.getOriginalFilename());
            List<Products> products = ocrService.processReceipt(file);
            products.forEach(entry-> System.out.println(entry));
            return ResponseEntity.ok(products);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Or an error message
        }
    }

    @GetMapping("/products")
    public ResponseEntity<List<Products>> getAllProductsForMedical() {
        List<Products> products = medicalService.getProducts();
        return ResponseEntity.ok(products);
    }

    @PutMapping("/product/update")
    public ResponseEntity<Products> updateProduct(@RequestBody Products product) {
        Products updated = medicalService.updateProductField(product);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/product/delete")
    public ResponseEntity<String> deleteProduct(
            @RequestParam String HSN,
            @RequestParam String productName,
            @RequestParam String batch,
            @RequestParam String exp) {

        medicalService.deleteProduct(HSN, productName, batch, exp);
        return ResponseEntity.ok("Product deleted successfully.");
    }



    @GetMapping("/downloadExcel")
    public void downloadExcel(HttpServletResponse response) throws IOException {
        List<Products> products = medicalService.getProducts();
        excelSheetDownloadService.exportToExcel(products, response);
    }

    @PostMapping("/billing")
    public ResponseEntity<String> processBilling(@RequestBody BillingDTO billingDTO) {
        try {
            medicalService.processBilling(billingDTO);
            return ResponseEntity.ok("Billing processed successfully. Stock updated.");
        } catch (IllegalArgumentException | ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing billing.");
        }
    }

    @GetMapping("/getAllMedical")
    public ResponseEntity<?> getAllMedical() {
        List<Medical> medicals = medicalService.getAllMedical();
        return ResponseEntity.ok(medicals);
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<Document>> findNearbyMedicalsWithMedicine(
            @RequestParam String medicineName,
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "2000") double radiusInMeters) {

        List<Document> results = medicalRepository.findNearbyMedicalsWithMedicine(medicineName, lat, lng, radiusInMeters);
        return ResponseEntity.ok(results);
    }

//    @PostMapping("/update-location")
//    public ResponseEntity<Medical> updateLocation(@RequestBody LocationUpdateDto locationUpdateDto) {
//        Medical updatedMedical = medicalService.updateLocation(locationUpdateDto);
//        return ResponseEntity.ok(updatedMedical);
//    }
    @GetMapping("/{id}")
    public ResponseEntity<Medical> getMedicalById(@PathVariable String id) {
        Medical medical = medicalService.getMedicalById(id);
        return ResponseEntity.ok(medical);
    }

    @GetMapping("/nearest-medical")
    public ResponseEntity<List<Medical>> getNearestMedical(@RequestParam double latitude, @RequestParam double longitude,@RequestParam String productName) {
        List<Medical> nearestMedicals = medicalService.getNearestMedical(latitude, longitude,productName);
        return ResponseEntity.ok(nearestMedicals);
    }

    @PostMapping("/upload-photos")
    public Medical uploadPhotos(@RequestParam("files") List<MultipartFile> files) throws IOException {
        return medicalService.uploadMedicalPhotos(files);
    }

    @GetMapping("/get-all-photos")
    public List<String> getAllPhotos() {
        return medicalService.getAllPhotos();
    }



}
