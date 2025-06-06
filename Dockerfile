# 1. 경량 Java 17 이미지를 베이스로 사용
FROM openjdk:17-jdk-slim

# 2. Docker 컨테이너 내에서 /tmp 디렉토리를 외부 마운트 가능하게 설정
VOLUME /tmp

# 3. 빌드 시 전달받을 JAR 파일 경로를 변수로 설정 (기본값: build/libs/*.jar)
ARG JAR_FILE=build/libs/*.jar

# 4. 지정된 JAR 파일을 컨테이너 내부에 app.jar라는 이름으로 복사
COPY ${JAR_FILE} app.jar

# 5. Spring Boot 실행 시 사용할 기본 프로파일을 prod로 설정
ENV SPRING_PROFILES_ACTIVE=prod

# 6. 컨테이너 시작 시 JAR 파일을 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]
