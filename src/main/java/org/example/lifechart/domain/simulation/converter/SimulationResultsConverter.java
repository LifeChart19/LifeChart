package org.example.lifechart.domain.simulation.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.example.lifechart.domain.simulation.entity.SimulationResults;

import java.io.IOException;

@Converter
public class SimulationResultsConverter implements AttributeConverter<SimulationResults, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

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