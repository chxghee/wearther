package com.chxghee.wearther.outfit.domain;

import java.util.Arrays;
import java.util.List;

/**
 * 온도 레벨에 따른 옷차림 기준
 * PRD Feature 1의 온도별 옷차림 기준 테이블
 */
public enum OutfitLevel {
    LEVEL_1(4, Integer.MIN_VALUE,
            List.of(),
            List.of("히트텍", "니트/스웨터", "맨투맨", "후드 티셔츠"),
            List.of("면바지", "슬랙스", "데님 팬츠", "카고 팬츠"),
            List.of("패딩", "울 코트", "무스탕"),
            List.of("비니 모자", "목도리", "장갑")),

    LEVEL_2(8, 5,
            List.of(),
            List.of("히트텍", "니트/스웨터", "맨투맨", "후드 티셔츠"),
            List.of("면바지", "슬랙스", "데님 팬츠", "카고 팬츠"),
            List.of("울 코트", "무스탕", "패딩"),
            List.of("비니 모자")),

    LEVEL_3(11, 9,
            List.of(),
            List.of("맨투맨", "후드 티셔츠", "니트/스웨터", "히트텍"),
            List.of("면바지", "슬랙스", "데님 팬츠", "카고 팬츠"),
            List.of("트렌치 코트", "경량 패딩", "플리스 자켓", "가죽 자켓"),
            List.of()),

    LEVEL_4(16, 12,
            List.of(),
            List.of("긴소매 티셔츠", "셔츠/블라우스", "맨투맨", "후드 티셔츠", "니트/스웨터"),
            List.of("면바지", "슬랙스", "데님 팬츠", "카고 팬츠"),
            List.of("가디건", "블레이저", "트러커 자켓", "가죽 자켓", "블루종", "플리스 자켓"),
            List.of()),

    LEVEL_5(19, 17,
            List.of(),
            List.of("긴소매 티셔츠", "셔츠/블라우스", "맨투맨", "후드 티셔츠", "니트/스웨터"),
            List.of("면바지", "슬랙스", "데님 팬츠", "카고 팬츠"),
            List.of("가디건", "바람막이", "블루종"),
            List.of()),

    LEVEL_6(22, 20,
            List.of(),
            List.of("긴소매 티셔츠", "셔츠/블라우스", "맨투맨", "후드 티셔츠"),
            List.of("면바지", "슬랙스", "데님 팬츠", "카고 팬츠"),
            List.of(),
            List.of()),

    LEVEL_7(27, 23,
            List.of(),
            List.of("반소매 티셔츠", "피케/카라 티셔츠", "셔츠/블라우스"),
            List.of("반바지", "린넨 바지", "면바지", "슬랙스"),
            List.of(),
            List.of("캡 모자")),

    LEVEL_8(Integer.MAX_VALUE, 28,
            List.of(),
            List.of("민소매 티셔츠", "반소매 티셔츠", "린넨 셔츠", "피케/카라 티셔츠"),
            List.of("반바지", "린넨 바지"),
            List.of(),
            List.of("캡 모자"));

    private final int maxTemp;
    private final int minTemp;
    private final List<String> innerWear;
    private final List<String> topWear;
    private final List<String> bottomWear;
    private final List<String> outerWear;
    private final List<String> accessories;

    OutfitLevel(int maxTemp, int minTemp, List<String> innerWear, List<String> topWear,
                List<String> bottomWear, List<String> outerWear, List<String> accessories) {
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.innerWear = innerWear;
        this.topWear = topWear;
        this.bottomWear = bottomWear;
        this.outerWear = outerWear;
        this.accessories = accessories;
    }

    /**
     * 온도에 해당하는 OutfitLevel 반환
     * 온도는 반올림하여 정수로 변환 후 매칭 (예: 11.6°C → 12°C)
     */
    public static OutfitLevel fromTemperature(double temperature) {
        int roundedTemp = (int) Math.round(temperature);
        return Arrays.stream(values())
                .filter(level -> roundedTemp >= level.minTemp && roundedTemp <= level.maxTemp)
                .findFirst()
                .orElse(LEVEL_4); // 기본값
    }

    public int getMaxTemp() {
        return maxTemp;
    }

    public int getMinTemp() {
        return minTemp;
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
