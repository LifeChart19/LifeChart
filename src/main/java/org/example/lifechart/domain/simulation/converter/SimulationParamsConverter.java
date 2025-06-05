package org.example.lifechart.domain.simulation.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.example.lifechart.domain.simulation.entity.SimulationParams;

import java.io.IOException;

@Converter
public class SimulationParamsConverter implements AttributeConverter<SimulationParams, String> {
    
    //직렬화를 위해 Jackson의 ObjectMapper객체를 생성해야함.
    private final ObjectMapper objectMapper = new ObjectMapper();

    //attribute는 convert가 붙은 필드를 db에 저장하기 전 자동 호출. simulation엔티티 params필드임.
    public String convertToDatabaseColumn(SimulationParams attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("직렬화 실패",e);
        }
    }
    public SimulationParams convertToEntityAttribute(String dbData) {
        try {
            //db에서 읽은 JSON문자열을 다시 자바 객체로 변환 -> get가능
            return objectMapper.readValue(dbData, SimulationParams.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("역직렬화 실패", e);
        }
    }



    //
}
