package com.chxghee.wearther.weather.presentation;

import com.chxghee.wearther.weather.application.WeatherService;
import com.chxghee.wearther.weather.presentation.dto.WeatherOutfitResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 날씨 및 옷차림 추천 API 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class WeatherController {

    private final WeatherService weatherService;

    /**
     * 통합 날씨-옷차림 API
     * 도시 이름으로 날씨와 옷차림 추천 정보를 조회
     *
     * @param city 도시 이름 (기본값: Seoul)
     * @return 날씨 및 옷차림 추천 정보
     */
    @GetMapping("/weather-outfit")
    public ResponseEntity<WeatherOutfitResponse> getWeatherAndOutfit(
            @RequestParam(defaultValue = "Seoul") String city
    ) {
        WeatherOutfitResponse response = weatherService.getWeatherAndOutfit(city);
        return ResponseEntity.ok(response);
    }

}
