"use client"

import { Hexagon } from "lucide-react"

export function AppHeader({ online }: { online: boolean }) {
  return (
    <header className="sticky top-0 z-30 flex h-16 items-center justify-between border-b border-border bg-card/80 px-4 backdrop-blur-md md:px-6">
      {/* thin top accent line */}
      <span
        className="pointer-events-none absolute inset-x-0 top-0 h-px"
        style={{ background: "linear-gradient(90deg, transparent, var(--primary), transparent)" }}
        aria-hidden
      />

      <div className="flex items-center gap-3">
        <div className="relative flex size-9 items-center justify-center rounded-md bg-primary/10 glow-primary">
          <Hexagon className="size-5 text-primary" aria-hidden />
          <span className="absolute size-1.5 rounded-full bg-primary" aria-hidden />
        </div>
        <div className="leading-tight">
          <h1 className="font-display text-base font-bold tracking-tight text-foreground md:text-lg">
            AERO<span className="text-primary text-glow">GRID</span>
          </h1>
          <p className="hud-label hidden text-[10px] text-muted-foreground sm:block">
            Controle de Frota · Tempo Real
          </p>
        </div>
      </div>

      <div
        className="flex items-center gap-2 rounded-full border border-border bg-background/60 px-3 py-1.5"
        role="status"
        aria-live="polite"
      >
        <span className="relative flex size-2.5">
          {online && (
            <span className="absolute inline-flex size-full animate-ping rounded-full bg-primary opacity-75" />
          )}
          <span
            className="relative inline-flex size-2.5 rounded-full"
            style={{ background: online ? "var(--primary)" : "var(--destructive)" }}
          />
        </span>
        <span className="hud-label text-[10px] font-semibold text-foreground">
          {online ? "Sistema Online" : "Sistema Offline"}
        </span>
      </div>
    </header>
  )
}
