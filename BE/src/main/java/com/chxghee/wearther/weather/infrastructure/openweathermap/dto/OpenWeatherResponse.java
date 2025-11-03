package com.chxghee.wearther.weather.infrastructure.openweathermap.dto;

import java.util.List;

/**
 * OpenWeatherMap API 5일 예보 응답
 */
public record OpenWeatherResponse(
        List<ForecastItemDto> list,
        CityDto city
) {
}
