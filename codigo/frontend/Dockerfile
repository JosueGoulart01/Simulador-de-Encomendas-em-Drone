# Estágio de build
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# Estágio de produção (standalone)
FROM node:20-alpine AS runner
WORKDIR /app
ENV NODE_ENV=production
ENV PORT=3000
ENV HOSTNAME="0.0.0.0"

# Copia o standalone gerado pelo Next.js
COPY --from=builder /app/.next/standalone ./
# Copia os arquivos estáticos
COPY --from=builder /app/.next/static ./.next/static
# Copia os arquivos públicos
COPY --from=builder /app/public ./public

EXPOSE ${PORT}
CMD ["node", "server.js"]