package org.example.lifechart.domain.goal.service;

import java.net.SocketTimeoutException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.goal.dto.response.ApartmentPriceDto;
import org.example.lifechart.domain.goal.enums.RegionCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenApiApartmentPriceService {

	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;

	@Value("${openapi.kosis.key}")
	private String apiKey;

	private static final String BASE_URL = "https://kosis.kr/openapi/Param/statisticsParameterData.do";
	private static final String DEFAULT_PARAMS =
		"?method=getList" +
			"&itmId=T001+" +
			"&objL1=ALL" +
			"&objL2=&objL3=&objL4=&objL5=&objL6=&objL7=&objL8=" +
			"&format=json&jsonVD=Y&prdSe=M" +
			"&outputFields=OBJ_ID+OBJ_NM+NM+ITM_ID+ITM_NM+UNIT_NM+PRD_SE+PRD_DE+LST_CHN_DE+" +
			"&orgId=408&tblId=DT_KAB_11672_S15";

	@Retryable(
		value = {SocketTimeoutException.class},
		maxAttempts = 3,
		backoff = @Backoff(delay = 2000)
	)
	public ApartmentPriceDto fetchLatest(String region, String subregion) {
		String API_URL = buildUrl(120); // 최근 120개월
		try {
			String response = restTemplate.getForObject(API_URL, String.class);
			List<Map<String, Object>> dataList = objectMapper.readValue(response, new TypeReference<>() {});

			// 최신 데이터 추출
			Map<String, Object> latestEntry = dataList.stream()
				.filter(item -> subregion.equals(item.get("C1_NM")))
				.max(Comparator.comparing(item -> (String) item.get("PRD_DE")))
				.orElseThrow(() -> new CustomException(ErrorCode.DATA_NOT_FOUND));

			// 지역 코드 -> RegionCode enum 매핑
			RegionCode regionCode = RegionCode.fromCode((String) latestEntry.get("C1"));

			if (!regionCode.getRegion().equals(region) || !regionCode.getSubregion().equals(subregion)) {
				log.warn("지역 불일치. 기대값: {}:{}, 실제값: {}: {}",
					region, subregion, regionCode.getRegion(), regionCode.getSubregion());
				throw new CustomException(ErrorCode.INVALID_REGION_MATCH);
			}

			return mapToDto(latestEntry, regionCode);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			log.error("최신 아파트 가격을 호출하는 데 실패. 지역: {}, 세부지역: {}, 원인: {}",
				region, subregion, e.getMessage());
			throw new CustomException(ErrorCode.EXTERNAL_API_FAILURE);
		}
	}

	/**
	 *
	 * @param months : N개월 동안의 데이터를 불러오기 위한 입력 필드
	 * @return
	 */
	public Map<RegionCode, Pair<ApartmentPriceDto, ApartmentPriceDto>> fetchDurationAll(int months) {
		String url = buildUrl(months);
		try {
			String response = restTemplate.getForObject(url, String.class);
			List<Map<String, Object>> dataList = objectMapper.readValue(response, new TypeReference<>() {});

			return RegionCode.getValidCodes().stream() // 등록된 모든 지역 코드를 반복
				.map(code -> {
					List<Map<String, Object>> regionData = dataList.stream()
						.filter(item -> code.getSubregion().equals(item.get("C1_NM"))) // 데이터의 C1_NM과 RegionCode의 subregion같은 값을 매칭
						.collect(Collectors.toList()); // subregion별로 데이터를 모아서 List<Map<String, Object>> 형태로 저장

					if (regionData.isEmpty()) return null; // 데이터가 없으면 null을 리턴 (null인 경우 아래 filter에서 제거됨)

					Map<String, Object> oldest = regionData.stream()
						.min(Comparator.comparing(item -> (String) item.get("PRD_DE"))).orElse(null); // 가장 오래된 데이터 뽑기
					Map<String, Object> latest = regionData.stream()
						.max(Comparator.comparing(item -> (String) item.get("PRD_DE"))).orElse(null); // 가장 최신 데이터 뽑기

					return Map.entry(code,
						Pair.of(mapToDto(oldest, code), mapToDto(latest, code)));
				})
				.filter((Objects::nonNull))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		} catch (Exception e) {
			log.warn("KOSIS 데이터 동기화 실패: {}", e.getMessage());
			throw new CustomException(ErrorCode.EXTERNAL_API_FAILURE);
		}
	}

	private ApartmentPriceDto mapToDto(Map<String, Object> entry, RegionCode code) {
		return ApartmentPriceDto.builder()
			.region(code.getRegion())
			.subregion(code.getSubregion())
			.period((String) entry.get("PRD_DE"))
			.price(Double.parseDouble((String) entry.get("DT")))
			.unit((String) entry.get("UNIT_NM"))
			.build();
	}

	private String buildUrl(int months) {
		return BASE_URL +
			DEFAULT_PARAMS +
			"&newEstPrdCnt=" + months +
			"&apiKey=" + apiKey;
	}

}
