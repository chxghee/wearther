package com.chxghee.wearther.weather.presentation.dto;

/**
 * 시간대별 예보 정보 (3시간 간격)
 */
public record HourlyForecastDto(
        String dateTime,
        double temperature,
        String weatherMain,
        String weatherDescription,
        String weatherIcon
) {
}
