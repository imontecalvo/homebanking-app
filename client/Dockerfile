FROM node:20.10.0

WORKDIR /client
RUN npm install -g serve
RUN mkdir dist
COPY ./dist ./dist

EXPOSE 3000

CMD serve -s dist