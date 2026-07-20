export type DroneStatus =
  | "IDLE"
  | "CARREGANDO"
  | "EM_VOO"
  | "ENTREGANDO"
  | "RETORNANDO"

export type Prioridade = "BAIXA" | "MEDIA" | "ALTA"

export type PedidoStatus = "FILA" | "ALOCADO" | "ENTREGUE" | "CANCELADO"

export interface Drone {
  id: number
  identificador: string
  capacidadeMaxKg: number
  autonomiaMaxKm: number
  bateriaAtual: number
  statusAtual: DroneStatus
  posXAtual: number
  posYAtual: number
}

export interface Pedido {
  id: number
  peso: number
  posXDestino: number
  posYDestino: number
  prioridade: Prioridade
  status: PedidoStatus
  dataCriacao: string
  droneId: number | null
}

export interface ZonaExclusao {
  id: number
  nome: string
  xMin: number
  yMin: number
  xMax: number
  yMax: number
}

export interface DashboardKPIs {
  totalEntregasRealizadas: number
  totalPedidosAguardando: number
  tempoMedioMinutosPorEntrega: number
  droneMaisEficiente: string
  dronesTotal: number
  dronesEmVoo: number
  dronesOciosos: number
  // Novos campos
  totalPedidos: number
  pedidosEmTransito: number
  bateriaMedia: number
  droneMenorBateria: string
  dronesCarregando: number
  dronesEntregando: number
}

export interface NovoDrone {
  identificador: string
  capacidadeMaxKg: number
  autonomiaMaxKm: number
}

export interface NovoPedido {
  peso: number
  posXDestino: number
  posYDestino: number
  prioridade: Prioridade
}

export interface NovaZona {
  nome: string
  xMin: number
  yMin: number
  xMax: number
  yMax: number
}

export interface ApiValidationError {
  timestamp: string
  status: number
  erro: string
  mensagem: string
  validacoes?: Record<string, string>
}

export const STATUS_META: Record<
  DroneStatus,
  { label: string; color: string; cssVar: string }
> = {
  IDLE: { label: "Ocioso", color: "#64748b", cssVar: "--status-idle" },
  CARREGANDO: { label: "Carregando", color: "#fbbf24", cssVar: "--status-carregando" },
  EM_VOO: { label: "Em voo", color: "#34d399", cssVar: "--status-em-voo" },
  ENTREGANDO: { label: "Entregando", color: "#22d3ee", cssVar: "--status-entregando" },
  RETORNANDO: { label: "Retornando", color: "#fb923c", cssVar: "--status-retornando" },
}