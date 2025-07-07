# Étape de build
FROM node:18

# Crée le dossier de travail
WORKDIR /app

# Installe les dépendances
COPY package*.json ./
RUN npm install

# Copie le reste
COPY . .

# Lancement via docker-compose avec `npm run dev -- --host`
