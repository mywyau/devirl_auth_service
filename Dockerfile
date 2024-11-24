## Use sbtscala/scala-sbt image to build the project
#FROM sbtscala/scala-sbt:eclipse-temurin-focal-11.0.22_7_1.9.9_3.4.0 AS builder
#
## Set the working directory
#WORKDIR /app
#
## Copy the entire project
#COPY . .
#
## Build the fat JAR
#RUN sbt clean assembly
#
## Use a minimal Java runtime for the final image
#FROM eclipse-temurin:11-jre-focal
#
## Set the working directory
#WORKDIR /app
#
## Copy the fat JAR from the builder stage
#COPY --from=builder /app/target/scala-3.3.0/app-assembly-0.1.0.jar /app/app.jar
#
## Expose the application port
#EXPOSE 8080
#
## Run the application
#CMD ["java", "-jar", "/app/app.jar"]


# Stage 1: Build the fat JAR
FROM sbtscala/scala-sbt:eclipse-temurin-focal-11.0.22_7_1.9.9_3.4.0 AS builder

# Set the working directory
WORKDIR /app

# Copy the project definition files first to leverage Docker layer caching
COPY project/ project/
COPY build.sbt .

# Fetch dependencies and compile
RUN sbt update compile

# Copy the rest of the application source files
COPY . .

# Build the fat JAR
RUN sbt assembly

# Stage 2: Use a minimal runtime container
FROM eclipse-temurin:11-jre-focal

# Set the working directory
WORKDIR /app

# Copy the fat JAR from the builder stage
COPY --from=builder /app/target/scala-3.3.0/pistachio-assembly-0.1.0-SNAPSHOT.jar /app/app.jar

# Expose the application port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "/app/app.jar"]
