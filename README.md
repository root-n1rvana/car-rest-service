# Car Rest Service

### Task 4.1 Planning: Car Database

**_Assignment:_**

 * Analyze and decompose Car DB Reset service  (create UML class diagram for application) based on attached `file.csv` data.


### Task 4.2 Create RestApi endpoints

**_Assignment:_**

1. Create new Spring Boot project using [Initializer](https://start.spring.io/) with dependencies:
   - **Spring Web** (Build web, including RESTful, applications using Spring MVC. Uses Apache Tomcat as the default embedded container.)
   - **Spring Data JPA** (Persist data in SQL stores with Java Persistence API using Spring Data and Hibernate.)
   - **Flyway Migration** (Version control for your database, so you can migrate from any version (incl. an empty database) to the latest version of the schema.)
   - **H2 Database** or **PostgreSQL** Driver of your choice.
2. Create model and schema initalizing sql migration script according with your UML diagrama
3. Create JPA repositories and service layer with base CRUD operations
4. Following best practices on RestAPI design - implement required endpoints to manage API model
    - Implement create/update/list/delete operations for provided data
      - manufacturers
      - manufacturers/model
      - manufacturers/model/year

        ex: `POST /api/v1/manufacturers/toyota/models/corolla/2001`
    - Implement search endpoint with parameters like `manufacturer` ,'model', 'minYear', 'maxYear, 'category'
      
      ex: `GET /api/v1/cars?manufacturer=mercedes&minYear=2005`
    - all list endpoints should support pagination and sorting
5. Cover controllers with tests
6. Add additional components tests if required





### Guides

The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)

