FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Cache Maven dependencies in their own layer before copying source,
# so dependency downloads are skipped on rebuilds that only touch code.
COPY backend/mvnw backend/mvnw.cmd ./backend/
COPY backend/.mvn ./backend/.mvn
COPY backend/pom.xml ./backend/
RUN cd backend && chmod +x mvnw && ./mvnw -q dependency:go-offline || true

# frontend-maven-plugin builds the React app from the repo root (this
# WORKDIR), so the full context — frontend source and backend/ — needs
# to be present before packaging.
COPY . .
# -DskipTests: Spring Initializr's default placeholder test
# (BackendApplicationTests.contextLoads) boots the full application
# context, which needs a live datasource. That's not available at
# Docker build time — Render (and most PaaS Docker builds) only inject
# SPRING_DATASOURCE_URL at container *runtime* — so `mvn package`
# without this flag fails the build every time, even though nothing is
# actually broken. If the project later grows a real test suite that
# needs a database, give it an in-memory/Testcontainers datasource
# instead of removing this flag blindly.
RUN cd backend && chmod +x mvnw && ./mvnw clean package -q -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/backend/target/backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
# Render's free instances only have 512MB RAM. Cap the heap as a share of
# whatever memory the container actually has (auto-detected via cgroups)
# instead of letting the JVM's un-tuned defaults risk an OOM kill.
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
