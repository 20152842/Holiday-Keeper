# Holiday-Keeper
> **“Nager.Date 무인증 API 활용"**
---

## 🎯 프로젝트 목표

외부 API 두 개만으로 최근 5 년(2020 ~ 2025) 의 전 세계 공휴일 데이터를 저장·조회·관리하는 Mini Service 구현

---

## 📌 외부 API

| 용도 | 엔드포인트 | 응답 |
|------|------|------|
| 국가목록 | GET https://date.nager.at/api/v3/AvailableCountries | 국가배열 |
| 특정 연도 공휴일 | GET https://date.nager.at/api/v3/PublicHolidays/{year}/{countryCode}<br>e.g. https://date.nager.at/api/v3/PublicHolidays/2025/KR | 공휴일 |

별도의 ***인증‧API Key가 전혀 필요 없습니다.***

---
## 🏃 빌드 & 실행 방법 & 실행 결과

### 1) 환경 요구사항

  - Java 21
  - Gradle Wrapper (`./gradlew`) 사용
  - DB: 인메모리 H2 (추가 설치 불필요)

### 2) 빌드
```
./gradlew clean build
```
### 3) 실행
```
 ./gradlew bootRun
```
### 4) ./gradlew clean test 성공 스크린샷

<img width="1872" height="388" alt="image" src="https://github.com/user-attachments/assets/9670dc7b-06b0-4c61-b523-8169c9ab6bfe" />

### 5) Swagger UI & OpenAPI JSON 확인
```
- 기본 포트: http://localhost:8080

- Swagger UI: http://localhost:8080/swagger-ui/index.html

- OpenAPI JSON: http://localhost:8080/v3/api-docs
```
---

## 📌 REST API 명세 요약

| method | endpoint | request body | response | 설명 |
|--------|----------|--------------|----------|-------|
| POST | /api/v1/holidays | 없음 | json<br>{<br>&nbsp;&nbsp;"status": "success",<br>&nbsp;&nbsp;"years": [2020,2021,2022,2023,2024,2025],<br>&nbsp;&nbsp;"countriesCount": 110,<br>&nbsp;&nbsp;"totalHolidaysInserted": 15423<br>} | 최근 5년 공휴일 전체 로드 |
| GET | /api/v1/holidays | 없음 (Query 사용)<br>year, country, from, to, type, page, size | json<br>{<br>&nbsp;&nbsp;"page": 0,<br>&nbsp;&nbsp;"size": 20,<br>&nbsp;&nbsp;"total": 42,<br>&nbsp;&nbsp;"items": [<br>&nbsp;&nbsp;&nbsp;&nbsp;{<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"date": "2025-01-01",<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"localName": "신정",<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"englishName": "New Year's Day",<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"countryCode": "KR",<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"global": true,<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"fixed": true,<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"types": ["Public"]<br>&nbsp;&nbsp;&nbsp;&nbsp;}<br>&nbsp;&nbsp;]<br>} | 공휴일 검색 (필터 & 페이징) |
| PUT | /api/v1/holidays | json<br>{<br>&nbsp;&nbsp;"year": 2025,<br>&nbsp;&nbsp;"country": "KR"<br>} | json<br>{<br>&nbsp;&nbsp;"status": "success",<br>&nbsp;&nbsp;"year": 2025,<br>&nbsp;&nbsp;"country": "KR",<br>&nbsp;&nbsp;"updatedCount": 17<br>} | 특정 연도·국가 공휴일 재동기화 (Upsert) |
| DELETE | /api/v1/holidays | 없음 (Query 사용)<br>year, country | json<br>{<br>&nbsp;&nbsp;"status": "success",<br>&nbsp;&nbsp;"deleted": 17<br>} | 특정 연도·국가 공휴일 전체 삭제 |
| GET | /api/v1/countries | 없음 | json<br>[<br>&nbsp;&nbsp;{ "code": "KR", "name": "Korea" },<br>&nbsp;&nbsp;{ "code": "US", "name": "United States" }<br>] | 국가 목록 조회 |
| CRON | (자동 실행)<br>0 0 1 2 * | 없음 | json<br>{<br>&nbsp;&nbsp;"status": "success",<br>&nbsp;&nbsp;"syncedYears": [2024, 2025],<br>&nbsp;&nbsp;"countries": 110<br>} | 매년 1/2 01:00 전년도·금년도 공휴일 자동 동기화 |

---

## 📌 ERD

<img width="1187" height="426" alt="image" src="https://github.com/user-attachments/assets/e5dd8e32-a831-4985-9151-b34ee17e231c" />


---

## 📌 테이블 명세서

**countries**
| 필드명 | 타입 | 설명 |
|--------|--------|--------|
| id | bigint (PK) | 기본 키 |
| code | varchar(10) | ISO 국가 코드 |
| name | varchar(255) | 국가 이름 |

**holidays**
| 필드명 | 타입 | 설명 |
|--------|--------|--------|
| id | bigint (PK) | 기본 키 |
| country_code | varchar(10) (FK → countries.code) | 국가 코드 |
| date | date | 공휴일 날짜 |
| local_name | varchar(255) | 현지 언어 명칭 |
| name | varchar(255) | 영문 공휴일 명칭 |
| fixed | boolean | 매년 동일 여부 |
| global | boolean | 전역 공휴일 여부 |
| type | varchar(255) | 공휴일 타입 |
| counties | text | 적용 지역 목록 |
| launch_year | int | 최초 지정 연도 |
| created_at | timestamp | 등록일 |


