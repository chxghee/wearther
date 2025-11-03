package com.chxghee.wearther.outfit.domain;

import java.util.List;

/**
 * 옷차림 추천 도메인 모델
 */
public class OutfitRecommendation {
    private final OutfitLevel mainLevel;
    private final List<OutfitLevel> allLevels;
    private final List<String> innerWear;
    private final List<String> topWear;
    private final List<String> bottomWear;
    private final List<String> outerWear;
    private final List<String> accessories;

    public OutfitRecommendation(OutfitLevel mainLevel, List<OutfitLevel> allLevels) {
        this.mainLevel = mainLevel;
        this.allLevels = allLevels;

        // 모든 레벨의 옷차림 정보를 통합
        this.innerWear = allLevels.stream()
                .flatMap(level -> level.getInnerWear().stream())
                .distinct()
                .toList();

        this.topWear = allLevels.stream()
                .flatMap(level -> level.getTopWear().stream())
                .distinct()
                .toList();

        this.bottomWear = allLevels.stream()
                .flatMap(level -> level.getBottomWear().stream())
                .distinct()
                .toList();

        this.outerWear = allLevels.stream()
                .flatMap(level -> level.getOuterWear().stream())
                .distinct()
                .toList();

        this.accessories = allLevels.stream()
                .flatMap(level -> level.getAccessories().stream())
                .distinct()
                .toList();
    }

    public OutfitLevel getMainLevel() {
        return mainLevel;
    }

    public List<OutfitLevel> getAllLevels() {
        return allLevels;
    }

    public List<String> getInnerWear() {
        return innerWear;
    }

    public List<String> getTopWear() {
        return topWear;
    }

    public List<String> getBottomWear() {
        return bottomWear;
    }

    public List<String> getOuterWear() {
        return outerWear;
    }

    public List<String> getAccessories() {
        return accessories;
    }
}
