package at.fhv.se.collabnotes.integration.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class RestTestUtils {
    
    public static HttpEntity<String> requestWithJsonFor(Object o) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String addItemFormJson = objectMapper.writeValueAsString(o);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<String>(addItemFormJson, headers);
        return request;
    }

    public static HttpEntity<String> requestWithPlainText(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        HttpEntity<String> request = new HttpEntity<String>(text, headers);
        return request;
    }
}
