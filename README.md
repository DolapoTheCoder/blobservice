
# Blob Service

A Kotlin-based service for detecting inconsistencies in blob reference counts across multiple databases using Spring Boot and JDBC. This service queries two MySQL databases to ensure the integrity of blob references.

## Prerequisites

Before you begin, ensure you have met the following requirements:

- **Java Development Kit (JDK)**: Ensure you have JDK 11 or higher installed. You can download it from [AdoptOpenJDK](https://adoptopenjdk.net/) or [Oracle's official site](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html).
- **Gradle**: You need Gradle installed. You can install it by following the instructions on the [Gradle website](https://gradle.org/install/).
- **MySQL Server**: Have your MySQL server installed and running. You can download it from [MySQL's official site](https://dev.mysql.com/downloads/mysql/).

## Getting Started

1. **Clone the Repository**:

   Open your terminal and run:

   ```bash
   git clone https://github.com/DolapoTheCoder/blobservice.git
   cd blobservice
   ```

2. **Set Up Your Databases**:

   Create two MySQL databases: `ProtonMailGlobal` and `ProtonMailShard`. Use the SQL files provided in the `sql` directory to set up your database structure and seed data.

   You can import the SQL files using the MySQL command line:

   ```bash
   mysql -u <username> -p ProtonMailGlobal < path/to/ProtonMailGlobal.sql
   mysql -u <username> -p ProtonMailShard < path/to/ProtonMailShard.sql
   ```

   Replace `<username>` with your MySQL username and provide the correct path to the SQL files.

3. **Configure Database Properties**:

   Create `src/main/resources/application.properties` and configure your database connection properties for both databases:

   ```properties
   spring.datasource.global.jdbc-url=jdbc:mysql://localhost:3306/ProtonMailGlobal
   spring.datasource.global.username=${YOUR_USERNAME}
   spring.datasource.global.password=${YOUR_PASSWORD}
   spring.datasource.global.driver-class-name = com.mysql.cj.jdbc.Driver
   
   spring.datasource.shard.jdbc-url=jdbc:mysql://localhost:3306/ProtonMailShard
    spring.datasource.shard.username=${YOUR_USERNAME}
    spring.datasource.shard.password=${YOUR_PASSWORD}
    spring.datasource.shard.driver-class-name=com.mysql.cj.jdbc.Driver
   ```

   Replace `${YOUR_USERNAME}` and `${YOUR_PASSWORD}` with your actual MySQL credentials.

4. **Build the Project**:

   Use Gradle to build the project:

   ```bash
   ./gradlew build
   ```

   If you are on Windows, use:

   ```bash
   gradlew build
   ```

5. **Run the Service**:

   After building the project, run the service:

   ```bash
   ./gradlew bootRun
   ```

   If you encounter any issues, ensure that your databases are running and accessible.

## Usage

Once the service is running, it will automatically query the blob references from both databases. You can modify the logic in the `BlobService` class to trigger specific queries or handle specific operations.

## Testing the Service

You can test the service using a REST client like Postman or Curl. Add necessary endpoints in the `BlobController` class to expose functionality.

Use this in postman to check: [localhost:8080/check](localhost:8080/check)


## Contributing

If you want to contribute to this project, please follow these steps:

1. Fork the repository.
2. Create a new branch (`git checkout -b feature-branch`).
3. Make your changes and commit them (`git commit -m 'Add some feature'`).
4. Push to the branch (`git push origin feature-branch`).
5. Open a pull request.

## License

This project is licensed under the MIT License.

## Acknowledgements

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [MySQL Documentation](https://dev.mysql.com/doc/)
