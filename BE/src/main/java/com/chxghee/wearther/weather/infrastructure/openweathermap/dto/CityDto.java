package com.chxghee.wearther.weather.infrastructure.openweathermap.dto;

/**
 * 도시 정보
 */
public record CityDto(
        int id,
        String name,
        CoordinateData coord,
        String country,
        int timezone,
        long sunrise,
        long sunset
) {
}
