# 멀티스테이지 빌드를 위한 Dockerfile
# Stage 1: 빌드 스테이지
FROM gradle:8.14.3-jdk21 AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 캐시 최적화를 위한 설정
ENV GRADLE_OPTS="-Dorg.gradle.daemon=false -Dorg.gradle.parallel=true -Dorg.gradle.caching=true"

# 의존성 파일들 복사 (캐시 최적화)
COPY build.gradle settings.gradle gradlew ./
COPY gradle/ gradle/

# 의존성 다운로드 (캐시 레이어)
RUN gradle dependencies --no-daemon

# 소스 코드 복사
COPY src/ src/

# 애플리케이션 빌드
RUN gradle build -x test --no-daemon

# Stage 2: 실행 스테이지
FROM eclipse-temurin:21-jre-alpine

# 작업 디렉토리 설정
WORKDIR /app

# 필요한 패키지 설치
RUN apk add --no-cache curl

# 애플리케이션 사용자 생성 (보안)
RUN addgroup -S appuser && adduser -S appuser -G appuser

# 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*-SNAPSHOT.jar app.jar

# 파일 소유권 변경
RUN chown -R appuser:appuser /app

# 사용자 전환
USER appuser

# 포트 노출
EXPOSE 8080

# 헬스체크 추가
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 애플리케이션 실행 (Railway 호환)
ENTRYPOINT ["java","-Xmx512m","-Xms256m","-XX:+UseG1GC","-XX:+UseContainerSupport","-XX:MaxRAMPercentage=75.0","-jar","app.jar"]
