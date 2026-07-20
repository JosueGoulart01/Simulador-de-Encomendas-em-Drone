// Destino do back-end Spring Boot. Configurável via BACKEND_API_URL.
// Ex.: http://localhost:8080/api (padrão) ou a URL do servidor em produção.
const BACKEND_API_URL = process.env.BACKEND_API_URL ?? "http://localhost:8080/api"

/** @type {import('next').NextConfig} */
const nextConfig = {
  typescript: {
    ignoreBuildErrors: true,
  },
  images: {
    unoptimized: true,
  },
  output: 'standalone',
  // Proxy same-origin: o navegador chama /backend-api/* (mesma origem),
  // e o servidor Next.js repassa para o Spring Boot. Elimina o erro de CORS.
  async rewrites() {
    return [
      {
        source: "/backend-api/:path*",
        destination: `${BACKEND_API_URL}/:path*`,
      },
    ]
  },
}

export default nextConfig
