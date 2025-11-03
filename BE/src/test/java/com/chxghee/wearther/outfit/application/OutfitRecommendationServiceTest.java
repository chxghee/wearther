package com.chxghee.wearther.outfit.application;

import com.chxghee.wearther.outfit.domain.OutfitLevel;
import com.chxghee.wearther.outfit.domain.OutfitRecommendation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * OutfitRecommendationService 테스트
 */
class OutfitRecommendationServiceTest {

    private final OutfitRecommendationService service = new OutfitRecommendationService();

    @Test
    @DisplayName("온도 범위에 따라 옷차림을 추천한다")
    void recommendOutfit_Success() {
        // Given
        double minTemp = 15.0;
        double maxTemp = 20.0;
        List<Double> hourlyTemperatures = List.of(15.0, 17.0, 20.0, 18.0);

        // When
        OutfitRecommendation recommendation = service.recommendOutfit(minTemp, maxTemp, hourlyTemperatures);

        // Then
        assertThat(recommendation).isNotNull();
        assertThat(recommendation.getMainLevel()).isNotNull();
        assertThat(recommendation.getAllLevels()).isNotEmpty();
        assertThat(recommendation.getTopWear()).isNotEmpty();
        assertThat(recommendation.getBottomWear()).isNotEmpty();
    }

    @Test
    @DisplayName("추운 날씨(5도)에는 LEVEL_2 옷차림을 추천한다")
    void recommendOutfit_ColdWeather() {
        // Given
        double minTemp = 5.0;
        double maxTemp = 8.0;
        List<Double> hourlyTemperatures = List.of(5.0, 6.0, 7.0, 8.0);

        // When
        OutfitRecommendation recommendation = service.recommendOutfit(minTemp, maxTemp, hourlyTemperatures);

        // Then
        assertThat(recommendation.getMainLevel()).isIn(OutfitLevel.LEVEL_2, OutfitLevel.LEVEL_3);
        assertThat(recommendation.getOuterWear()).containsAnyOf("울 코트", "패딩", "무스탕");
    }

    @Test
    @DisplayName("더운 날씨(28도 이상)에는 LEVEL_8 옷차림을 추천한다")
    void recommendOutfit_HotWeather() {
        // Given
        double minTemp = 28.0;
        double maxTemp = 32.0;
        List<Double> hourlyTemperatures = List.of(28.0, 30.0, 32.0, 30.0);

        // When
        OutfitRecommendation recommendation = service.recommendOutfit(minTemp, maxTemp, hourlyTemperatures);

        // Then
        assertThat(recommendation.getMainLevel()).isEqualTo(OutfitLevel.LEVEL_8);
        assertThat(recommendation.getTopWear()).containsAnyOf("민소매 티셔츠", "반소매 티셔츠");
    }
}
