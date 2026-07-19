"use client"

import { useEffect, useRef, useState, useCallback } from "react"
import { Plus, Minus, LocateFixed, Radar, WifiOff } from "lucide-react"
import { Button } from "@/components/ui/button"
import { STATUS_META, type Drone, type Pedido, type ZonaExclusao } from "@/lib/types"

interface MapCanvasProps {
  drones: Drone[]
  zonas: ZonaExclusao[]
  pedidosAtivos: Pedido[]
  offline?: boolean
}

const MIN_SCALE = 8
const MAX_SCALE = 60
const DEFAULT_SCALE = 22

export function MapCanvas({ drones, zonas, pedidosAtivos, offline }: MapCanvasProps) {
  const canvasRef = useRef<HTMLCanvasElement>(null)
  const containerRef = useRef<HTMLDivElement>(null)
  const [scale, setScale] = useState(DEFAULT_SCALE)
  const [cursor, setCursor] = useState<{ x: number; y: number } | null>(null)

  // Refs para animação suave (interpolação entre ticks do polling).
  const dronesRef = useRef<Drone[]>(drones)
  const zonasRef = useRef<ZonaExclusao[]>(zonas)
  const pedidosRef = useRef<Pedido[]>(pedidosAtivos)
  const animPos = useRef<Map<number, { x: number; y: number; hx: number; hy: number }>>(new Map())
  const scaleRef = useRef(scale)
  const tRef = useRef(0)
  const mouseRef = useRef<{ px: number; py: number } | null>(null)

  useEffect(() => {
    dronesRef.current = drones
  }, [drones])
  useEffect(() => {
    zonasRef.current = zonas
  }, [zonas])
  useEffect(() => {
    pedidosRef.current = pedidosAtivos
  }, [pedidosAtivos])
  useEffect(() => {
    scaleRef.current = scale
  }, [scale])

  const centerBase = useCallback(() => setScale(DEFAULT_SCALE), [])

  useEffect(() => {
    const canvas = canvasRef.current
    const container = containerRef.current
    if (!canvas || !container) return
    const ctx = canvas.getContext("2d")
    if (!ctx) return

    let raf = 0
    let width = 0
    let height = 0

    const resize = () => {
      const dpr = window.devicePixelRatio || 1
      const rect = container.getBoundingClientRect()
      width = rect.width
      height = rect.height
      canvas.width = width * dpr
      canvas.height = height * dpr
      canvas.style.width = `${width}px`
      canvas.style.height = `${height}px`
      ctx.setTransform(dpr, 0, 0, dpr, 0, 0)
    }
    resize()
    const ro = new ResizeObserver(resize)
    ro.observe(container)

    const draw = () => {
      const s = scaleRef.current
      const cx = width / 2
      const cy = height / 2
      tRef.current += 1

      const wx = (x: number) => cx + x * s
      const wy = (y: number) => cy - y * s

      // ---- Fundo radial (mission control) ----
      const g = ctx.createRadialGradient(cx, cy, 0, cx, cy, Math.max(width, height) * 0.75)
      g.addColorStop(0, "#0b1626")
      g.addColorStop(1, "#060a12")
      ctx.fillStyle = g
      ctx.fillRect(0, 0, width, height)

      // ---- Grade a cada 1 unidade ----
      const unitsX = Math.ceil(cx / s) + 1
      const unitsY = Math.ceil(cy / s) + 1
      ctx.lineWidth = 1
      for (let i = -unitsX; i <= unitsX; i++) {
        const strong = i % 5 === 0
        ctx.strokeStyle = strong ? "rgba(120,190,220,0.13)" : "rgba(120,190,220,0.05)"
        ctx.beginPath()
        ctx.moveTo(Math.round(wx(i)) + 0.5, 0)
        ctx.lineTo(Math.round(wx(i)) + 0.5, height)
        ctx.stroke()
      }
      for (let j = -unitsY; j <= unitsY; j++) {
        const strong = j % 5 === 0
        ctx.strokeStyle = strong ? "rgba(120,190,220,0.13)" : "rgba(120,190,220,0.05)"
        ctx.beginPath()
        ctx.moveTo(0, Math.round(wy(j)) + 0.5)
        ctx.lineTo(width, Math.round(wy(j)) + 0.5)
        ctx.stroke()
      }

      // ---- Eixos ----
      ctx.strokeStyle = "rgba(34,211,238,0.5)"
      ctx.lineWidth = 1.5
      ctx.beginPath()
      ctx.moveTo(0, wy(0))
      ctx.lineTo(width, wy(0))
      ctx.moveTo(wx(0), 0)
      ctx.lineTo(wx(0), height)
      ctx.stroke()

      // ---- Rótulos dos eixos (a cada 5 unidades) ----
      ctx.fillStyle = "rgba(255,255,255,0.45)"
      ctx.font = "10px var(--font-sans, system-ui)"
      ctx.textAlign = "center"
      ctx.textBaseline = "middle"
      for (let i = -unitsX; i <= unitsX; i += 5) {
        if (i === 0) continue
        ctx.fillText(String(i), wx(i), wy(0) + 12)
      }
      for (let j = -unitsY; j <= unitsY; j += 5) {
        if (j === 0) continue
        ctx.fillText(String(j), wx(0) - 14, wy(j))
      }

      // ---- Zonas de exclusão ----
      for (const z of zonasRef.current) {
        const x = wx(z.xMin)
        const y = wy(z.yMax)
        const w = (z.xMax - z.xMin) * s
        const h = (z.yMax - z.yMin) * s
        ctx.fillStyle = "rgba(231,76,60,0.16)"
        ctx.fillRect(x, y, w, h)
        ctx.strokeStyle = "rgba(231,76,60,0.9)"
        ctx.lineWidth = 1.5
        ctx.setLineDash([6, 4])
        ctx.strokeRect(x, y, w, h)
        ctx.setLineDash([])
        // label pill
        const label = z.nome
        ctx.font = "600 11px var(--font-sans, system-ui)"
        const tw = ctx.measureText(label).width
        ctx.fillStyle = "rgba(231,76,60,0.92)"
        roundRect(ctx, x + 4, y + 4, tw + 12, 18, 4)
        ctx.fill()
        ctx.fillStyle = "#fff"
        ctx.textAlign = "left"
        ctx.textBaseline = "middle"
        ctx.fillText(label, x + 10, y + 4 + 9)
      }

      // ---- Destinos de pedidos ativos ----
      for (const p of pedidosRef.current) {
        const x = wx(p.posXDestino)
        const y = wy(p.posYDestino)
        const alocado = p.status === "ALOCADO"
        const col = alocado ? "#34d399" : "#fbbf24"
        // haste
        ctx.strokeStyle = col
        ctx.lineWidth = 2
        ctx.beginPath()
        ctx.moveTo(x, y)
        ctx.lineTo(x, y - 16)
        ctx.stroke()
        // balão
        ctx.fillStyle = col
        ctx.beginPath()
        ctx.arc(x, y - 18, 7, 0, Math.PI * 2)
        ctx.fill()
        ctx.fillStyle = "#0b1524"
        ctx.font = "700 9px var(--font-sans, system-ui)"
        ctx.textAlign = "center"
        ctx.textBaseline = "middle"
        ctx.fillText(String(p.id), x, y - 18)
        // ponto no destino
        ctx.fillStyle = col
        ctx.beginPath()
        ctx.arc(x, y, 2.5, 0, Math.PI * 2)
        ctx.fill()
      }

      // ---- Interpolação das posições dos drones ----
      const dronesNow = dronesRef.current
      const liveIds = new Set<number>()
      for (const d of dronesNow) {
        liveIds.add(d.id)
        const cur = animPos.current.get(d.id) ?? {
          x: d.posXAtual,
          y: d.posYAtual,
          hx: 0,
          hy: 1,
        }
        const nx = cur.x + (d.posXAtual - cur.x) * 0.12
        const ny = cur.y + (d.posYAtual - cur.y) * 0.12
        // vetor de direção (para orientar o ícone)
        const dx = nx - cur.x
        const dy = ny - cur.y
        if (Math.abs(dx) > 0.0005 || Math.abs(dy) > 0.0005) {
          cur.hx = cur.hx + (dx - cur.hx) * 0.2
          cur.hy = cur.hy + (dy - cur.hy) * 0.2
        }
        cur.x = nx
        cur.y = ny
        animPos.current.set(d.id, cur)
      }
      // limpar drones removidos
      for (const id of animPos.current.keys()) {
        if (!liveIds.has(id)) animPos.current.delete(id)
      }

      // ---- Rastros de voo (drone -> destino) ----
      for (const d of dronesNow) {
        if (!["EM_VOO", "ENTREGANDO", "CARREGANDO"].includes(d.statusAtual)) continue
        const pedido = pedidosRef.current.find((p) => p.droneId === d.id)
        if (!pedido) continue
        const pos = animPos.current.get(d.id)!
        ctx.strokeStyle = "rgba(34,211,238,0.45)"
        ctx.lineWidth = 1.5
        ctx.setLineDash([4, 6])
        ctx.lineDashOffset = -(tRef.current % 20)
        ctx.beginPath()
        ctx.moveTo(wx(pos.x), wy(pos.y))
        ctx.lineTo(wx(pedido.posXDestino), wy(pedido.posYDestino))
        ctx.stroke()
        ctx.setLineDash([])
        ctx.lineDashOffset = 0
      }

      // ---- Base central (0,0) com varredura de radar ----
      const bx = wx(0)
      const by = wy(0)
      // anéis pulsantes
      const pulse = (tRef.current % 90) / 90
      ctx.strokeStyle = `rgba(34,211,238,${0.35 * (1 - pulse)})`
      ctx.lineWidth = 2
      ctx.beginPath()
      ctx.arc(bx, by, 14 + pulse * 40, 0, Math.PI * 2)
      ctx.stroke()
      // sweep
      const sweep = (tRef.current * 0.02) % (Math.PI * 2)
      const sweepR = 3 * s
      const sg = ctx.createConicGradient(sweep, bx, by)
      sg.addColorStop(0, "rgba(34,211,238,0.28)")
      sg.addColorStop(0.08, "rgba(34,211,238,0)")
      sg.addColorStop(1, "rgba(34,211,238,0)")
      ctx.fillStyle = sg
      ctx.beginPath()
      ctx.moveTo(bx, by)
      ctx.arc(bx, by, sweepR, 0, Math.PI * 2)
      ctx.closePath()
      ctx.fill()
      // disco da base
      ctx.fillStyle = "#22d3ee"
      ctx.beginPath()
      ctx.arc(bx, by, 12, 0, Math.PI * 2)
      ctx.fill()
      ctx.strokeStyle = "rgba(255,255,255,0.85)"
      ctx.lineWidth = 2
      ctx.stroke()
      // ícone casa/heliporto (H)
      ctx.strokeStyle = "#fff"
      ctx.lineWidth = 2
      ctx.beginPath()
      ctx.moveTo(bx - 4, by - 4)
      ctx.lineTo(bx - 4, by + 4)
      ctx.moveTo(bx + 4, by - 4)
      ctx.lineTo(bx + 4, by + 4)
      ctx.moveTo(bx - 4, by)
      ctx.lineTo(bx + 4, by)
      ctx.stroke()
      // rótulo BASE
      ctx.fillStyle = "rgba(34,211,238,0.9)"
      ctx.font = "700 10px var(--font-sans, system-ui)"
      ctx.textAlign = "center"
      ctx.textBaseline = "top"
      ctx.fillText("BASE (0,0)", bx, by + 16)

      // ---- Drones ----
      for (const d of dronesNow) {
        const pos = animPos.current.get(d.id)!
        const x = wx(pos.x)
        const y = wy(pos.y)
        const color = STATUS_META[d.statusAtual].color
        const moving = d.statusAtual === "EM_VOO" || d.statusAtual === "RETORNANDO"

        // brilho / halo
        const glow = ctx.createRadialGradient(x, y, 0, x, y, 16)
        glow.addColorStop(0, hexAlpha(color, 0.5))
        glow.addColorStop(1, hexAlpha(color, 0))
        ctx.fillStyle = glow
        ctx.beginPath()
        ctx.arc(x, y, 16, 0, Math.PI * 2)
        ctx.fill()

        if (moving) {
          const r = 9 + ((tRef.current * 0.6) % 12)
          ctx.strokeStyle = hexAlpha(color, Math.max(0, 0.5 - r / 30))
          ctx.lineWidth = 2
          ctx.beginPath()
          ctx.arc(x, y, r, 0, Math.PI * 2)
          ctx.stroke()
        }

        // corpo do drone: quadricóptero simplificado orientado pela direção
        const ang = Math.atan2(-pos.hy, pos.hx) // tela: y invertido
        ctx.save()
        ctx.translate(x, y)
        ctx.rotate(moving ? ang : 0)
        // braços
        ctx.strokeStyle = "rgba(255,255,255,0.75)"
        ctx.lineWidth = 1.5
        const arm = 6
        ctx.beginPath()
        ctx.moveTo(-arm, -arm)
        ctx.lineTo(arm, arm)
        ctx.moveTo(arm, -arm)
        ctx.lineTo(-arm, arm)
        ctx.stroke()
        // rotores
        ctx.fillStyle = hexAlpha(color, 0.9)
        for (const [ox, oy] of [
          [-arm, -arm],
          [arm, -arm],
          [-arm, arm],
          [arm, arm],
        ]) {
          ctx.beginPath()
          ctx.arc(ox, oy, 2.4, 0, Math.PI * 2)
          ctx.fill()
        }
        // núcleo
        ctx.fillStyle = color
        ctx.strokeStyle = "rgba(255,255,255,0.95)"
        ctx.lineWidth = 2
        ctx.beginPath()
        ctx.arc(0, 0, 5, 0, Math.PI * 2)
        ctx.fill()
        ctx.stroke()
        ctx.restore()

        // etiqueta com nome
        ctx.font = "600 10px var(--font-sans, system-ui)"
        const tw = ctx.measureText(d.identificador).width
        ctx.fillStyle = "rgba(11,21,36,0.7)"
        roundRect(ctx, x - tw / 2 - 5, y - 26, tw + 10, 15, 4)
        ctx.fill()
        ctx.fillStyle = "rgba(255,255,255,0.95)"
        ctx.textAlign = "center"
        ctx.textBaseline = "middle"
        ctx.fillText(d.identificador, x, y - 26 + 8)
      }

      // ---- Leitura de coordenadas do cursor ----
      const m = mouseRef.current
      if (m) {
        const worldX = (m.px - cx) / s
        const worldY = (cy - m.py) / s
        setCursor({ x: worldX, y: worldY })
      }

      raf = requestAnimationFrame(draw)
    }

    raf = requestAnimationFrame(draw)
    return () => {
      cancelAnimationFrame(raf)
      ro.disconnect()
    }
  }, [])

  return (
    <div
      ref={containerRef}
      className="relative h-full w-full overflow-hidden rounded-lg border border-border"
      style={{ background: "#060a12", boxShadow: "inset 0 0 60px -20px rgba(34,211,238,0.25)" }}
      onMouseMove={(e) => {
        const rect = e.currentTarget.getBoundingClientRect()
        mouseRef.current = { px: e.clientX - rect.left, py: e.clientY - rect.top }
      }}
      onMouseLeave={() => {
        mouseRef.current = null
        setCursor(null)
      }}
    >
      <canvas ref={canvasRef} className="block h-full w-full" aria-label="Mapa cartesiano da frota de drones" />

      {/* Cabeçalho do painel */}
      <div className="pointer-events-none absolute left-4 top-4 flex items-center gap-2 rounded-md border border-[color:var(--primary)]/25 bg-background/50 px-3 py-1.5 backdrop-blur-sm">
        <Radar className="size-4 text-primary" aria-hidden />
        <span className="hud-label text-[10px] font-semibold text-foreground">Controle de Voo</span>
      </div>

      {/* Leitura de coordenadas */}
      <div className="pointer-events-none absolute right-4 top-4 rounded-md border border-border bg-background/50 px-3 py-1.5 font-mono text-xs tabular-nums text-primary backdrop-blur-sm">
        {cursor ? `X ${cursor.x.toFixed(1)}  Y ${cursor.y.toFixed(1)}` : "X —  Y —"}
      </div>

      {/* Aviso offline */}
      {offline && (
        <div className="absolute inset-0 flex items-center justify-center bg-black/45 backdrop-blur-sm">
          <div className="flex flex-col items-center gap-2 rounded-xl border border-border bg-card px-6 py-5 text-center shadow-lg">
            <WifiOff className="size-7 text-destructive" aria-hidden />
            <p className="text-sm font-semibold text-foreground">Sem conexão com o back-end</p>
            <p className="max-w-xs text-xs text-muted-foreground">
              Inicie o servidor Spring Boot e verifique a URL da API. O mapa atualiza automaticamente ao reconectar.
            </p>
          </div>
        </div>
      )}

      {/* Controles */}
      <div className="absolute bottom-4 right-4 flex flex-col gap-2">
        <Button
          size="icon"
          variant="secondary"
          onClick={() => setScale((s) => Math.min(MAX_SCALE, s + 4))}
          aria-label="Aproximar"
          className="size-9 shadow-lg"
        >
          <Plus className="size-4" />
        </Button>
        <Button
          size="icon"
          variant="secondary"
          onClick={() => setScale((s) => Math.max(MIN_SCALE, s - 4))}
          aria-label="Afastar"
          className="size-9 shadow-lg"
        >
          <Minus className="size-4" />
        </Button>
        <Button
          size="icon"
          variant="secondary"
          onClick={centerBase}
          aria-label="Centralizar na base"
          className="size-9 shadow-lg"
        >
          <LocateFixed className="size-4" />
        </Button>
      </div>

      {/* Legenda */}
      <div className="absolute bottom-4 left-4 flex flex-wrap gap-x-4 gap-y-1.5 rounded-md border border-border bg-background/50 px-3 py-2 backdrop-blur-sm">
        {Object.entries(STATUS_META).map(([key, meta]) => (
          <div key={key} className="flex items-center gap-1.5">
            <span
              className="size-2.5 rounded-full"
              style={{ background: meta.color, boxShadow: `0 0 8px ${meta.color}` }}
              aria-hidden
            />
            <span className="text-[10px] font-medium text-muted-foreground">{meta.label}</span>
          </div>
        ))}
      </div>
    </div>
  )
}

function roundRect(ctx: CanvasRenderingContext2D, x: number, y: number, w: number, h: number, r: number) {
  ctx.beginPath()
  ctx.moveTo(x + r, y)
  ctx.arcTo(x + w, y, x + w, y + h, r)
  ctx.arcTo(x + w, y + h, x, y + h, r)
  ctx.arcTo(x, y + h, x, y, r)
  ctx.arcTo(x, y, x + w, y, r)
  ctx.closePath()
}

function hexAlpha(hex: string, alpha: number): string {
  const h = hex.replace("#", "")
  const r = Number.parseInt(h.substring(0, 2), 16)
  const g = Number.parseInt(h.substring(2, 4), 16)
  const b = Number.parseInt(h.substring(4, 6), 16)
  return `rgba(${r},${g},${b},${alpha})`
}
