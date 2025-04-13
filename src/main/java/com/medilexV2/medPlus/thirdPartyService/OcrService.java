package com.medilexV2.medPlus.thirdPartyService;

import com.medilexV2.medPlus.dto.OcrResponse;
import com.medilexV2.medPlus.dto.Products;
import com.medilexV2.medPlus.entity.InvoiceNumber;
import com.medilexV2.medPlus.entity.Medical;
import com.medilexV2.medPlus.exceptions.ResourceNotFoundException;
import com.medilexV2.medPlus.repository.InvoiceNumberRepository;
import com.medilexV2.medPlus.repository.MedicalRepository;
import org.apache.logging.log4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class OcrService {

    private final RestTemplate restTemplate;
    private final MedicalRepository medicalRepository;
    private final InvoiceNumberRepository invoiceNumberRepository;
    Logger logger = org.apache.logging.log4j.LogManager.getLogger(OcrService.class);
    private final String flaskApiUrl = "http://localhost:5000/process_file";

    public OcrService(RestTemplate restTemplate, MedicalRepository medicalRepository, InvoiceNumberRepository invoiceNumberRepository) {
        this.restTemplate = restTemplate;
        this.medicalRepository = medicalRepository;
        this.invoiceNumberRepository = invoiceNumberRepository;

    }

    public List<Products> processReceipt(MultipartFile file) throws IOException {
        try {
            logger.info("Processing file: " + file.getOriginalFilename());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            logger.info("Setting headers: " + headers);
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            logger.info("Setting body: " + body);
            body.add("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            });
            logger.info("Adding file to body: " + file.getOriginalFilename());

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            logger.info("Creating request entity: " + requestEntity);

            ResponseEntity<OcrResponse> responseEntity = restTemplate.exchange(
                    flaskApiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<OcrResponse>() {}
            );

            OcrResponse ocrResponse = responseEntity.getBody();
            logger.info("Received response: " + ocrResponse); // Log the full OcrResponse object

            if (ocrResponse == null) {
                throw new IOException("Empty response from Flask API");
            }

            // Extract products and invoice number
            List<Products> products = ocrResponse.getProducts();
            for (Products product : products) {
                product.setCurrentStock(product.getQty()); // set default current stock
            }

            String invoiceNumber = ocrResponse.getInvoiceNumber();
            logger.info("Extracted invoice number: " + invoiceNumber); // Should now show "CR/98"
            logger.info("Extracted products: " + products);

            Medical currentUser = getCurrentUser();
            logger.info("Current user: " + currentUser);

            Optional<InvoiceNumber> byInvoiceNumber = invoiceNumberRepository.findByInvoiceNumberAndEmail(invoiceNumber, currentUser.getEmail());
            if(byInvoiceNumber.isPresent()){
                logger.info("Invoice number already exists in database: " + invoiceNumber);
                throw new RuntimeException("This Bill is already uploaded in database");
            }
            InvoiceNumber invoiceNumberEntity = new InvoiceNumber();
            invoiceNumberEntity.setInvoiceNumber(invoiceNumber);
            invoiceNumberEntity.setEmail(currentUser.getEmail());
            invoiceNumberRepository.save(invoiceNumberEntity);



            Medical medical = medicalRepository.findByEmail(currentUser.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("No medical found"));
            logger.info("Medical email: " + medical.getUsername());

            medical.getProducts().addAll(products);


            Medical saved = medicalRepository.save(medical);
            logger.info("Saved Medical email: " + saved.getUsername());
            logger.info(saved);

            return products;

        } catch (ResourceAccessException e) {
            throw new IOException("Failed to connect to Flask server. Is it running at " + flaskApiUrl + "?", e);
        }
    }

    private Medical getCurrentUser() {
        return (Medical) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}