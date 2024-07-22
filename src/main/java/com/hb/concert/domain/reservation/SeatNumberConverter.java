package com.hb.concert.domain.reservation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.List;

@Converter
public class SeatNumberConverter implements AttributeConverter<List<Integer>, String> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Integer> attribute) {
        try {
            return mapper.writeValueAsString(attribute);
        } catch (IOException ioe) {
            throw new IllegalArgumentException("Error Seat List to JSON", ioe);
        }
    }

    @Override
    public List<Integer> convertToEntityAttribute(String dbData) {
        try {
            return mapper.readValue(dbData, new TypeReference<List<Integer>>(){});
        } catch (IOException ioe) {
            throw new IllegalArgumentException("Error JSON to Seat List", ioe);
        }
    }
}
