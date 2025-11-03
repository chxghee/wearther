package com.chxghee.wearther.weather.application;

import com.chxghee.wearther.outfit.application.OutfitRecommendationService;
import com.chxghee.wearther.outfit.domain.OutfitRecommendation;
import com.chxghee.wearther.weather.infrastructure.geocoding.GeoCodingClient;
import com.chxghee.wearther.weather.infrastructure.openweathermap.OpenWeatherClient;
import com.chxghee.wearther.weather.infrastructure.openweathermap.dto.CityData;
import com.chxghee.wearther.weather.infrastructure.openweathermap.dto.ForecastItemDto;
import com.chxghee.wearther.weather.infrastructure.openweathermap.dto.OpenWeatherResponse;
import com.chxghee.wearther.weather.presentation.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 날씨 및 옷차림 추천 통합 서비스
 */
@Service
@RequiredArgsConstructor
public class WeatherService {

    private final OpenWeatherClient openWeatherClient;
    private final GeoCodingClient geoCodingClient;
    private final OutfitRecommendationService outfitRecommendationService;

    /**
     * 도시 이름으로 날씨와 옷차림 추천 정보를 조회
     *
     * @param cityName 도시 이름
     * @return 통합 날씨-옷차림 정보
     */
    public WeatherOutfitResponse getWeatherAndOutfit(String cityName) {
        // 1. Geocoding: 도시 이름 -> 좌표 변환
        CityData cityData = geoCodingClient.getCoordinatesByCityName(cityName);

        // 2. OpenWeatherMap: 날씨 예보 조회 (5일, 3시간 간격)
        OpenWeatherResponse weatherResponse = openWeatherClient.getForecast(cityData.lat(), cityData.lon());

        // 3. 24시간(8개) 예보 데이터 추출
        List<ForecastItemDto> next24Hours = weatherResponse.list().subList(0, Math.min(8, weatherResponse.list().size()));

        // 4. 현재 날씨 (첫 번째 예보)
        ForecastItemDto currentForecast = next24Hours.get(0);
        CurrentWeatherDto currentWeather = new CurrentWeatherDto(
                currentForecast.main().temp(),
                currentForecast.weather().get(0).main(),
                currentForecast.weather().get(0).description(),
                currentForecast.weather().get(0).icon()
        );

        // 5. 12시간 날씨 요약 (최저/최고 온도, 코멘트)
        List<ForecastItemDto> next12Hours = next24Hours.subList(0, Math.min(4, next24Hours.size()));
        WeatherSummaryDto weatherSummary = createWeatherSummary(next12Hours);

        // 6. 시간대별 예보 (24시간, 3시간 간격)
        List<HourlyForecastDto> hourlyForecasts = next24Hours.stream()
                .map(forecast -> new HourlyForecastDto(
                        forecast.dateTimeText(),
                        forecast.main().temp(),
                        forecast.weather().get(0).main(),
                        forecast.weather().get(0).description(),
                        forecast.weather().get(0).icon()
                ))
                .toList();

        // 7. 옷차림 추천
        List<Double> hourlyTemperatures = next12Hours.stream()
                .map(f -> f.main().temp())
                .toList();

        OutfitRecommendation outfitRecommendation = outfitRecommendationService.recommendOutfit(
                weatherSummary.minTemperature(),
                weatherSummary.maxTemperature(),
                hourlyTemperatures
        );

        // 각 레벨을 OutfitLevelDto로 변환하여 Map 생성
        java.util.Map<String, OutfitLevelDto> outfitByLevel = outfitRecommendation.getAllLevels().stream()
                .collect(java.util.stream.Collectors.toMap(
                        level -> level.name(),
                        level -> new OutfitLevelDto(
                                formatTemperatureRange(level),
                                level.getInnerWear(),
                                level.getTopWear(),
                                level.getBottomWear(),
                                level.getOuterWear(),
                                level.getAccessories()
                        )
                ));

        OutfitDto outfit = new OutfitDto(
                outfitRecommendation.getMainLevel().name(),
                outfitByLevel
        );

        return new WeatherOutfitResponse(currentWeather, weatherSummary, hourlyForecasts, outfit);
    }

    /**
     * 12시간 날씨 요약 생성 (최저/최고 온도, 코멘트)
     */
    private WeatherSummaryDto createWeatherSummary(List<ForecastItemDto> forecasts) {
        double minTemp = forecasts.stream()
                .mapToDouble(f -> f.main().temp())
                .min()
                .orElse(0);

        double maxTemp = forecasts.stream()
                .mapToDouble(f -> f.main().temp())
                .max()
                .orElse(0);

        String comment = generateWeatherComment(minTemp, maxTemp);

        return new WeatherSummaryDto(minTemp, maxTemp, comment);
    }

    /**
     * 날씨 코멘트 생성
     */
    private String generateWeatherComment(double minTemp, double maxTemp) {
        double tempDiff = maxTemp - minTemp;

        if (tempDiff >= 10) {
            return "일교차가 크니 얇은 외투를 챙기세요.";
        } else if (maxTemp >= 28) {
            return "더운 날씨예요. 시원한 옷차림을 추천해요.";
        } else if (minTemp <= 5) {
            return "쌀쌀한 날씨예요. 따뜻하게 입으세요.";
        } else if (tempDiff >= 5) {
            return "오전과 오후 기온차가 있어요. 겉옷을 챙기세요.";
        } else {
            return "쾌적한 날씨예요.";
        }
    }

    /**
     * 온도 레벨의 범위를 문자열로 포맷팅
     */
    private String formatTemperatureRange(com.chxghee.wearther.outfit.domain.OutfitLevel level) {
        int minTemp = level.getMinTemp();
        int maxTemp = level.getMaxTemp();

        // 특수 케이스 처리
        if (minTemp == Integer.MIN_VALUE) {
            return maxTemp + "°C 이하";
        } else if (maxTemp == Integer.MAX_VALUE) {
            return minTemp + "°C 이상";
        } else {
            return minTemp + "°C ~ " + maxTemp + "°C";
        }
    }

}
