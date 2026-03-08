# 1단계: 빌드 스테이지 (Build Stage)
FROM gradle:8.5-jdk17-alpine AS build
WORKDIR /app

# 소스 코드를 복사 (캐시 효율을 위해 설정 파일부터 복사 가능)
COPY . .

# 실행 권한 부여 및 빌드 (테스트 제외로 속도 향상)
RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar

# 2단계: 실행 스테이지 (Run Stage)
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# 빌드 스테이지에서 생성된 jar 파일만 추출하여 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 컨테이너 실행
ENTRYPOINT ["java", "-jar", "app.jar"]