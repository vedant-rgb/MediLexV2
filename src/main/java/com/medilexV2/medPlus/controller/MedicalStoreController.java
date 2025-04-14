package com.medilexV2.medPlus.controller;

import com.medilexV2.medPlus.dto.*;
import com.medilexV2.medPlus.entity.Medical;
import com.medilexV2.medPlus.exceptions.ResourceNotFoundException;
import com.medilexV2.medPlus.repository.MedicalLocationRepository;
import com.medilexV2.medPlus.repository.MedicalRepository;
import com.medilexV2.medPlus.service.ExcelSheetDownloadService;
import com.medilexV2.medPlus.service.MedicalService;
import com.medilexV2.medPlus.thirdPartyService.OcrService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/medical")
public class MedicalStoreController {
    private final OcrService ocrService;
    private final MedicalService medicalService;
    private final ExcelSheetDownloadService excelSheetDownloadService;
    private final MedicalRepository medicalRepository;
    Logger logger = org.apache.logging.log4j.LogManager.getLogger(MedicalStoreController.class);
    private final MedicalLocationRepository medicalLocationRepository;

    public MedicalStoreController(OcrService ocrService, MedicalService medicalService, ExcelSheetDownloadService excelSheetDownloadService, MedicalRepository medicalRepository,
                                  MedicalLocationRepository medicalLocationRepository) {
        this.ocrService = ocrService;
        this.medicalService = medicalService;
        this.excelSheetDownloadService = excelSheetDownloadService;
        this.medicalRepository = medicalRepository;
        this.medicalLocationRepository = medicalLocationRepository;
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

    @PostMapping("/addProduct")
    public ResponseEntity<Products> addProduct(@RequestBody Products product) {
        Products savedProduct = medicalService.addProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    @GetMapping("/products")
    public ResponseEntity<List<Products>> getAllProductsForMedical() {
        List<Products> products = medicalService.getProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/allProducts")
    public ResponseEntity<List<AllProducts>> getAllProducts() {
        List<AllProducts> products = medicalService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/productCount")
    public ResponseEntity<Integer> getProductCount() {
        int count = medicalService.getProductsCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/lowStockProducts")
    public ResponseEntity<List<Products>> getLowStockProducts() {
        List<Products> lowStockProducts = medicalService.getLowStockItems();
        return ResponseEntity.ok(lowStockProducts);
    }

    @GetMapping("/getExpiringProducts")
    public ResponseEntity<List<ExpiringProductDTO>> getExpiringProducts() {
        List<ExpiringProductDTO> expiringProducts = medicalService.getExpiredAndExpiringProductsNext10Days();
        return ResponseEntity.ok(expiringProducts);
    }

    @GetMapping("/getRecentOrders")
    public ResponseEntity<List<RecentOrders>> getAllRecentOrders() {
        return ResponseEntity.ok(medicalService.getRecentOrders());
    }


    @PutMapping("/product/update")
    public ResponseEntity<Products> updateProduct(@RequestBody Products product) {
        Products updated = medicalService.updateProductField(product);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/product/delete")
    public ResponseEntity<String> deleteProduct(
            @RequestParam String hsn,
            @RequestParam String productName,
            @RequestParam String batch,
            @RequestParam String exp) {

        medicalService.deleteProduct(hsn, productName, batch, exp);
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


    @GetMapping("/{id}")
    public ResponseEntity<Medical> getMedicalById(@PathVariable String id) {
        Medical medical = medicalService.getMedicalById(id);
        return ResponseEntity.ok(medical);
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
