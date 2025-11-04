// 날씨 아이콘 매핑 함수
function getWeatherEmoji(weatherMain, hour = 12) {
    const emojiMap = {
        'Thunderstorm': '⛈️',
        'Drizzle': '🌦️',
        'Rain': '🌧️',
        'Snow': '❄️',
        'Clouds': '☁️',
        'Mist': '🌫️',
        'Clear': (hour >= 6 && hour < 18) ? '☀️' : '🌙'
    };

    return emojiMap[weatherMain] || '☀️';
}

// 시간 포맷팅 (HH시)
// dateTimeStr은 백엔드에서 이미 현지 시간으로 변환된 상태 (timezone 적용됨)
function formatHour(dateTimeStr) {
    // "yyyy-MM-dd HH:mm:ss" 형식에서 시간 추출
    const timePart = dateTimeStr.split(' ')[1]; // "HH:mm:ss"
    const hour = parseInt(timePart.split(':')[0], 10); // HH
    return `${hour}시`;
}

// 한글 날씨 설명 변환
function getKoreanWeatherDescription(weatherMain) {
    const descriptionMap = {
        'Thunderstorm': '천둥번개',
        'Drizzle': '이슬비',
        'Rain': '비',
        'Snow': '눈',
        'Clear': '맑음',
        'Clouds': '구름',
        'Mist': '안개'
    };

    return descriptionMap[weatherMain] || weatherMain;
}

// 현재 날씨 렌더링
function renderCurrentWeather(currentWeather) {
    const currentTempElement = document.getElementById('current-temperature');
    const currentIconElement = document.getElementById('current-weather-icon');
    const currentDescElement = document.getElementById('current-weather-description');

    if (currentTempElement) {
        currentTempElement.textContent = `${Math.round(currentWeather.temperature)}°`;
    }

    if (currentIconElement) {
        const now = new Date();
        currentIconElement.textContent = getWeatherEmoji(currentWeather.weatherMain, now.getHours());
    }

    if (currentDescElement) {
        currentDescElement.textContent = getKoreanWeatherDescription(currentWeather.weatherMain);
    }
}

// 날씨 요약 렌더링
function renderWeatherSummary(summary) {
    const minTempElement = document.getElementById('min-temperature');
    const maxTempElement = document.getElementById('max-temperature');
    const commentElement = document.getElementById('weather-comment');

    if (minTempElement) {
        minTempElement.textContent = `${Math.round(summary.minTemperature)}`;
    }

    if (maxTempElement) {
        maxTempElement.textContent = `${Math.round(summary.maxTemperature)}`;
    }

    if (commentElement) {
        commentElement.textContent = summary.comment;
    }
}

// 시간대별 예보 렌더링
function renderHourlyForecasts(forecasts) {
    const container = document.getElementById('hourly-forecast-container');
    if (!container) return;

    container.innerHTML = '';

    forecasts.forEach((forecast, index) => {
        // dateTimeStr은 이미 현지 시간 (백엔드에서 timezone 적용됨)
        const dateTimeStr = forecast.dateTime;
        const timePart = dateTimeStr.split(' ')[1]; // "HH:mm:ss"
        const hour = parseInt(timePart.split(':')[0], 10); // HH

        const forecastCard = document.createElement('div');
        forecastCard.className = 'forecast-card';

        const timeLabel = index === 0 ? '지금' : formatHour(dateTimeStr);

        forecastCard.innerHTML = `
            <div class="forecast-time">${timeLabel}</div>
            <div class="forecast-icon">${getWeatherEmoji(forecast.weatherMain, hour)}</div>
            <div class="forecast-temp">${Math.round(forecast.temperature)}°</div>
        `;

        container.appendChild(forecastCard);
    });
}

// 옷차림 추천 렌더링
function renderOutfitRecommendations(outfit) {
    const container = document.getElementById('outfit-container');
    if (!container) return;

    container.innerHTML = '';

    // 디버깅: mainLevelKey 확인
    console.log('=== 옷차림 추천 디버깅 ===');
    console.log('mainLevelKey:', outfit.mainLevelKey);
    console.log('outfitByLevel keys:', Object.keys(outfit.outfitByLevel));

    // 레벨 키를 순서대로 정렬 (LEVEL_1이 가장 추운 날씨)
    const sortedLevels = Object.keys(outfit.outfitByLevel).sort((a, b) => {
        const numA = parseInt(a.replace('LEVEL_', ''));
        const numB = parseInt(b.replace('LEVEL_', ''));
        return numB - numA; // 내림차순 (높은 레벨부터)
    });

    console.log('sortedLevels:', sortedLevels);

    sortedLevels.forEach((levelKey, index) => {
        const levelData = outfit.outfitByLevel[levelKey];
        const isMainLevel = levelKey === outfit.mainLevelKey;

        // 디버깅: 각 레벨의 mainLevel 여부 확인
        console.log(`[${index}] ${levelKey}: isMainLevel = ${isMainLevel} (온도: ${levelData.temperatureRange})`);

        const outfitCard = document.createElement('div');
        outfitCard.className = `outfit-card ${isMainLevel ? 'main-level' : ''}`;

        let cardHTML = `
            <div class="outfit-header">
                <span class="temp-range">${levelData.temperatureRange}</span>
                ${isMainLevel ? '<span class="recommended-badge">⭐ 추천</span>' : ''}
            </div>
            <div class="outfit-content">
        `;

        // 아우터
        if (levelData.outerWear && levelData.outerWear.length > 0) {
            cardHTML += `
                <div class="outfit-category">
                    <div class="category-header">
                        <span class="category-icon">🧥</span>
                        <span class="category-title">아우터</span>
                    </div>
                    <div class="category-items">
                        ${levelData.outerWear.map(item => `<span class="item-tag">${item}</span>`).join('')}
                    </div>
                </div>
            `;
        }

        // 상의
        if (levelData.topWear && levelData.topWear.length > 0) {
            cardHTML += `
                <div class="outfit-category">
                    <div class="category-header">
                        <span class="category-icon">👕</span>
                        <span class="category-title">상의</span>
                    </div>
                    <div class="category-items">
                        ${levelData.topWear.map(item => `<span class="item-tag">${item}</span>`).join('')}
                    </div>
                </div>
            `;
        }

        // 하의
        if (levelData.bottomWear && levelData.bottomWear.length > 0) {
            cardHTML += `
                <div class="outfit-category">
                    <div class="category-header">
                        <span class="category-icon">👖</span>
                        <span class="category-title">하의</span>
                    </div>
                    <div class="category-items">
                        ${levelData.bottomWear.map(item => `<span class="item-tag">${item}</span>`).join('')}
                    </div>
                </div>
            `;
        }

        // 악세서리
        if (levelData.accessories && levelData.accessories.length > 0) {
            cardHTML += `
                <div class="outfit-category">
                    <div class="category-header">
                        <span class="category-icon">🧢</span>
                        <span class="category-title">악세서리</span>
                    </div>
                    <div class="category-items">
                        ${levelData.accessories.map(item => `<span class="item-tag">${item}</span>`).join('')}
                    </div>
                </div>
            `;
        }

        cardHTML += `
            </div>
        `;

        outfitCard.innerHTML = cardHTML;
        container.appendChild(outfitCard);
    });
}

// 로딩/에러 메시지 제어
function showLoading() {
    const loading = document.getElementById('loading-message');
    const error = document.getElementById('error-message');

    if (loading) loading.style.display = 'flex';
    if (error) error.style.display = 'none';
}

function hideLoading() {
    const loading = document.getElementById('loading-message');
    if (loading) loading.style.display = 'none';
}

function showError() {
    const loading = document.getElementById('loading-message');
    const error = document.getElementById('error-message');

    if (loading) loading.style.display = 'none';
    if (error) error.style.display = 'flex';
}

// API 호출 및 데이터 렌더링
async function fetchWeatherAndOutfit() {
    showLoading();

    try {
        const response = await fetch('/api/v1/weather-outfit?city=Seoul');

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();

        // 각 섹션 렌더링
        renderCurrentWeather(data.currentWeather);
        renderWeatherSummary(data.weatherSummary);
        renderHourlyForecasts(data.hourlyForecasts);
        renderOutfitRecommendations(data.outfit);

        hideLoading();
    } catch (error) {
        console.error('날씨 정보를 가져오는데 실패했습니다:', error);
        showError();
    }
}

// 페이지 로드 시 실행
document.addEventListener('DOMContentLoaded', () => {
    fetchWeatherAndOutfit();
});
