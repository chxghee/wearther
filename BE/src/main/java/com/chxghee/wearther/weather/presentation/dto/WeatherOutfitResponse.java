package com.chxghee.wearther.weather.presentation.dto;

import java.util.List;

/**
 * 통합 날씨-옷차림 API 응답 DTO
 */
public record WeatherOutfitResponse(
        CurrentWeatherDto currentWeather,
        WeatherSummaryDto weatherSummary,
        List<HourlyForecastDto> hourlyForecasts,
        OutfitDto outfit
) {
}
