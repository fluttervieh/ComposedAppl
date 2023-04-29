FROM node:latest
WORKDIR /usr/app
COPY package.json .
COPY . .
RUN npm install
ENTRYPOINT ["node", "server.js"]