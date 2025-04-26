package com.example.eowa;

import com.example.eowa.model.WebToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class WebTokenConverter implements Converter<String, WebToken> {
    @Override
    public WebToken convert(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, WebToken.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
