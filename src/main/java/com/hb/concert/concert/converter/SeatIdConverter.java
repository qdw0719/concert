package com.hb.concert.concert.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.List;

@Converter
public class SeatIdConverter implements AttributeConverter<List<Integer>, String> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Integer> attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return mapper.writeValueAsString(attribute);
        } catch (IOException ioe) {
            throw new IllegalArgumentException("Error converting seat list to JSON", ioe);
        }
    }

    @Override
    public List<Integer> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        try {
            return mapper.readValue(dbData, new TypeReference<List<Integer>>() {});
        } catch (IOException ioe) {
            throw new IllegalArgumentException("Error converting JSON to seat list", ioe);
        }
    }
}
