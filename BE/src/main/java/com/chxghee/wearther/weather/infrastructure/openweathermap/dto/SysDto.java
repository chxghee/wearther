package com.chxghee.wearther.weather.infrastructure.openweathermap.dto;

/**
 * 시스템 정보 (일출/일몰 시각 등)
 */
public record SysDto(
        int type,
        long id,
        String country,
        long sunrise,
        long sunset
) {
}
