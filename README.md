# product-service

### Requirements

**Develop application with RESTFul API for managing Products and Categories.**  
Database Schema:  
• Create schema to store products and categories. (DB can be chosen on your own)  
CRUD Operation:  
Client should perform CRUD operations on Products and Categories. It would be nice also to have ability to get full
category path for the products.  
TECHNOLOGY:
• Java or C++ as programming language  
• Optional: Spring Boot as a core framework for Java for a comparable framework for C++  
• Introduce Swagger documentation  
• Any other technologies/libraries can be chosen on your own  
IMPORTANT:  
• Please keep Code Quality Standards in mind, while working on your task. • Your approach toward the problem is also
important for us.  
• There will be provided also data set for Products and Categories to fill DB. • Please push the code to GitHub and
share the link with us.

### How to run
Build code and create docker image:
```
./gradlew clean build && docker image build -t product-service .
```
Run docker compose file:
```
docker-compose up -d
```
Then you can check out API via [SWAGGER]  
(http://localhost:8080/swagger-ui/index.html)
Stop docker compose
```
docker-compose down
```

