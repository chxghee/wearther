package com.chxghee.wearther.weather.presentation.dto;

import java.util.List;

/**
 * 옷차림 추천 정보
 */
public record OutfitDto(
        String mainLevelKey,
        List<String> innerWear,
        List<String> topWear,
        List<String> bottomWear,
        List<String> outerWear,
        List<String> accessories
) {
}
