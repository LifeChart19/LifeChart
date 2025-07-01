package org.example.lifechart.domain.simulation.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.example.lifechart.domain.simulation.dto.response.SimulationParams;

import java.io.IOException;

@Converter
public class SimulationParamsConverter implements AttributeConverter<SimulationParams, String> {

    //직렬화를 위해 Jackson의 ObjectMapper객체를 생성해야함.
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    //attribute는 convert가 붙은 필드를 db에 저장하기 전 자동 호출. simulation엔티티 params필드임.
    public String convertToDatabaseColumn(SimulationParams attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("SimulationParams 직렬화 실패", e);
        }
    }

    public SimulationParams convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, SimulationParams.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("SimulationParams 역직렬화 실패", e);
        }
    }


}
