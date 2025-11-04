# CLAUDE.md

이 파일은 Claude Code(claude.ai/code)가 이 리포지토리의 코드로 작업할 때 참고할 수 있는 가이드를 제공합니다.

## 1. 프로젝트 개요

Wearther는 실시간 날씨 정보와 의상 제안을 통합한 날씨 및 의상 추천 서비스입니다. MVP는 '문제 1' 해결에 중점을 둡니다. 즉, 10초 이내에 단일 인터페이스에서 날씨와 의상 추천을 제공하여 정보 파편화를 줄이는 것입니다.

**목표**: 사용자가 10초 이내, 5번 미만의 터치로 "오늘 뭐 입지?"를 결정하도록 돕습니다.  

(자세한 요구사항은 `~/docs/PRD.md` 참고)


## 2. 기술 스택
- 프레임워크: Spring boot 3.5.7
- 언어: Java 21
- 빌드: Gradle
- 테스트: Junit5


## 3. 아키텍처

백엔드(BE)와 프론트엔드(FE) 디렉터리가 분리된 모노레포입니다:

- **BE/**: Java 21 기반 Spring Boot 3.5.7 애플리케이션
- **FE/**: 현재 비어 있으며, 프론트엔드 구현 예정 (MVP V1 단계에서는 타임리프로 구현)

### 백엔드 아키텍처

백엔드는 명확한 관심사 분리(separation of concerns)를 따르는 계층형 아키텍처(layered architecture)를 사용합니다:

[예시]
```
com.chxghee.wearther/
├── global/
│   └── config/          # 애플리케이션 전반의 설정 (WebClient 등)
├── weather/
│   ├── presentation/    # REST 컨트롤러
│   ├── application/     # 비즈니스 로직 (서비스)
│   ├── domain/          # 도메인 모델 및 엔티티
│   └── infrastructure/  # 외부 API 클라이언트
│       ├── openweathermap/  # OpenWeatherMap API 연동
│       └── geocoding/       # Geocoding API 연동
```

**주요 아키텍처 패턴**:

- 계층형 아키텍처: presentation -\> application -\> domain \<- infrastructure
- 명확한 도메인 모델을 갖춘 도메인 주도 설계(DDD)
- 외부 API 연동을 위한 인프라스트럭처 계층
- 반응형 API 호출을 위한 Spring WebFlux (WebClient) 사용



## 4. 개발 규칙 

### 코딩 스타일
- [구글 자바 스타일 가이드](https://google.github.io/styleguide/javaguide.html) 를 컨벤션으로 한다.
- 축약어를 쓰지 않고, 네이밍을 보고도 어떤 역할을 하는지 쉽게 알 수 있어야 한다.
- 가독성이 좋은 코드를 작성한다. 
- 복잡한 기능이 있다면 한글로 짧막한 주석을 추가한다.
- DTO는 record 를 사용한다.



### Git 워크플로우 규칙
- 깃 허브 저장소: https://github.com/chxghee/wearther
- Main Branch: `main`
- 깃 컨벤션은 'Conventional Commits' 를 따른다.
- 모든 기능 개발은 `feature/[간단-설명-kebab-case]` 형식의 브랜치에서 진행한다.
- 이슈 번호가 없는 간단한 수정은 `fix/[간단-설명]` 또는 `chore/[간단-설명]` 브랜치를 사용한다.
- 커밋 메세지는 한글로 작성한다.
- 구현한 기능을 한번에 커밋하지 않고 단계별로 끊어서 커밋한다.
- 구현한 기능과 관련된 이슈가 있다면 커밋 메세지 제목 맨 끝에 [#이슈번호] 형식으로 이슈를 추가한다.  
  (예: `feat: 날씨 조회 기능 구현 [#이슈번호]`, `fix: `)
- 커밋 본문에는 변경 이유를 명확히 서술한다.
- 임의로 메인 브랜치에 머지나 강제 푸시하지 않는다.
- PR을 작성할 떄는 템플릿(`.github/PULL_REQUEST_TEMPLATE.md`)을 활용하고, 어떤 방식으로 구현을 했는지 '기타' 항목에 설명을 추가한다. 



### 그 외
- 개발 구현 완료 되면 테스트 코드를 작성하여 구현한 기능이 제대로 동작하는지 확인한다.
- 외부 API에 의존하는 기능에 대한 테스트의 경우, 외부 API를 Mocking 하여 테스트를 진행한다.
- 구현할 기능을 단계별로 나누어 개발을 진행하고 나뉜 단계마다 커밋을 작성한다.


## 5. 주요 개발 명령어

### 백엔드 (Spring Boot)

모든 Gradle 명령어는 `BE/` 디렉터리에서 실행해야 합니다:

```bash
cd BE

# 프로젝트 빌드
./gradlew build

# 테스트 실행
./gradlew test

# 단일 테스트 클래스 실행
./gradlew test --tests "ClassName"

# 단일 테스트 메서드 실행
./gradlew test --tests "ClassName.methodName"

# 애플리케이션 실행
./gradlew bootRun

# 빌드 산출물 정리
./gradlew clean
```

## 6. 외부 API 연동

애플리케이션은 세 개의 외부 API와 연동됩니다:

1.  **OpenWeatherMap Current Weather API**: 실시간 현재 날씨 조회

   - 클라이언트: `OpenWeatherClient` (weather/infrastructure/openweathermap/)
   - 엔드포인트: `/data/2.5/weather`
   - 메서드: `getCurrentWeather(double lat, double lon)`
   - 역할: 사용자 조회 시점의 실시간 날씨 정보 제공
   - 응답: `CurrentWeatherResponse` DTO
   - `external.openweather.api-key` 속성을 통한 API 키 설정 필요

2.  **OpenWeatherMap Forecast API**: 날씨 예보 데이터 조회

   - 클라이언트: `OpenWeatherClient` (weather/infrastructure/openweathermap/)
   - 엔드포인트: `/data/2.5/forecast`
   - 메서드: `getForecast(double lat, double lon)`
   - 역할: 5일간의 3시간 간격 예보 데이터 제공
   - 응답: `OpenWeatherResponse` DTO
   - `external.openweather.api-key` 속성을 통한 API 키 설정 필요

3.  **Geocoding API**: 도시 이름을 좌표로 변환

   - 클라이언트: `GeoCodingClient` (weather/infrastructure/geocoding/)
   - 참고: 현재 OpenWeatherClient와 동일한 플레이스홀더(placeholder) 구현 상태임

**설정**: API 키는 application.yml에 설정해야 합니다:

```yml
external.openweather.api-key=YOUR_API_KEY
```

**Current Weather API 통합 배경**:
- Forecast API는 3시간 간격의 고정된 예보 시점(00:00, 03:00, 06:00 등)만 제공
- 사용자 조회 시점과 예보 시점이 최대 3시간까지 차이날 수 있는 문제 발생
- Current Weather API를 추가하여 실시간 현재 날씨를 제공함으로써 시간 불일치 문제 해결

## 7. 주요 구현 세부사항

- **WebClient 설정**: OpenWeatherMap의 기본 URL과 함께 `global/config/WebClientConfig.java`에 설정됨
- **반응형 API 호출**: 동기적 동작을 위해 `.block()`과 함께 Spring WebFlux WebClient 사용
- **Lombok**: 상용구(boilerplate) 코드 감소를 위해 활성화됨 (`@RequiredArgsConstructor`, `@Data` 등)
- **유효성 검사**: 요청 유효성 검사를 위해 Spring Validation 의존성 사용 가능

## 8. 중요 참고사항

- 의상 추천 로직(기온 레벨 매핑)은 데이터베이스가 아닌 인메모리(in-memory)로 구현됨
- 사용자 데이터나 옷장 정보는 저장되지 않음 (MVP 범위)
- MVP v1 에서는 프론트엔드(FE/)는 타임리프로 구현
- 주요 키 값은 `@dev.env` 라는 환경변수 파일에 저장되어 있다. (사용되는 날씨 API 키의 경우 WEATHER_API_KEY=keyyyyy... 형태로 저장되어 있다.)
- `@dev.env`은 읽기, 수정, 삭제를 할 수 없다.
    



