package com.chxghee.wearther.weather.infrastructure.openweathermap.dto;

/**
 * 날씨 상태 정보
 */
public record WeatherDto(
        int id,
        String main,
        String description,
        String icon
) {
}
