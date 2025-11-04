package com.chxghee.wearther.weather.infrastructure.openweathermap;

import com.chxghee.wearther.weather.infrastructure.openweathermap.dto.CurrentWeatherResponse;
import com.chxghee.wearther.weather.infrastructure.openweathermap.dto.OpenWeatherResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * OpenWeatherMap API 클라이언트
 * 현재 날씨 및 5일 예보 데이터를 조회
 */
@Component
@RequiredArgsConstructor
public class OpenWeatherClient {

    private final WebClient webClient;

    @Value("${external.openweather.api-key}")
    private String apiKey;

    /**
     * 좌표 기반 현재 날씨 조회
     * @param lat 위도
     * @param lon 경도
     * @return 현재 날씨 데이터
     */
    public CurrentWeatherResponse getCurrentWeather(double lat, double lon) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/data/2.5/weather")
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .queryParam("units", "metric")  // 섭씨 온도로 받기
                        .queryParam("appid", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(CurrentWeatherResponse.class)
                .block();
    }

    /**
     * 좌표 기반 날씨 예보 조회 (5일, 3시간 간격)
     * @param lat 위도
     * @param lon 경도
     * @return 날씨 예보 데이터
     */
    public OpenWeatherResponse getForecast(double lat, double lon) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/data/2.5/forecast")
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .queryParam("units", "metric")  // 섭씨 온도로 받기
                        .queryParam("appid", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(OpenWeatherResponse.class)
                .block();
    }

}
