"use client"

import { PackageCheck, Clock, Hourglass, Trophy } from "lucide-react"
import { Card } from "@/components/ui/card"
import { useDashboard } from "@/hooks/use-live-data"

export function KpiCards() {
  const { kpis, isLoading } = useDashboard()

  const items = [
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
      label: "Tempo Médio / Entrega",
      value: kpis ? `${kpis.tempoMedioMinutosPorEntrega.toFixed(1)} min` : "—",
      icon: Clock,
      tint: "var(--chart-5)",
    },
    {
      label: "Drone Mais Eficiente",
      value: kpis ? kpis.droneMaisEficiente : "—",
      icon: Trophy,
      tint: "var(--primary)",
    },
  ]

  return (
    <div className="grid grid-cols-2 gap-3 lg:grid-cols-4">
      {items.map((item) => (
        <Card
          key={item.label}
          className="group relative flex flex-row items-center gap-3 overflow-hidden border-border bg-card/70 p-4 backdrop-blur-sm transition-all hover:border-[color:var(--primary)]/40"
        >
          {/* corner tick marks (HUD detail) */}
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
            className="flex size-11 shrink-0 items-center justify-center rounded-md"
            style={{
              background: `color-mix(in srgb, ${item.tint} 12%, transparent)`,
              boxShadow: `0 0 20px -8px ${item.tint}`,
            }}
          >
            <item.icon className="size-5" style={{ color: item.tint }} aria-hidden />
          </div>
          <div className="min-w-0">
            <p className="hud-label truncate text-[10px] text-muted-foreground">{item.label}</p>
            <p
              className={`truncate font-display text-xl font-bold text-foreground tabular-nums transition-opacity ${
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
