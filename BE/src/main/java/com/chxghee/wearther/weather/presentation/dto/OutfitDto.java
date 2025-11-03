package com.chxghee.wearther.weather.presentation.dto;

import java.util.Map;

/**
 * 옷차림 추천 정보
 * 레벨별로 구분된 옷차림 정보를 제공
 */
public record OutfitDto(
        String mainLevelKey,
        Map<String, OutfitLevelDto> outfitByLevel
) {
}
