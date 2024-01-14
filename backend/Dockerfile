FROM node:20.10.0

WORKDIR /backend
COPY package*.json .
RUN npm install

EXPOSE 3001

COPY . .
CMD npm start