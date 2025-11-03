package com.chxghee.wearther.weather.infrastructure.geocoding;

import com.chxghee.wearther.weather.infrastructure.openweathermap.dto.CityData;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * OpenWeatherMap Geocoding API 클라이언트
 * 도시 이름을 위도/경도 좌표로 변환
 */
@Component
@RequiredArgsConstructor
public class GeoCodingClient {

    private final WebClient webClient;

    @Value("${external.openweather.api-key}")
    private String apiKey;

    /**
     * 도시 이름으로 좌표를 조회
     * @param cityName 도시 이름 (예: Seoul, London)
     * @return 좌표 정보 (위도, 경도)
     */
    public CityData getCoordinatesByCityName(String cityName) {
        CityData[] response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/geo/1.0/direct")
                        .queryParam("q", cityName)
                        .queryParam("limit", 1)
                        .queryParam("appid", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(CityData[].class)
                .block();

        if (response == null || response.length == 0) {
            throw new IllegalArgumentException("도시를 찾을 수 없습니다: " + cityName);
        }

        return response[0];
    }

}
