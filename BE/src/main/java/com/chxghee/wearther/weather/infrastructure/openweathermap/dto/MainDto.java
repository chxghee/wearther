package com.chxghee.wearther.weather.infrastructure.openweathermap.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 기온 및 기압 정보
 */
public record MainDto(
        double temp,
        @JsonProperty("feels_like") double feelsLike,
        @JsonProperty("temp_min") double tempMin,
        @JsonProperty("temp_max") double tempMax,
        int pressure,
        int humidity
) {
}
