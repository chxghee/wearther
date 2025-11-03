package com.chxghee.wearther.weather.application;

import com.chxghee.wearther.outfit.application.OutfitRecommendationService;
import com.chxghee.wearther.outfit.domain.OutfitLevel;
import com.chxghee.wearther.outfit.domain.OutfitRecommendation;
import com.chxghee.wearther.weather.infrastructure.geocoding.GeoCodingClient;
import com.chxghee.wearther.weather.infrastructure.openweathermap.OpenWeatherClient;
import com.chxghee.wearther.weather.infrastructure.openweathermap.dto.*;
import com.chxghee.wearther.weather.presentation.dto.WeatherOutfitResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * WeatherService 테스트
 * 외부 API를 Mocking하여 통합 날씨-옷차림 조회 기능 테스트
 */
@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private OpenWeatherClient openWeatherClient;

    @Mock
    private GeoCodingClient geoCodingClient;

    @Mock
    private OutfitRecommendationService outfitRecommendationService;

    @InjectMocks
    private WeatherService weatherService;

    @Test
    @DisplayName("도시 이름으로 날씨와 옷차림 추천 정보를 조회한다")
    void getWeatherAndOutfit_Success() {
        // Given
        String cityName = "Seoul";
        CityData cityData = new CityData("Seoul", 37.5665, 126.9780, "KR");

        // Mock OpenWeatherMap API 응답 (24시간, 3시간 간격 = 8개)
        List<ForecastItemDto> forecastList = List.of(
                createForecastItem(15.0, "Clear", "clear sky", "01d"),
                createForecastItem(17.0, "Clear", "clear sky", "01d"),
                createForecastItem(20.0, "Clear", "clear sky", "01d"),
                createForecastItem(18.0, "Clouds", "few clouds", "02n"),
                createForecastItem(16.0, "Clouds", "few clouds", "02n"),
                createForecastItem(14.0, "Clouds", "scattered clouds", "03n"),
                createForecastItem(13.0, "Clouds", "scattered clouds", "03n"),
                createForecastItem(12.0, "Clear", "clear sky", "01d")
        );

        CityDto cityDto = new CityDto(1835848, "Seoul",
                new CoordinateData(37.5665, 126.9780), "KR", 32400, 0, 0);
        OpenWeatherResponse weatherResponse = new OpenWeatherResponse(forecastList, cityDto);

        when(geoCodingClient.getCoordinatesByCityName(anyString())).thenReturn(cityData);
        when(openWeatherClient.getForecast(anyDouble(), anyDouble())).thenReturn(weatherResponse);

        // Mock OutfitRecommendationService
        OutfitRecommendation mockOutfitRecommendation = new OutfitRecommendation(
                OutfitLevel.LEVEL_5,
                List.of(OutfitLevel.LEVEL_4, OutfitLevel.LEVEL_5, OutfitLevel.LEVEL_6)
        );
        when(outfitRecommendationService.recommendOutfit(anyDouble(), anyDouble(), anyList()))
                .thenReturn(mockOutfitRecommendation);

        // When
        WeatherOutfitResponse response = weatherService.getWeatherAndOutfit(cityName);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.currentWeather()).isNotNull();
        assertThat(response.currentWeather().temperature()).isEqualTo(15.0);
        assertThat(response.weatherSummary()).isNotNull();
        assertThat(response.weatherSummary().minTemperature()).isEqualTo(15.0);
        assertThat(response.weatherSummary().maxTemperature()).isEqualTo(20.0);
        assertThat(response.hourlyForecasts()).hasSize(8);

        // 옷차림 추천 검증 (레벨별 구분)
        assertThat(response.outfit()).isNotNull();
        assertThat(response.outfit().mainLevelKey()).isEqualTo("LEVEL_5");
        assertThat(response.outfit().outfitByLevel()).isNotNull();
        assertThat(response.outfit().outfitByLevel()).hasSize(3);
        assertThat(response.outfit().outfitByLevel()).containsKeys("LEVEL_4", "LEVEL_5", "LEVEL_6");

        // 각 레벨의 temperatureRange 검증
        assertThat(response.outfit().outfitByLevel().get("LEVEL_4").temperatureRange()).isEqualTo("12°C ~ 16°C");
        assertThat(response.outfit().outfitByLevel().get("LEVEL_5").temperatureRange()).isEqualTo("17°C ~ 19°C");
        assertThat(response.outfit().outfitByLevel().get("LEVEL_6").temperatureRange()).isEqualTo("20°C ~ 22°C");
    }

    private ForecastItemDto createForecastItem(double temp, String weatherMain, String description, String icon) {
        MainDto mainDto = new MainDto(temp, temp, temp, temp, 1013, 65);
        WeatherDto weatherDto = new WeatherDto(800, weatherMain, description, icon);
        return new ForecastItemDto(System.currentTimeMillis() / 1000, mainDto, List.of(weatherDto), "2025-01-01 12:00:00");
    }
}
