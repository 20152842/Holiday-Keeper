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

### 5) Swagger UI & OpenAPI JSON & H2 Console
```
- 기본 포트: http://localhost:8080

- Swagger UI: http://localhost:8080/swagger-ui/index.html

- OpenAPI JSON: http://localhost:8080/v3/api-docs

- H2 Console: http://localhost:8080/h2-console
  - Driver: org.h2.Driver
  - JDBC URL: jdbc:h2:mem:holidaydb
  - User Name: test
  - Password: 1234
```
---

## 📌 REST API 명세 요약

| method | endpoint | request body (예시 포함) | response | 설명 |
|--------|----------|--------------------------|----------|-------|
| POST | /api/v1/holidays | 없음 | json<br>{<br>&nbsp;&nbsp;"status": "success",<br>&nbsp;&nbsp;"years": [2020,2021,2022,2023,2024,2025],<br>&nbsp;&nbsp;"countriesCount": 110,<br>&nbsp;&nbsp;"totalHolidaysInserted": 15423<br>} | 최근 5년 공휴일 전체 로드 |
| GET | /api/v1/holidays | 없음 (Query 사용)<br>**예시 1**<br>`GET /api/v1/holidays?country=KR&year=2025&page=0&size=20`<br>**예시 2**<br>`GET /api/v1/holidays?from=2025-01-01&to=2025-12-31&type=Public&page=0&size=50` | json<br>{<br>&nbsp;&nbsp;"page": 0,<br>&nbsp;&nbsp;"size": 20,<br>&nbsp;&nbsp;"total": 42,<br>&nbsp;&nbsp;"items": [<br>&nbsp;&nbsp;&nbsp;&nbsp;{<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"date": "2025-01-01",<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"localName": "신정",<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"englishName": "New Year's Day",<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"countryCode": "KR",<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"global": true,<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"fixed": true,<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"types": ["Public"]<br>&nbsp;&nbsp;&nbsp;&nbsp;}<br>&nbsp;&nbsp;]<br>} | 공휴일 검색 (필터 & 페이징) |
| PUT | /api/v1/holidays | json<br>**예시 1**<br>`{ "year": 2025, "country": "KR" }`<br>**예시 2**<br>`{ "year": 2024, "country": "US" }` | json<br>{<br>&nbsp;&nbsp;"status": "success",<br>&nbsp;&nbsp;"year": 2025,<br>&nbsp;&nbsp;"country": "KR",<br>&nbsp;&nbsp;"updatedCount": 17<br>} | 특정 연도·국가 공휴일 재동기화 (Upsert) |
| DELETE | /api/v1/holidays | 없음 (Query 사용)<br>**예시 1**<br>`DELETE /api/v1/holidays?year=2025&country=KR`<br>**예시 2**<br>`curl -X DELETE "http://localhost:8080/api/v1/holidays?year=2020&country=US"` | json<br>{<br>&nbsp;&nbsp;"status": "success",<br>&nbsp;&nbsp;"deleted": 17<br>} | 특정 연도·국가 공휴일 전체 삭제 |
| CRON | (자동 실행)<br>0 0 1 2 1 ? | 없음<br>**예시 1**<br>`2025-02-01 01:00 KST → prevYear=2024, currentYear=2025`<br>**예시 2**<br>`2026-02-01 01:00 KST → prevYear=2025, currentYear=2026` | json<br>{<br>&nbsp;&nbsp;"status": "success",<br>&nbsp;&nbsp;"syncedYears": [2024, 2025],<br>&nbsp;&nbsp;"countries": 110<br>} | 매년 2월 1일 01:00 KST 기준, 전년도·금년도 공휴일 자동 동기화 |

---

## 📌 ERD

<img width="1177" height="397" alt="image" src="https://github.com/user-attachments/assets/bfc15f04-1356-4172-b868-34fa1236310a" />


---

## 📌 테이블 명세서

**countries**
| 필드명       | 타입          | 설명          |
|-------------|---------------|---------------|
| id          | bigint (PK)   | 기본 키       |
| country_code| varchar(10)   | ISO 국가 코드 |
| name        | varchar(255)  | 국가 이름     |

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

---
## 📚 참고 자료

- **Spring Test**
  - [Spring Test 기본 개념 및 사용법](https://soonmin.tistory.com/85)

- **Spring QueryDSL**
  - [Spring Boot + QueryDSL 설정 및 예제](https://adjh54.tistory.com/484)

- **Spring Batch**
  - [데이터 동기화 스케줄러, 나는 왜 회사에서 Spring Batch를 선택했을까?](https://jh2021.tistory.com/44)
  - [실습 예제 프로젝트 – roaming-data-synchronizer](https://github.com/ljh468/roaming-data-synchronizer?tab=readme-ov-file#%EB%AA%A8%EB%8B%88%ED%84%B0%EB%A7%81-%EB%B0%8F-%ED%8A%B8%EB%9F%AC%EB%B8%94%EC%8A%88%ED%8C%85)


