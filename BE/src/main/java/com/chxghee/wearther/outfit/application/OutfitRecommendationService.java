package com.chxghee.wearther.outfit.application;

import com.chxghee.wearther.outfit.domain.OutfitLevel;
import com.chxghee.wearther.outfit.domain.OutfitRecommendation;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 옷차림 추천 서비스
 * 온도 범위에 따른 옷차림을 추천
 */
@Service
public class OutfitRecommendationService {

    /**
     * 최저/최고 온도와 시간대별 온도 리스트를 기반으로 옷차림 추천
     *
     * @param minTemp 12시간 내 최저 온도
     * @param maxTemp 12시간 내 최고 온도
     * @param hourlyTemperatures 12시간 내 3시간 간격 온도 리스트
     * @return 옷차림 추천 정보
     */
    public OutfitRecommendation recommendOutfit(double minTemp, double maxTemp, List<Double> hourlyTemperatures) {
        // 최저/최고 온도 범위에 해당하는 모든 레벨 식별
        List<OutfitLevel> allLevels = Arrays.stream(OutfitLevel.values())
                .filter(level -> isLevelInRange(level, minTemp, maxTemp))
                .toList();

        // 가장 빈번하게 나타나는 온도 레벨을 mainLevel로 식별
        OutfitLevel mainLevel = findMostFrequentLevel(hourlyTemperatures);

        return new OutfitRecommendation(mainLevel, allLevels);
    }

    /**
     * 레벨이 주어진 온도 범위와 겹치는지 확인
     */
    private boolean isLevelInRange(OutfitLevel level, double minTemp, double maxTemp) {
        return !(level.getMaxTemp() < minTemp || level.getMinTemp() > maxTemp);
    }

    /**
     * 시간대별 온도에서 가장 빈번하게 나타나는 레벨 찾기
     */
    private OutfitLevel findMostFrequentLevel(List<Double> temperatures) {
        Map<OutfitLevel, Long> levelCounts = temperatures.stream()
                .map(OutfitLevel::fromTemperature)
                .collect(Collectors.groupingBy(level -> level, Collectors.counting()));

        return levelCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(OutfitLevel.LEVEL_4); // 기본값
    }
}
