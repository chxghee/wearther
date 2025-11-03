package com.chxghee.wearther.weather.infrastructure.openweathermap.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * OpenWeatherMap API의 5일 예보 중 개별 시간대 데이터
 */
public record ForecastItemDto(
        @JsonProperty("dt") long timestamp,
        MainDto main,
        List<WeatherDto> weather,
        @JsonProperty("dt_txt") String dateTimeText
) {
}
