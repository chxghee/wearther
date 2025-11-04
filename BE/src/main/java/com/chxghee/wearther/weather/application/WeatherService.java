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

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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

        // 2-1. OpenWeatherMap: 현재 날씨 조회
        com.chxghee.wearther.weather.infrastructure.openweathermap.dto.CurrentWeatherResponse currentWeatherResponse =
                openWeatherClient.getCurrentWeather(cityData.lat(), cityData.lon());

        // 2-2. OpenWeatherMap: 날씨 예보 조회 (5일, 3시간 간격)
        OpenWeatherResponse weatherResponse = openWeatherClient.getForecast(cityData.lat(), cityData.lon());

        // 3. 24시간(8개) 예보 데이터 추출
        List<ForecastItemDto> next24Hours = weatherResponse.list().subList(0, Math.min(8, weatherResponse.list().size()));

        // 4. 현재 날씨 (Current Weather API 데이터 사용)
        CurrentWeatherDto currentWeather = new CurrentWeatherDto(
                currentWeatherResponse.main().temp(),
                currentWeatherResponse.weather().get(0).main(),
                currentWeatherResponse.weather().get(0).description(),
                currentWeatherResponse.weather().get(0).icon()
        );

        // 5. 12시간 날씨 요약 (최저/최고 온도, 코멘트) - 현재 날씨 포함
        List<ForecastItemDto> next12Hours = next24Hours.subList(0, Math.min(4, next24Hours.size()));
        WeatherSummaryDto weatherSummary = createWeatherSummary(next12Hours, currentWeatherResponse.main().temp());

        // 6. 시간대별 예보 (24시간, 3시간 간격) - timezone을 사용하여 현지 시간으로 변환
        int timezoneOffset = currentWeatherResponse.timezone();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 6-1. 현재 날씨를 HourlyForecastDto로 변환
        LocalDateTime currentLocalDateTime = LocalDateTime.ofEpochSecond(
                currentWeatherResponse.timestamp() + timezoneOffset,
                0,
                ZoneOffset.UTC
        );
        HourlyForecastDto currentHourlyForecast = new HourlyForecastDto(
                currentLocalDateTime.format(formatter),
                currentWeatherResponse.main().temp(),
                currentWeatherResponse.weather().get(0).main(),
                currentWeatherResponse.weather().get(0).description(),
                currentWeatherResponse.weather().get(0).icon()
        );

        // 6-2. 예보 데이터를 HourlyForecastDto 리스트로 변환
        List<HourlyForecastDto> forecastList = next24Hours.stream()
                .map(forecast -> {
                    // dt(Unix timestamp) + timezone으로 현지 시간 계산
                    long localTimestamp = forecast.timestamp() + timezoneOffset;
                    LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(
                            localTimestamp,
                            0,
                            ZoneOffset.UTC
                    );
                    String localDateTimeStr = localDateTime.format(formatter);

                    return new HourlyForecastDto(
                            localDateTimeStr,
                            forecast.main().temp(),
                            forecast.weather().get(0).main(),
                            forecast.weather().get(0).description(),
                            forecast.weather().get(0).icon()
                    );
                })
                .toList();

        // 6-3. 현재 날씨를 첫 번째 요소로, 예보 데이터를 나머지로 결합
        List<HourlyForecastDto> hourlyForecasts = new java.util.ArrayList<>();
        hourlyForecasts.add(currentHourlyForecast);
        hourlyForecasts.addAll(forecastList);

        // 7. 옷차림 추천 - 현재 온도 포함
        List<Double> hourlyTemperatures = new java.util.ArrayList<>();
        hourlyTemperatures.add(currentWeatherResponse.main().temp());
        hourlyTemperatures.addAll(
                next12Hours.stream()
                        .map(f -> f.main().temp())
                        .toList()
        );

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
     * 현재 날씨 온도를 포함하여 계산
     */
    private WeatherSummaryDto createWeatherSummary(List<ForecastItemDto> forecasts, double currentTemp) {
        // 현재 온도를 포함한 온도 리스트 생성
        List<Double> allTemperatures = new java.util.ArrayList<>();
        allTemperatures.add(currentTemp);
        allTemperatures.addAll(
                forecasts.stream()
                        .map(f -> f.main().temp())
                        .toList()
        );

        double minTemp = allTemperatures.stream()
                .mapToDouble(Double::doubleValue)
                .min()
                .orElse(0);

        double maxTemp = allTemperatures.stream()
                .mapToDouble(Double::doubleValue)
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
