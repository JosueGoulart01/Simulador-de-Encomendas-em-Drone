"use client"

import { Loader2, Inbox } from "lucide-react"
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { useFila } from "@/hooks/use-live-data"
import type { Prioridade } from "@/lib/types"

const PRIORIDADE_META: Record<Prioridade, { label: string; className: string }> = {
  ALTA: { label: "Alta", className: "bg-accent text-accent-foreground" },
  MEDIA: { label: "Média", className: "bg-chart-5 text-white" },
  BAIXA: { label: "Baixa", className: "bg-muted text-muted-foreground" },
}

function formatData(iso: string) {
  const d = new Date(iso)
  return d.toLocaleTimeString("pt-BR", { hour: "2-digit", minute: "2-digit", second: "2-digit" })
}

export function FilaTab() {
  const { fila, isLoading } = useFila()

  if (isLoading && fila.length === 0) {
    return (
      <div className="flex h-40 items-center justify-center text-muted-foreground">
        <Loader2 className="size-5 animate-spin" />
      </div>
    )
  }

  if (fila.length === 0) {
    return (
      <div className="flex h-40 flex-col items-center justify-center gap-2 text-center text-muted-foreground">
        <Inbox className="size-8 opacity-50" aria-hidden />
        <p className="text-sm">Nenhum pedido na fila.</p>
        <p className="text-xs">Novos pedidos aparecem aqui automaticamente.</p>
      </div>
    )
  }

  return (
    <div className="overflow-hidden rounded-lg border border-border">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead className="w-12">ID</TableHead>
            <TableHead>Peso</TableHead>
            <TableHead>Destino</TableHead>
            <TableHead>Prioridade</TableHead>
            <TableHead className="text-right">Criado</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {fila.map((p) => {
            const meta = PRIORIDADE_META[p.prioridade]
            return (
              <TableRow key={p.id}>
                <TableCell className="font-mono text-xs text-muted-foreground">#{p.id}</TableCell>
                <TableCell className="tabular-nums">{p.peso.toFixed(1)} kg</TableCell>
                <TableCell className="font-mono text-xs tabular-nums">
                  ({p.posXDestino}, {p.posYDestino})
                </TableCell>
                <TableCell>
                  <Badge className={meta.className}>{meta.label}</Badge>
                </TableCell>
                <TableCell className="text-right text-xs tabular-nums text-muted-foreground">
                  {formatData(p.dataCriacao)}
                </TableCell>
              </TableRow>
            )
          })}
        </TableBody>
      </Table>
    </div>
  )
}
