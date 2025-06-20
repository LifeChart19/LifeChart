# 1. 경량 Java 21 이미지를 베이스로 사용
FROM openjdk:21-jdk-slim

# 2. curl 설치
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# 3. 빌드 시 전달받을 JAR 파일 경로를 변수로 설정 (기본값: build/libs/app.jar)
ARG JAR_FILE=build/libs/app.jar

# 4. 지정된 JAR 파일을 컨테이너 내부 /app.jar 경로에 복사 (절대경로 권장)
COPY ${JAR_FILE} /app.jar

# 5. Spring Boot 실행 시 사용할 기본 프로파일을 외부에서 전달받도록 설정
ARG SPRING_PROFILES_ACTIVE=dev
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}

# 6. 컨테이너 시작 시 JAR 파일을 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]

# 7. CMD를 빈 배열로 지정해 ENTRYPOINT 실행 시 기본 인자를 없앰 (필요 시 덮어쓰기 가능)
CMD []
