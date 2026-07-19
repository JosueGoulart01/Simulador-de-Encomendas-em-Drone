"use client"

import { Loader2, BatteryMedium } from "lucide-react"
import { Card } from "@/components/ui/card"
import { useDrones } from "@/hooks/use-live-data"
import { STATUS_META } from "@/lib/types"

export function FrotaTab() {
  const { drones, isLoading } = useDrones()

  if (isLoading && drones.length === 0) {
    return (
      <div className="flex h-40 items-center justify-center text-muted-foreground">
        <Loader2 className="size-5 animate-spin" />
      </div>
    )
  }

  return (
    <div className="flex flex-col gap-3">
      {drones.map((d) => {
        const meta = STATUS_META[d.statusAtual]
        const bateriaColor =
          d.bateriaAtual > 50 ? "var(--secondary)" : d.bateriaAtual > 20 ? "var(--accent)" : "var(--destructive)"
        return (
          <Card key={d.id} className="p-3">
            <div className="flex items-center justify-between gap-2">
              <div className="flex items-center gap-2">
                <span
                  className="size-2.5 shrink-0 rounded-full"
                  style={{ background: meta.color }}
                  aria-hidden
                />
                <span className="text-sm font-semibold text-foreground">{d.identificador}</span>
              </div>
              <span
                className="rounded-full px-2 py-0.5 text-[10px] font-semibold"
                style={{
                  background: `color-mix(in srgb, ${meta.color} 18%, transparent)`,
                  color: meta.color,
                }}
              >
                {meta.label}
              </span>
            </div>

            <div className="mt-3 grid grid-cols-2 gap-x-4 gap-y-2 text-xs">
              <div>
                <p className="text-muted-foreground">Posição</p>
                <p className="font-mono tabular-nums text-foreground">
                  ({d.posXAtual.toFixed(1)}, {d.posYAtual.toFixed(1)})
                </p>
              </div>
              <div>
                <p className="text-muted-foreground">Capacidade</p>
                <p className="tabular-nums text-foreground">{d.capacidadeMaxKg.toFixed(1)} kg</p>
              </div>
              <div className="col-span-2">
                <div className="mb-1 flex items-center justify-between">
                  <span className="flex items-center gap-1 text-muted-foreground">
                    <BatteryMedium className="size-3.5" aria-hidden /> Bateria
                  </span>
                  <span className="tabular-nums text-foreground">{d.bateriaAtual.toFixed(0)}%</span>
                </div>
                <div className="h-1.5 w-full overflow-hidden rounded-full bg-muted">
                  <div
                    className="h-full rounded-full transition-all duration-500"
                    style={{ width: `${d.bateriaAtual}%`, background: bateriaColor }}
                  />
                </div>
              </div>
            </div>
          </Card>
        )
      })}
    </div>
  )
}
