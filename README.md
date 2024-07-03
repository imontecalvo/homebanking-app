# Homebanking App

## DEMO
https://github.com/imontecalvo/ejercicio-homebanking/assets/82344492/2e11e22d-f949-4721-a623-9f52f8651073

## Ejecución con Docker
🚧 PENDIENTE 🚧

## Ejecución sin Docker
En caso de querer ejecutar de forma local sin usar Docker, los pasos son los siguientes:
1. Clonar repositorio
2. Ejecutar `npm install` en`/client`
3. Correr frontend con los siguientes comandos
    ```
    #Correr frontend, estando en ./client, tenemos dos opciones
    #1. Para correr en modo dev
    npm run dev

    #2. Para servir el front buildeado
    npm run build #Una vez buildeado ya no hace falta volver a correrlo
    npm install -g serve
    serve -s dist
    ```
4. Configurar archivo `application.properties` en `/backend/src/main/resources/application.properties`, siguiendo el formato:
```
spring.application.name=backend
spring.datasource.url=jdbc:postgresql://localhost:5432/<DB_NAME>
spring.datasource.username=<DB_USER>
spring.datasource.password=<DB_PASSWORD>

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

security.jwt.key.private=<SECRET_KEY>
security.jwt.user.generator=<TOKEN_ISSUER_NAME>
``` 
5. Correr backend con comando:
```
mvn spring-boot:run
```
6. Luego acceder a http://localhost:5174 en caso de estar corriendo en modo dev o http://localhost:3000 en caso de estar corriendo `serve`.
Los puertos pueden variar en caso de una configuración distinta o si los puertos default están ocupados, de todas formas **se informan por la terminal**.
    
## Información útil
- El servidor del frontend (servidor serve) corre en el puerto `3000` (en host y en el contenedor de docker)
- El servidor del backend corre en el puerto `8080`
- La base de datos postgres corre en el puerto `5432`

## Tecnologías y herramientas usadas
- Postgresql
- React
- Java + Spring Boot + Hibernate JPA
- Spring Security + JWT (auth)
- Serve (servidor de frontend buildeado)
