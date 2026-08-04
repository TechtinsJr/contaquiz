#!/bin/bash

echo "🚀 Iniciando ambiente de desenvolvimento ContaQuiz..."

# Subir PostgreSQL
echo "📦 Subindo PostgreSQL..."
docker compose up -d postgres

# Aguardar banco estar pronto
echo "⏳ Aguardando PostgreSQL ficar pronto..."
until docker compose exec -T postgres pg_isready -U postgres > /dev/null 2>&1; do
  sleep 1
done

echo "✅ PostgreSQL está pronto!"
echo "🔧 Iniciando aplicação Quarkus..."

# Iniciar Quarkus em modo dev
./mvnw quarkus:dev
