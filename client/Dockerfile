FROM node:20.10.0

WORKDIR /client
RUN npm install -g serve
RUN mkdir build
COPY ./build ./build

EXPOSE 3000

CMD serve -s build