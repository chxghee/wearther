# API 명세서

## 개요
Wearther API는 실시간 날씨 정보와 온도별 옷차림 추천을 통합하여 제공합니다.

**Base URL**: `http://localhost:8080` (개발 환경)

---

## 엔드포인트 목록

### 1. 통합 날씨-옷차림 조회 API

도시 이름으로 날씨 정보와 해당 날씨에 어울리는 옷차림을 조회합니다.

#### 기본 정보
- **URL**: `/api/v1/weather-outfit`
- **Method**: `GET`
- **Content-Type**: `application/json`

#### Query Parameters

| 파라미터 | 타입 | 필수 여부 | 기본값 | 설명 | 예시 |
|---------|------|----------|--------|------|------|
| `city` | String | 선택 | `Seoul` | 영문 도시명 | `Seoul`, `London`, `Tokyo` |

#### 성공 응답 (200 OK)

**Response Body**

```json
{
  "currentWeather": {
    "temperature": 15.5,
    "weatherMain": "Clear",
    "weatherDescription": "clear sky",
    "weatherIcon": "01d"
  },
  "weatherSummary": {
    "minTemperature": 12.0,
    "maxTemperature": 18.0,
    "comment": "오전과 오후 기온차가 있어요. 겉옷을 챙기세요."
  },
  "hourlyForecasts": [
    {
      "dateTime": "2025-01-03 09:00:00",
      "temperature": 12.0,
      "weatherMain": "Clear",
      "weatherDescription": "clear sky",
      "weatherIcon": "01d"
    },
    {
      "dateTime": "2025-01-03 12:00:00",
      "temperature": 15.5,
      "weatherMain": "Clear",
      "weatherDescription": "clear sky",
      "weatherIcon": "01d"
    },
    {
      "dateTime": "2025-01-03 15:00:00",
      "temperature": 18.0,
      "weatherMain": "Clouds",
      "weatherDescription": "few clouds",
      "weatherIcon": "02d"
    }
    // ... 24시간치 데이터 (3시간 간격, 총 8개)
  ],
  "outfit": {
    "mainLevelKey": "LEVEL_5",
    "outfitByLevel": {
      "LEVEL_4": {
        "temperatureRange": "12°C ~ 16°C",
        "innerWear": [],
        "topWear": [
          "긴소매 티셔츠",
          "셔츠/블라우스",
          "맨투맨",
          "후드 티셔츠",
          "니트/스웨터"
        ],
        "bottomWear": [
          "면바지",
          "슬랙스",
          "데님 팬츠",
          "카고 팬츠"
        ],
        "outerWear": [
          "가디건",
          "블레이저",
          "트러커 자켓",
          "가죽 자켓",
          "블루종",
          "플리스 자켓"
        ],
        "accessories": []
      },
      "LEVEL_5": {
        "temperatureRange": "17°C ~ 19°C",
        "innerWear": [],
        "topWear": [
          "긴소매 티셔츠",
          "셔츠/블라우스",
          "맨투맨",
          "후드 티셔츠",
          "니트/스웨터"
        ],
        "bottomWear": [
          "면바지",
          "슬랙스",
          "데님 팬츠",
          "카고 팬츠"
        ],
        "outerWear": [
          "가디건",
          "바람막이",
          "블루종"
        ],
        "accessories": []
      },
      "LEVEL_6": {
        "temperatureRange": "20°C ~ 22°C",
        "innerWear": [],
        "topWear": [
          "긴소매 티셔츠",
          "셔츠/블라우스",
          "맨투맨",
          "후드 티셔츠"
        ],
        "bottomWear": [
          "면바지",
          "슬랙스",
          "데님 팬츠",
          "카고 팬츠"
        ],
        "outerWear": [],
        "accessories": []
      }
    }
  }
}
```

**Response Fields**

| 필드 | 타입 | 설명 |
|------|------|------|
| `currentWeather` | Object | 현재 날씨 정보 |
| `currentWeather.temperature` | Double | 현재 기온 (°C) |
| `currentWeather.weatherMain` | String | 날씨 상태 (Clear, Clouds, Rain 등) |
| `currentWeather.weatherDescription` | String | 날씨 상세 설명 |
| `currentWeather.weatherIcon` | String | 날씨 아이콘 코드 |
| `weatherSummary` | Object | 12시간 날씨 요약 |
| `weatherSummary.minTemperature` | Double | 12시간 내 최저 기온 (°C) |
| `weatherSummary.maxTemperature` | Double | 12시간 내 최고 기온 (°C) |
| `weatherSummary.comment` | String | 날씨 분석 코멘트 |
| `hourlyForecasts` | Array | 시간대별 예보 (24시간, 3시간 간격) |
| `hourlyForecasts[].dateTime` | String | 예보 시간 (yyyy-MM-dd HH:mm:ss) |
| `hourlyForecasts[].temperature` | Double | 예상 기온 (°C) |
| `hourlyForecasts[].weatherMain` | String | 날씨 상태 |
| `hourlyForecasts[].weatherDescription` | String | 날씨 상세 설명 |
| `hourlyForecasts[].weatherIcon` | String | 날씨 아이콘 코드 |
| `outfit` | Object | 옷차림 추천 정보 |
| `outfit.mainLevelKey` | String | 가장 빈번한 온도 레벨 (LEVEL_1~LEVEL_8) |
| `outfit.outfitByLevel` | Object | 레벨별 옷차림 정보 |
| `outfit.outfitByLevel.{LEVEL}` | Object | 특정 레벨의 옷차림 정보 |
| `outfit.outfitByLevel.{LEVEL}.temperatureRange` | String | 해당 레벨의 온도 범위 |
| `outfit.outfitByLevel.{LEVEL}.innerWear` | Array\<String\> | 추천 이너웨어 목록 |
| `outfit.outfitByLevel.{LEVEL}.topWear` | Array\<String\> | 추천 상의 목록 |
| `outfit.outfitByLevel.{LEVEL}.bottomWear` | Array\<String\> | 추천 하의 목록 |
| `outfit.outfitByLevel.{LEVEL}.outerWear` | Array\<String\> | 추천 아우터 목록 |
| `outfit.outfitByLevel.{LEVEL}.accessories` | Array\<String\> | 추천 악세서리 목록 |

#### 에러 응답

**400 Bad Request** - 잘못된 도시명

```json
{
  "timestamp": "2025-01-03T09:00:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "도시를 찾을 수 없습니다: InvalidCity",
  "path": "/api/v1/weather-outfit"
}
```

**500 Internal Server Error** - 외부 API 오류

```json
{
  "timestamp": "2025-01-03T09:00:00.000+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "날씨 정보를 가져오는 중 오류가 발생했습니다.",
  "path": "/api/v1/weather-outfit"
}
```

---

## 날씨 코멘트 생성 규칙

날씨 요약 코멘트는 다음 조건에 따라 자동 생성됩니다:

| 조건 | 코멘트 |
|------|--------|
| 일교차 ≥ 10°C | "일교차가 크니 얇은 외투를 챙기세요." |
| 최고 온도 ≥ 28°C | "더운 날씨예요. 시원한 옷차림을 추천해요." |
| 최저 온도 ≤ 5°C | "쌀쌀한 날씨예요. 따뜻하게 입으세요." |
| 일교차 ≥ 5°C | "오전과 오후 기온차가 있어요. 겉옷을 챙기세요." |
| 기타 | "쾌적한 날씨예요." |

---

## 온도별 옷차림 레벨

옷차림 추천은 다음 8단계 온도 레벨을 기준으로 합니다:

| 레벨 | 온도 범위 | 주요 아이템 |
|------|----------|------------|
| **LEVEL_8** | 28°C 이상 | 민소매 티셔츠, 반소매 티셔츠, 반바지 |
| **LEVEL_7** | 23°C ~ 27°C | 반소매 티셔츠, 피케/카라 티셔츠, 반바지 |
| **LEVEL_6** | 20°C ~ 22°C | 긴소매 티셔츠, 맨투맨, 면바지 |
| **LEVEL_5** | 17°C ~ 19°C | 긴소매 티셔츠, 맨투맨, 가디건, 바람막이 |
| **LEVEL_4** | 12°C ~ 16°C | 맨투맨, 니트/스웨터, 가디건, 자켓 |
| **LEVEL_3** | 9°C ~ 11°C | 니트/스웨터, 트렌치 코트, 경량 패딩 |
| **LEVEL_2** | 5°C ~ 8°C | 히트텍, 니트/스웨터, 울 코트, 패딩 |
| **LEVEL_1** | 4°C 이하 | 히트텍, 패딩, 울 코트, 목도리, 장갑 |

### 추천 로직

1. **온도 반올림 매칭**
   - 모든 온도는 반올림하여 정수로 변환 후 레벨 매칭
   - 예: 11.6°C → 12°C, 11.4°C → 11°C, 9.85°C → 10°C
   - 이를 통해 온도 범위 갭 문제를 해결하고 직관적인 매칭 제공

2. **12시간 내 최저/최고 온도 범위**에 해당하는 각 레벨의 옷차림을 레벨별로 구분하여 제공
   - 최저 온도 (반올림) 이상 ~ 최고 온도 (반올림) 이하 범위의 모든 레벨 포함

3. **각 레벨**은 온도 범위와 함께 해당 레벨의 옷차림 카테고리(상의, 하의, 아우터, 악세서리)를 포함

4. **가장 빈번하게 나타나는 온도 레벨**을 `mainLevelKey`로 표시 (참고용)
   - 12시간 내 시간대별 온도(3시간 간격, 총 4개)를 각각 반올림하여 레벨 매칭
   - 가장 많이 나타나는 레벨을 mainLevel로 선정

#### 예시 1: 반올림 적용

실제 온도: 9.85°C → 11.51°C → 12.17°C → 11.98°C

**반올림 후:**
- 9.85°C → **10°C** → LEVEL_3 (9~11°C)
- 11.51°C → **12°C** → LEVEL_4 (12~16°C)
- 12.17°C → **12°C** → LEVEL_4 (12~16°C)
- 11.98°C → **12°C** → LEVEL_4 (12~16°C)

**결과:**
- `outfitByLevel`: LEVEL_3, LEVEL_4 제공 (최저 10°C ~ 최고 12°C 범위)
- `mainLevelKey`: "LEVEL_4" (4개 중 3개가 LEVEL_4)

#### 예시 2: 기본 케이스

실제 온도: 12°C → 15°C → 18°C → 17°C

**반올림 후:**
- 12°C → **12°C** → LEVEL_4 (12~16°C)
- 15°C → **15°C** → LEVEL_4 (12~16°C)
- 18°C → **18°C** → LEVEL_5 (17~19°C)
- 17°C → **17°C** → LEVEL_5 (17~19°C)

**결과:**
- `outfitByLevel`: LEVEL_4, LEVEL_5 제공
- `mainLevelKey`: "LEVEL_4" 또는 "LEVEL_5" (동점, 먼저 발견된 LEVEL_4)

---

## 사용 예시

### cURL

```bash
# 기본 (서울 날씨)
curl -X GET "http://localhost:8080/api/v1/weather-outfit"

# 특정 도시
curl -X GET "http://localhost:8080/api/v1/weather-outfit?city=London"
```

### JavaScript (Fetch API)

```javascript
// 기본 (서울 날씨)
fetch('http://localhost:8080/api/v1/weather-outfit')
  .then(response => response.json())
  .then(data => console.log(data))
  .catch(error => console.error('Error:', error));

// 특정 도시
fetch('http://localhost:8080/api/v1/weather-outfit?city=Tokyo')
  .then(response => response.json())
  .then(data => console.log(data))
  .catch(error => console.error('Error:', error));
```

### Java (RestTemplate)

```java
RestTemplate restTemplate = new RestTemplate();
String url = "http://localhost:8080/api/v1/weather-outfit?city=Seoul";
WeatherOutfitResponse response = restTemplate.getForObject(url, WeatherOutfitResponse.class);
```

---

## 참고사항

### 외부 API 의존성
- **OpenWeatherMap Geocoding API**: 도시 이름 → 좌표 변환
- **OpenWeatherMap 5 Day Forecast API**: 날씨 예보 조회
- API 키는 서버 환경변수 `WEATHER_API_KEY`로 관리됩니다.

### 응답 시간
- **목표**: P50 3초 이내, P90 5초 이내
- 외부 API 호출이 포함되어 있어 네트워크 상황에 따라 달라질 수 있습니다.

### 캐싱
- 현재 버전(v1.0)에서는 캐싱이 구현되어 있지 않습니다.
- 향후 버전에서 Redis 등을 활용한 캐싱 기능 추가 예정입니다.

### 데이터 갱신 주기
- OpenWeatherMap API는 3시간 간격으로 데이터를 제공합니다.
- 실시간 조회 시마다 최신 예보 데이터를 반환합니다.

---

## 변경 이력

| 버전 | 날짜 | 변경 내용 |
|------|------|----------|
| v1.1 | 2025-01-03 | 옷차림 응답 구조 변경 - 레벨별 구분 제공 (outfitByLevel) |
| v1.0 | 2025-01-03 | 초기 버전 작성 - GET /api/v1/weather-outfit 엔드포인트 |