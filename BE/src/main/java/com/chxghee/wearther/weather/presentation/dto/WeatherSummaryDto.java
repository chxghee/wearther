package com.chxghee.wearther.weather.presentation.dto;

/**
 * 12시간 날씨 요약 정보
 */
public record WeatherSummaryDto(
        double minTemperature,
        double maxTemperature,
        String comment
) {
}
