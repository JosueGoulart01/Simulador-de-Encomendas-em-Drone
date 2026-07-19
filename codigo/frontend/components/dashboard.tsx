"use client"

import { AppHeader } from "@/components/app-header"
import { KpiCards } from "@/components/kpi-cards"
import { MapCanvas } from "@/components/map-canvas"
import { SidebarPanel } from "@/components/sidebar-panel"
import { useDrones, useZonas, usePedidosAtivos } from "@/hooks/use-live-data"

export function Dashboard() {
  const { drones, online } = useDrones()
  const { zonas } = useZonas()
  const { pedidos } = usePedidosAtivos()

  return (
    <div className="relative flex h-dvh flex-col bg-background">
      {/* Ambient grid + radial glow backdrop */}
      <div className="pointer-events-none absolute inset-0 bg-grid opacity-40" aria-hidden />
      <div
        className="pointer-events-none absolute inset-0"
        aria-hidden
        style={{
          background:
            "radial-gradient(120% 80% at 50% -10%, color-mix(in srgb, var(--primary) 10%, transparent), transparent 60%)",
        }}
      />

      <div className="relative z-10 flex h-full flex-col">
        <AppHeader online={online} />

        <main className="flex flex-1 flex-col gap-4 overflow-hidden p-4 lg:flex-row">
          {/* Área principal: KPIs + Mapa */}
          <section className="flex min-h-0 flex-1 flex-col gap-4 lg:min-w-[60%]">
            <KpiCards />
            <div className="min-h-[420px] flex-1">
              <MapCanvas drones={drones} zonas={zonas} pedidosAtivos={pedidos} offline={!online} />
            </div>
          </section>

          {/* Barra lateral direita */}
          <aside className="flex w-full min-h-0 flex-col rounded-lg border border-border bg-card/70 p-4 backdrop-blur-sm lg:w-[380px] lg:shrink-0">
            <SidebarPanel />
          </aside>
        </main>

        <footer className="flex items-center justify-between border-t border-border px-4 py-2 text-[10px] text-muted-foreground">
          <span className="hud-label">AeroGrid Control · v1.0</span>
          <span className="hidden items-center gap-1.5 sm:flex">
            <span className="size-1.5 animate-pulse rounded-full bg-primary" aria-hidden />
            Sincronização automática · 2s
          </span>
        </footer>
      </div>
    </div>
  )
}
