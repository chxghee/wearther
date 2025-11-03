package com.chxghee.wearther.weather.infrastructure.openweathermap.dto;

/**
 * Geocoding API 응답 (배열 형태)
 */
public record CityData(
        String name,
        double lat,
        double lon,
        String country
) {
}
