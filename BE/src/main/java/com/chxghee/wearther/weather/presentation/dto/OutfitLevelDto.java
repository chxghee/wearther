package com.chxghee.wearther.weather.presentation.dto;

import java.util.List;

/**
 * 특정 온도 레벨의 옷차림 정보를 담는 DTO
 */
public record OutfitLevelDto(
        String temperatureRange,
        List<String> innerWear,
        List<String> topWear,
        List<String> bottomWear,
        List<String> outerWear,
        List<String> accessories
) {
}
