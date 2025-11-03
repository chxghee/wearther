package com.chxghee.wearther.weather.presentation.dto;

/**
 * 현재 날씨 정보
 */
public record CurrentWeatherDto(
        double temperature,
        String weatherMain,
        String weatherDescription,
        String weatherIcon
) {
}
