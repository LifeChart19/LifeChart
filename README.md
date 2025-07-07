# 1. Project Overview (프로젝트 개요)

- 프로젝트 이름: LifeChart
- 프로젝트 설명: 사용자의 자산 흐름과 목표 달성 가능성을 데이터로 시각화한 인생 목표 경로 설계 API 시스템

- 팀 19조

| 역할 | 팀장 | 팀원 | 팀원 | 팀원 | 팀원 | 팀원 |
|:-------------:|:-------------:|:-------------:|:-------------:|:-------------:|:-------------:|:-------------:|
|이름|정기백|이형진|오병택|고승표|석창훈|이혜원|
|GitHub|rlqor9688|le951|byeongtaek12|KSP0321|ChangHoonS|EZRANDO|

---

## API 명세서

[API 명세서 링크](http://3.37.215.135/swagger-ui/index.html#/auth-controller/refresh)

---

## 와이어 프레임

[와이어프레임 링크](https://www.figma.com/design/zTSk1flAYiXfST3GZB6niZ/LifeChart?node-id=342-11&p=f&t=pmxRvrUhetYTCGk8-0)

---

## ERD

![erd](https://github.com/user-attachments/assets/8a33f2e2-ebe7-4efb-9528-2cb9a10e4f3a)

---

# 2. Key Features (주요 기능)

<h3>은행 계좌 생성 및 연동</h3>

- **사용자 회원가입 시 은행 계좌 자동 생성**

<h3>목표</h3>

- **사용자는 원하는 카테고리를 선택해 목표를 생성**

<h3>공유 목표</h3>

- **사용자는 커뮤니티에서 목표를 조회하고 댓글·좋아요·팔로우 등을 통해 다른 사용자와 상호작용이 가능**

<h3>시뮬레이션</h3>

- **사용자가 생성한 목표를 기반으로 자산 설정(현재 자산, 연 이율, 저축액)을 요청하면, 시뮬레이션에서 목표에 맞는 계산이 자동으로 반영**

<h3>알림</h3>

- **사용자는 다양한 이벤트에 대한 알림을 실시간으로 수신하고, 조회·읽음 처리**

<h3>CI/CD & Monitoring</h3>

- **GitHub Actions로 Docker 기반 배포 자동화를 구성**
- **Prometheus와 Grafana로 서비스 상태를 실시간 모니터링**

---

# 3. Technology Stack (기술 스택)

### Language
![Java 21](https://img.shields.io/badge/Java_21-007396?style=flat-square&logo=openjdk&logoColor=white)

### Version Control

<img src="https://img.shields.io/badge/Spring-6DB33F?style=flat-square&logo=Spring&logoColor=white"/><img src="https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=GitHub&logoColor=white"/><img src="https://img.shields.io/badge/Postman-FF6C37?style=flat-square&logo=Postman&logoColor=white"/>

### Interface Description Language
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ_IDEA-000000?style=flat-square&logo=intellijidea&logoColor=white)

### Backend
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.5-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-59666C?style=flat-square&logo=hibernate)
![Spring Boot Web](https://img.shields.io/badge/Spring_Boot_Web-6DB33F?style=flat-square&logo=spring)
![Spring Validation](https://img.shields.io/badge/Spring_Validation-6DB33F?style=flat-square&logo=spring)
![Spring Cloud Gateway](https://img.shields.io/badge/Spring_Cloud_Gateway-6DB33F?style=flat-square&logo=spring)
![QueryDSL](https://img.shields.io/badge/QueryDSL-008000?style=flat-square)
![Lombok](https://img.shields.io/badge/Lombok-ED1C24?style=flat-square&logo=lombok)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=flat-square&logo=gradle)

### Monitoring
![Prometheus](https://img.shields.io/badge/Prometheus-E6522C?style=flat-square&logo=prometheus&logoColor=white)
![Grafana](https://img.shields.io/badge/Grafana-F46800?style=flat-square&logo=grafana&logoColor=white)

### Security
![JWT](https://img.shields.io/badge/JWT-000000?style=flat-square)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=flat-square&logo=springsecurity&logoColor=white)
![BCrypt](https://img.shields.io/badge/BCrypt-E6772E?style=flat-square)

### Deployment & Distribution
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=flat-square&logo=githubactions&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white)
![Docker Hub](https://img.shields.io/badge/Docker_Hub-2496ED?style=flat-square&logo=docker)
![AWS EC2](https://img.shields.io/badge/AWS_EC2-FF9900?style=flat-square&logo=amazonaws&logoColor=white)
![Ubuntu](https://img.shields.io/badge/Ubuntu-E95420?style=flat-square&logo=ubuntu)
![AWS IAM](https://img.shields.io/badge/AWS_IAM-232F3E?style=flat-square&logo=amazonaws)

### API Communication
![Spring Cloud OpenFeign](https://img.shields.io/badge/OpenFeign-007396?style=flat-square)
![OpenAPI](https://img.shields.io/badge/OpenAPI-6BA539?style=flat-square)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=flat-square&logo=swagger&logoColor=black)
![Postman](https://img.shields.io/badge/Postman-FF6C37?style=flat-square&logo=postman&logoColor=white)
![AWS SQS](https://img.shields.io/badge/AWS_SQS-FF4F8B?style=flat-square&logo=amazonaws)
![AWS SNS](https://img.shields.io/badge/AWS_SNS-FF9900?style=flat-square&logo=amazonaws)

### Database
![H2](https://img.shields.io/badge/H2-1A73E8?style=flat-square)
![JDBC](https://img.shields.io/badge/JDBC-007396?style=flat-square)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat-square&logo=redis&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white)

---
<br/>

# 4. Project Structure (프로젝트 구조)

```
├── lifechart
# ───────────── 어댑터 계층 (외부 입출력 인터페이스) ─────────────
adapter
├── in
├── out
# ───────────── 공통 유틸 / 설정 / 전역 예외 처리 ─────────────
common
├── config
├── entity
├── enums
├── exception
├── lock
├── port
├── response
├── util
# ───────────── 핵심 도메인 계층 ─────────────
domain
├── account
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── enums
│   ├── service
├── auth
│   ├── controller
│   ├── dto
│   ├── service
├── comment
│   ├── controller
│   ├── dto
│   │   ├── request
│   │   ├── response
│   ├── entity
│   ├── repository
│   ├── service
├── follow
│   ├── controller
│   ├── dto
│   │   ├── request
│   │   ├── response
│   ├── entity
│   ├── repository
│   ├── service
├── goal
│   ├── controller
│   ├── dto
│   │   ├── mapper
│   │   ├── request
│   │   ├── response
│   ├── entity
│   ├── enums
│   ├── event
│   ├── fetcher
│   ├── fixture
│   ├── helper
│   ├── repository
│   ├── scheduler
│   ├── service
├── like
│   ├── controller
│   ├── dto
│   │   ├── request
│   │   ├── response
│   ├── entity
│   ├── repository
│   ├── service
├── notification
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── port
│   ├── repository
│   ├── service
├── shareGoal
│   ├── controller
│   ├── dto
│   │   ├── reqeust
│   │   ├── response
│   ├── enums
│   ├── repository
│   ├── scheduler
│   ├── service
├── simulation
│   ├── config
│   ├── controller
│   ├── converter
│   ├── dto
│   │   ├── request
│   │   ├── response
│   ├── entity
│   ├── event
│   ├── listener
│   ├── logging
│   │   ├── dto
│   │   ├── entity
│   │   ├── enums
│   │   ├── repository
│   │   ├── service
│   ├── repository
│   ├── service
│   │   ├── calculator
│   │   ├── simulation
├── user
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── port
│   ├── repository
│   ├── scheduler
│   ├── service
# ───────────── 외부 연동 / 보안 / 검증 ─────────────
infra
├── client
│   ├── dto
security
validation
├── annotation
├── support
├── validator

```

<br/>

---

# 5. Development Workflow (개발 워크플로우)

## 브랜치 전략 (Branch Strategy)

- 코드 컨벤션: 네이버 코드 컨벤션을 기준으로 적용
- 커밋 & PR 컨벤션: 정해진 템플릿을 활용하여 작성
- 코드리뷰 & 머지 방식: PR은 단위 비즈니스 로직 및 테스트 코드 구현 후 생성