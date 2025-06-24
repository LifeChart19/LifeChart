package org.example.lifechart.domain.goal.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RegionCode {
	// format: code, subregionName, regionName

	KOREA("000", "전국", "전국"),
	CAPITAL("010", "수도권", "수도권"),
	LOCAL("020", "지방", "지방"),

	// 서울 및 권역
	SEOUL("030", "서울", "서울"),
	SEOUL_CENTER("0301", "도심권", "서울"),
	SEOUL_NE("0302", "동북권", "서울"),
	SEOUL_SE("0303", "동남권", "서울"),
	SEOUL_NW("0304", "서북권", "서울"),
	SEOUL_SW("0305", "서남권", "서울"),

	// 광역시
	BUSAN("040", "부산", "부산"),
	DAEGU("050", "대구", "대구"),
	INCHEON("060", "인천", "인천"),
	GWANGJU("070", "광주", "광주"),
	DAEJEON("080", "대전", "대전"),
	ULSAN("090", "울산", "울산"),
	SEJONG("091", "세종", "세종"),

	// 도
	GYEONGGI("100", "경기", "경기"),
	GANGWON("110", "강원", "강원"),
	CHUNGBUK("120", "충북", "충북"),
	CHUNGNAM("130", "충남", "충남"),
	JEONBUK("140", "전북", "전북"),
	JEONNAM("150", "전남", "전남"),
	GYEONGBUK("160", "경북", "경북"),
	GYEONGNAM("170", "경남", "경남"),
	JEJU("180", "제주", "제주"),

	UNKNOWN("999", "기타", "기타");

	private final String code;
	private final String subregion;
	private final String region;

	private static final Map<String, RegionCode> codeMap = new HashMap<>();

	static {
		for (RegionCode rc : values()) {
			codeMap.put(rc.code, rc);
		}
	}

	public static RegionCode fromCode(String code) {
		return codeMap.getOrDefault(code, UNKNOWN);
	}

	public static String getRegionName(String code) {
		return fromCode(code).getRegion();
	}

	public static String getSubregionName(String code) {
		return fromCode(code).getSubregion();
	}

	public static List<RegionCode> getValidCodes() {
		return Arrays.stream(values())
			.filter(code -> !code.equals(UNKNOWN))
			.collect(Collectors.toList());
	}
}
