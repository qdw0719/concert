## 마일스톤

![image](https://github.com/qdw0719/concert/assets/84309890/0d44b5b6-16ef-43f5-a2d0-499d482cbe6b)

---


## 1. 요구사항 분석

### 1-1. 기능 요구사항

1. **유저 토큰 발급 API**
    - 유저의 UUID와 대기열 정보를 포함한 토큰 발급.
    - 대기열 정보는 대기 순서와 잔여 시간을 포함.
    - 모든 API는 이 토큰을 이용해 대기열 검증 후 이용 가능.
2. **콘서트 목록 조회 API**
    - 예약 가능한 콘서트 목록을 조회.
3. **콘서트 일정 조회 API**
    - 특정 콘서트의 예약 가능한 날짜 및 지역 목록 조회.
4. **예약 가능 좌석 조회 API**
    - 특정 콘서트의 특정 날짜에 예약 가능한 좌석 정보 조회.
    - 좌석 정보는 1 ~ 50까지의 좌석번호로 관리.
5. **좌석 예약 요청 API**
    - 콘서트, 날짜와 좌석 정보를 입력받아 좌석을 예약 처리.
    - 좌석 예약 시, 해당 좌석을 5분간 임시 배정.
    - 5분 내 결제가 완료되지 않으면 임시 배정 해제.
6. **잔액 충전/조회 API**
    - 결제에 사용될 금액을 API를 통해 충전.
    - 사용자 식별자 및 충전 금액을 받아 잔액 충전.
    - 사용자 식별자를 통해 잔액 조회.
7. **결제 API**
    - 결제 처리 및 결제 내역 생성.
    - 결제가 완료되면 좌석 배정 및 대기열 토큰 만료.


### 1-2. 비기능 요구사항

- 다수의 인스턴스로 동작해도 기능 문제 없도록 구현.
- 동시성 이슈 고려.
- 대기열 개념 적용.

---


## 2. 시퀀스 다이어그램

![sequencediagram](https://github.com/qdw0719/concert/assets/84309890/500310ca-a274-4eb4-88e7-aa3987029ced)

--- 

## 3. API 명세

### 3-1. Swagger

![api_users_charge](https://github.com/qdw0719/concert/assets/84309890/13373771-2056-4004-891c-6c931c2e257c)
![api_reservations](https://github.com/qdw0719/concert/assets/84309890/5a0d1a5f-f855-4429-b274-272fff221063)
![api_payments_create](https://github.com/qdw0719/concert/assets/84309890/1dc56265-83a8-4b80-abbf-d290a3d4ac60)
![api_concerts](https://github.com/qdw0719/concert/assets/84309890/6687a533-ff21-4b0a-ad09-386adbe127ae)
![api_concerts_{concertId}_details](https://github.com/qdw0719/concert/assets/84309890/cd4fa99c-18a7-4511-b494-9188149521cb)
![api_concerts_{concertId}_details_{detailId}_seats](https://github.com/qdw0719/concert/assets/84309890/a1f97bfc-3c3a-4b88-9693-94b44f2d5e40)

---

## 4. ERD

![Untitled (2)](https://github.com/qdw0719/concert/assets/84309890/8a527b1a-51c4-4b9e-9cdc-e3ab6ae4847d)

