package com.chxghee.wearther.weather.infrastructure.openweathermap.dto;

/**
 * 바람 정보
 */
public record WindDto(
        double speed,
        int deg
) {
}
