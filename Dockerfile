# 1. JDK 이미지 선택 (Java 21)
FROM eclipse-temurin:21-jdk-alpine

# 2. 작업 디렉토리
WORKDIR /app

# 3. 빌드된 jar 복사
COPY build/libs/*.jar app.jar

# 4. 프로파일
ENV SPRING_PROFILES_ACTIVE=default

# 5. 앱 실행
ENTRYPOINT ["java", "-jar", "app.jar"]