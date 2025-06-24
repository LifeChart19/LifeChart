package org.example.lifechart.domain.goal.service;

import java.net.SocketTimeoutException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
		String API_URL = buildUrl(120); // 최근 60개월
		try {
			String response = restTemplate.getForObject(API_URL, String.class);
			List<Map<String, Object>> dataList = objectMapper.readValue(response, new TypeReference<>() {});

			// 최신 데이터 추출
			Optional<Map<String, Object>> latestEntry = dataList.stream()
				.filter(item -> subregion.equals(item.get("C1_NM")))
				.max(Comparator.comparing(item -> (String) item.get("PRD_DE")));

			if (latestEntry.isEmpty()) {
				throw new CustomException(ErrorCode.DATA_NOT_FOUND);
			}

			Map<String, Object> entry = latestEntry.get();

			// 지역 코드 -> RegionCode enum 매핑
			String code = (String) entry.get("C1"); //
			RegionCode regionCode = RegionCode.fromCode(code);

			if (!regionCode.getRegion().equals(region) || !regionCode.getSubregion().equals(subregion)) {
				throw new CustomException(ErrorCode.INVALID_REGION_MATCH);
			}

			return ApartmentPriceDto.builder()
				.region(regionCode.getRegion()) // 예 : "서울"
				.subregion(regionCode.getSubregion()) // 예 : "동남권"
				.period((String) entry.get("PRD_DE")) // 예: "202504"
				.price(Double.parseDouble((String) entry.get("DT"))) // 예: "1639.3"
				.unit((String) entry.get("UNIT_NM")) // 예: "만원/m^2"
				.build();
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			throw new CustomException(ErrorCode.EXTERNAL_API_FAILURE);
		}
	}

	private String buildUrl(int months) {
		return BASE_URL +
			DEFAULT_PARAMS +
			"&newEstPrdCnt=" + months +
			"&apiKey=" + apiKey;
	}
}
