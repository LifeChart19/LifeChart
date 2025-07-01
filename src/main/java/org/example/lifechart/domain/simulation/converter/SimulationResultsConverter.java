package org.example.lifechart.domain.simulation.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.example.lifechart.domain.simulation.dto.response.SimulationResults;

import java.io.IOException;

@Converter
public class SimulationResultsConverter implements AttributeConverter<SimulationResults, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    public String convertToDatabaseColumn(SimulationResults attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("SimulationResults 직렬화 실패", e);
        }
    }

    @Override
    public SimulationResults convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, SimulationResults.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("SimulationResults 역직렬화 실패", e);
        }
    }
}