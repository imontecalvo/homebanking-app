# Homebanking App

## Ejecución con Docker
### Creación de componentes y Ejecución
En caso de ser la primera vez que desees ejecutarlo y no tengas los componentes creados, podes crearlos y ejecutarlos a través del comando:
```
sudo docker compose up --build
```
Luego acceder a http://localhost:3000

### Ejecución
En caso de ya tener creados los componentes no es necesario realizar el `build` de nuevo, simplemente se puede ejecutar con el comando:
```
sudo docker compose up
```
Luego acceder a http://localhost:3000

## Ejecución sin Docker
En caso de querer ejecutar de forma local sin usar Docker, los pasos son los siguientes:
1. Clonar repositorio
2. Ejecutar `npm install` tanto en `/client` como en `/backend`
3. Modificar archivo `./backend/.env`, reemplazando la variable de entorno `DB_HOST="dbpostgres"` por `DB_HOST="localhost"` en caso de tener corriendo la base de datos en forma local.
4. Asegurarse de que la variable de entorno `DB_HOST_PORT` contenga el puerto en el que se está corriendo la base de datos local.
   (Nota: Por defecto corre en el puerto 5432 por lo que habría que reemplazar 6000 por dicho puerto).
6. Asegurarse de que el usuario y contraseña de la base de datos coincidan con los establecidos en `./backend/.env`
6. Asegurarse de tener una base de datos tal que coincida con el nombre establecido en `./backend/.env` (`homebanking`)
7. Correr los siguientes comandos
    ```
    #Correr backend, estando en ./backend
    npm start
    #En caso de querer correlo en modo dev con nodemon usar: npm run dev

    #Correr frontend, estando en ./client
    npm start 
    ```
8. Luego acceder a http://localhost:3000
    
## Información útil
- El servidor del frontend (servidor serve) corre en el puerto `3000` (en host y en el contenedor de docker)
- El servidor del backend corre en el puerto `3001` (en host y en el contenedor de docker)
- La base de datos postgres corre en el puerto `6000` (host) y en el `5432` (contenedor docker)

## Tecnologías y herramientas usadas
- Postgresql
- React
- Node.js + Express
- JWT (auth)
- Sequelize (ORM)
- Docker
- Serve (servidor de frontend buildeado)
