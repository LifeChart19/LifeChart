# 1. Project Overview (프로젝트 개요)

- 프로젝트 이름: LifeChart
- 프로젝트 설명: 사용자의 자산 흐름과 목표 달성 가능성을 데이터로 시각화한 인생 목표 경로 설계 API 시스템

- 팀 19조
  
| 역할 | 팀장 | 팀원 | 팀원 | 팀원 | 팀원 | 팀원 |
|:-------------:|:-------------:|:-------------:|:-------------:|:-------------:|:-------------:|:-------------:|
|이름|정기백|이형진|오병택|고승표|석창훈|이혜원|
|GitHub|rlqor9688|le951|byeongtaek12|KSP0321|ChangHoonS|EZRANDO|
## API 명세서

[API 명세서 링크](https://www.notion.so/teamsparta/API-2032dc3ef5148016a993e6b5b3d1688f)

## ERD

![erd](https://github.com/user-attachments/assets/8a33f2e2-ebe7-4efb-9528-2cb9a10e4f3a)



# 2. Key Features (주요 기능)

<h3>은행 계좌 생성 및 연동</h3>

- **사용자 회원가입 시 은행 계좌 자동 생성**


<h3>목표</h3>



<h3>공유 목표</h3>



<h3>시뮬레이션</h3>



<h3>알림</h3>



<h3>CI/CD & Monitoring</h3>



# 3. Technology Stack (기술 스택)

## Language

<img src="https://img.shields.io/badge/java-007396?style=flat-square&logo=java&logoColor=white"/><img src="https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=MySQL&logoColor=white"/>

## Version Control

<img src="https://img.shields.io/badge/Spring-6DB33F?style=flat-square&logo=Spring&logoColor=white"/><img src="https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=GitHub&logoColor=white"/><img src="https://img.shields.io/badge/Postman-FF6C37?style=flat-square&logo=Postman&logoColor=white"/>

## JDK Version

Java 17 (OpenJDK 17)


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

# 5. Development Workflow (개발 워크플로우)

## 브랜치 전략 (Branch Strategy)

- 공동 작업을 위해 `dev` 브랜치를 생성하여 기능 개발의 기준 브랜치(origin) 로 사용함
- 개인 작업은 `feature/기능명` 형식의 브랜치를 dev 브랜치에서 분리하여 개발
- 개발 완료시 pull request로 `dev` 브랜치에 병합

## 블록 구문

한 줄짜리 블록일 경우라도 {}를 생략하지 않고, 명확히 줄 바꿈 하여 사용

```
if (session != null) {
     session.invalidate();
}
```

<br/>
<br/>   
카멜 표기법을 이용하여 가독성을 향상

```
private final UserRepository userRepository;
private final PasswordEncoder passwordEncoder;

```

<br/>

## 메소드 네이밍 통일

메소드 작성 시 아래 네이밍 규칙을 준수하여 의미 전달을 명확하게 함<br/>

```
- 생성 create{필드명}  
- 조회 find{필드명}by{조회기준} 
- 페이지 적용시 findPage{필드명}By{조회기준} 
- 수정 update{필드명} 
- 삭제 delete{필드명}
```

## 예외처리

예외 발생시 커스텀 예외를 설정해 처리
```
public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
}
```

## 생성자 & Setter
데이터베이스 보호를 위해 생성자 사용 지양 <br/>
setter를 엔티티 내부에서 사용하므로써 보안 향상
```
public static Board of(CreateRequestDto requestDto, User user) {
        Board board = new Board(requestDto);
        board.initUser(user);
        return board;
    }
```
