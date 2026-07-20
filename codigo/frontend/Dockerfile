# Estágio 1: Instalação de dependências e Build do Next.js
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
ENV NEXT_PUBLIC_API_BASE_URL=http://localhost:8080/api
RUN npm run build

# Estágio 2: Ambiente de execução de produção
FROM node:20-alpine AS runner
WORKDIR /app
ENV NODE_ENV production

# Copia os arquivos gerados e dependências essenciais
COPY --from=builder /app/package*.json ./
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/public ./public

EXPOSE 3000
ENV PORT 3000

CMD ["npm", "run", "start"]