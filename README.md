# Holiday-Keeper
> **“Nager.Date 무인증 API 활용"**
---

## 🎯 프로젝트 목표

외부 API 두 개만으로 최근 5 년(2020 ~ 2025) 의 전 세계 공휴일 데이터를 저장·조회·관리하는 Mini Service 구현

---

## 📌 외부 API
| 용도 | 엔드포인트 | 응답 |
| 국가목록 | GET https://date.nager.at/api/v3/AvailableCountries | 국가배열 |
특정 연도 공휴일 GET https://date.nager.at/api/v3/PublicHolidays/{year}/
{countryCode}
e.g. https://date.nager.at/api/v3/PublicHolidays/2025/KR
공휴일
별도의 인증‧API Key가 전혀 필요 없습니다.
