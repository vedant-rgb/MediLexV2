package com.medilexV2.medPlus.controller;

import com.medilexV2.medPlus.dto.Products;
import com.medilexV2.medPlus.thirdPartyService.OcrService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/medical")
public class MedicalController {
    private final OcrService ocrService;
    Logger logger = org.apache.logging.log4j.LogManager.getLogger(MedicalController.class);


    public MedicalController(OcrService ocrService) {
        this.ocrService = ocrService;
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

}
