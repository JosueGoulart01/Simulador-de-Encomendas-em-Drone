import type {
  Drone,
  Pedido,
  ZonaExclusao,
  DashboardKPIs,
  NovoDrone,
  NovoPedido,
  NovaZona,
  ApiValidationError,
} from "./types"

/**
 * Camada de integração com a API REST (Spring Boot).
 *
 * Por padrão as chamadas passam pelo proxy same-origin `/backend-api`
 * (configurado em next.config.mjs -> rewrites), o que evita erros de CORS:
 * o navegador chama a mesma origem e o servidor Next.js repassa para o
 * Spring Boot (BACKEND_API_URL, padrão http://localhost:8080/api).
 *
 * Para chamar o back-end diretamente (sem proxy), defina
 * NEXT_PUBLIC_API_BASE_URL — nesse caso o servidor precisa habilitar CORS.
 *
 * Todas as chamadas batem no back-end real — não há dados mockados.
 */
export const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ?? "/backend-api"

export function isApiValidationError(e: unknown): e is ApiValidationError {
  return typeof e === "object" && e !== null && "erro" in e && "status" in e
}

/** Erro lançado quando não há comunicação com o servidor (offline/CORS/rede). */
export class NetworkError extends Error {
  constructor(message = "Sem conexão com o servidor") {
    super(message)
    this.name = "NetworkError"
  }
}

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  let res: Response
  try {
    res = await fetch(`${API_BASE_URL}${path}`, {
      headers: { "Content-Type": "application/json", Accept: "application/json" },
      cache: "no-store",
      ...options,
    })
  } catch {
    // Falha de rede (servidor fora do ar, CORS, DNS, etc.)
    throw new NetworkError()
  }

  const isJson = res.headers.get("content-type")?.includes("application/json")
  const data = res.status === 204 ? null : isJson ? await res.json().catch(() => null) : null

  if (!res.ok) {
    if (data && isApiValidationError(data)) throw data
    const fallback: ApiValidationError = {
      timestamp: new Date().toISOString(),
      status: res.status,
      erro: res.status >= 500 ? "Erro interno do servidor" : "Erro na requisição",
      mensagem:
        res.status >= 500
          ? "Ocorreu um erro interno no servidor. Tente novamente."
          : `A requisição falhou (HTTP ${res.status}).`,
    }
    throw fallback
  }
  return data as T
}

export const api = {
  // ---- GET (polling / carregamento) ----
  getDrones: () => request<Drone[]>("/drones"),
  getFila: () => request<Pedido[]>("/pedidos/fila"),
  getPedidosAtivos: () => request<Pedido[]>("/pedidos/fila"),
  getDashboard: () => request<DashboardKPIs>("/simulacao/dashboard"),
  getZonas: () => request<ZonaExclusao[]>("/zonas-exclusao"),

  // ---- POST (cadastros) ----
  createDrone: (body: NovoDrone) =>
    request<Drone>("/drones", { method: "POST", body: JSON.stringify(body) }),
  createPedido: (body: NovoPedido) =>
    request<Pedido>("/pedidos", { method: "POST", body: JSON.stringify(body) }),
  createZona: (body: NovaZona) =>
    request<ZonaExclusao>("/zonas-exclusao", { method: "POST", body: JSON.stringify(body) }),

  // ---- DELETE ----
  deleteZona: (id: number) =>
    request<null>(`/zonas-exclusao/${id}`, { method: "DELETE" }),
}
