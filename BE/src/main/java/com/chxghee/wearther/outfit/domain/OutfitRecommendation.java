package com.chxghee.wearther.outfit.domain;

import java.util.List;

/**
 * 옷차림 추천 도메인 모델
 * 각 레벨의 옷차림 정보를 통합하지 않고 분리하여 관리
 */
public class OutfitRecommendation {
    private final OutfitLevel mainLevel;
    private final List<OutfitLevel> allLevels;

    public OutfitRecommendation(OutfitLevel mainLevel, List<OutfitLevel> allLevels) {
        this.mainLevel = mainLevel;
        this.allLevels = allLevels;
    }

    public OutfitLevel getMainLevel() {
        return mainLevel;
    }

    public List<OutfitLevel> getAllLevels() {
        return allLevels;
    }
}
