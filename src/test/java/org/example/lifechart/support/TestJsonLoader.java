package org.example.lifechart.support;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestJsonLoader {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static <T> T loadJson(String path, TypeReference<T> typeReference) {
		try (InputStream is = new ClassPathResource(path).getInputStream()) {
			return objectMapper.readValue(is, typeReference);
		} catch (IOException e) {
			throw new RuntimeException("Failed to load JSON from " + path, e);
		}
	}
}
