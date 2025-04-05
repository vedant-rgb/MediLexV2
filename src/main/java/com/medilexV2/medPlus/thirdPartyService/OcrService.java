package com.medilexV2.medPlus.thirdPartyService;

import com.medilexV2.medPlus.dto.Products;
import com.medilexV2.medPlus.entity.Medical;
import com.medilexV2.medPlus.entity.User;
import com.medilexV2.medPlus.exceptions.ResourceNotFoundException;
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
    Logger logger = org.apache.logging.log4j.LogManager.getLogger(OcrService.class);
    private final String flaskApiUrl = "http://localhost:5000/process_file";


    public OcrService(RestTemplate restTemplate, MedicalRepository medicalRepository) {
        this.restTemplate = restTemplate;
        this.medicalRepository = medicalRepository;
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
            List<Products> response = restTemplate.exchange(
                    flaskApiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<List<Products>>() {}
            ).getBody();
            logger.info("Received response: " + response);

            Medical medical = medicalRepository.findByEmail(getCurrentuser().getEmail()).orElseThrow(()->new ResourceNotFoundException("No medical found"));
            medical.setProducts(response);
            Medical saved = medicalRepository.save(medical);
            logger.info(saved);
            return response;


        } catch (ResourceAccessException e) {
            throw new IOException("Failed to connect to Flask server. Is it running at " + flaskApiUrl + "?", e);
        }
    }


    private User getCurrentuser(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }


}
