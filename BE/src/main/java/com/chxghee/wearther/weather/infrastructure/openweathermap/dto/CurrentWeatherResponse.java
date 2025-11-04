package com.chxghee.wearther.weather.infrastructure.openweathermap.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * OpenWeatherMap API 현재 날씨 응답
 * API 엔드포인트: /data/2.5/weather
 */
public record CurrentWeatherResponse(
        CoordinateData coord,
        List<WeatherDto> weather,
        String base,
        MainDto main,
        int visibility,
        WindDto wind,
        CloudsDto clouds,
        @JsonProperty("dt") long timestamp,
        SysDto sys,
        int timezone,
        long id,
        String name,
        int cod
) {
}
