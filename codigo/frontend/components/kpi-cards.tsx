"use client"

import { PackageCheck, Clock, Hourglass, Trophy, Package, TrendingUp, Battery, AlertTriangle } from "lucide-react"
import { Card } from "@/components/ui/card"
import { useDashboard } from "@/hooks/use-live-data"

export function KpiCards() {
  const { kpis, isLoading } = useDashboard()

  // Agrupamento por temas
  const pedidos = [
    {
      label: "Entregas Realizadas",
      value: kpis ? String(kpis.totalEntregasRealizadas) : "—",
      icon: PackageCheck,
      tint: "var(--secondary)",
    },
    {
      label: "Pedidos Aguardando",
      value: kpis ? String(kpis.totalPedidosAguardando) : "—",
      icon: Hourglass,
      tint: "var(--accent)",
    },
    {
      label: "Em Trânsito",
      value: kpis ? String(kpis.pedidosEmTransito) : "—",
      icon: TrendingUp,
      tint: "#34d399",
    },
    {
      label: "Total de Pedidos",
      value: kpis ? String(kpis.totalPedidos) : "—",
      icon: Package,
      tint: "#818cf8",
    },
  ]

  const frota = [
    {
      label: "Drones Totais",
      value: kpis ? String(kpis.dronesTotal) : "—",
      icon: Trophy,
      tint: "var(--primary)",
    },
    {
      label: "Em Voo / Retornando",
      value: kpis ? String(kpis.dronesEmVoo) : "—",
      icon: TrendingUp,
      tint: "#34d399",
    },
    {
      label: "Ociosos",
      value: kpis ? String(kpis.dronesOciosos) : "—",
      icon: Clock,
      tint: "#64748b",
    },
    {
      label: "Carregando",
      value: kpis ? String(kpis.dronesCarregando) : "—",
      icon: Battery,
      tint: "#fbbf24",
    },
    {
      label: "Entregando",
      value: kpis ? String(kpis.dronesEntregando) : "—",
      icon: PackageCheck,
      tint: "#22d3ee",
    },
  ]

  const desempenho = [
    {
      label: "Tempo Médio / Entrega",
      value: kpis ? `${kpis.tempoMedioMinutosPorEntrega.toFixed(1)} min` : "—",
      icon: Clock,
      tint: "#fb923c",
    },
    {
      label: "Drone Mais Eficiente",
      value: kpis ? kpis.droneMaisEficiente : "—",
      icon: Trophy,
      tint: "var(--primary)",
    },
    {
      label: "Bateria Média",
      value: kpis ? `${kpis.bateriaMedia.toFixed(0)}%` : "—",
      icon: Battery,
      tint: "#22d3ee",
    },
    {
      label: "Menor Bateria",
      value: kpis ? kpis.droneMenorBateria : "—",
      icon: AlertTriangle,
      tint: "#ef4444",
    },
  ]

  const allItems = [...pedidos, ...frota, ...desempenho]

  return (
    <div className="grid grid-cols-2 gap-3 md:grid-cols-4 lg:grid-cols-4 xl:grid-cols-6">
      {allItems.map((item) => (
        <Card
          key={item.label}
          className="group relative flex flex-row items-center gap-3 overflow-hidden border-border bg-card/70 p-3 backdrop-blur-sm transition-all hover:border-[color:var(--primary)]/40"
        >
          <span
            className="absolute left-2 top-2 size-2 border-l border-t opacity-50"
            style={{ borderColor: item.tint }}
            aria-hidden
          />
          <span
            className="absolute bottom-2 right-2 size-2 border-b border-r opacity-50"
            style={{ borderColor: item.tint }}
            aria-hidden
          />
          <div
            className="flex size-10 shrink-0 items-center justify-center rounded-md"
            style={{
              background: `color-mix(in srgb, ${item.tint} 12%, transparent)`,
              boxShadow: `0 0 20px -8px ${item.tint}`,
            }}
          >
            <item.icon className="size-4" style={{ color: item.tint }} aria-hidden />
          </div>
          <div className="min-w-0">
            <p className="hud-label truncate text-[9px] text-muted-foreground">{item.label}</p>
            <p
              className={`truncate font-display text-base font-bold text-foreground tabular-nums transition-opacity ${
                isLoading && !kpis ? "opacity-40" : "opacity-100"
              }`}
            >
              {item.value}
            </p>
          </div>
        </Card>
      ))}
    </div>
  )
}