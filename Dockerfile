# Sử dụng Java 17
FROM eclipse-temurin:17-jdk-alpine

# Thư mục làm việc
WORKDIR /app

# Copy file jar sau khi build (Phải chạy mvn package trước)
COPY target/*.jar app.jar

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]

# Expose port (mặc định Spring Boot là 8080)
EXPOSE 8080
