"use client"

import { useState } from "react"
import { useSWRConfig } from "swr"
import { toast } from "sonner"
import { Loader2, PlaneTakeoff, Package, ShieldAlert, Trash2 } from "lucide-react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import { api, isApiValidationError, NetworkError } from "@/lib/api"
import type { Prioridade } from "@/lib/types"
import { useZonas } from "@/hooks/use-live-data"

type Errors = Record<string, string>

function FieldError({ msg }: { msg?: string }) {
  if (!msg) return null
  return (
    <p className="text-xs font-medium text-destructive" role="alert">
      {msg}
    </p>
  )
}

export function CadastrosTab() {
  const { mutate } = useSWRConfig()
  const { refreshZonas } = useZonas()

  return (
    <div className="flex flex-col gap-4">
      <DroneForm
        onCreated={() => {
          mutate("drones")
          mutate("dashboard")
        }}
      />
      <PedidoForm
        onCreated={() => {
          mutate("fila")
          mutate("pedidos-ativos")
          mutate("dashboard")
        }}
      />
      <ZonaForm onCreated={() => refreshZonas()} />
    </div>
  )
}

function handleError(e: unknown, setErrors: (e: Errors) => void) {
  if (e instanceof NetworkError) {
    toast.error("Sem conexão com o servidor", {
      description: "Verifique se o back-end está rodando em " + (process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080/api"),
    })
  } else if (isApiValidationError(e)) {
    setErrors(e.validacoes ?? {})
    const first = e.validacoes ? Object.values(e.validacoes)[0] : e.mensagem
    toast.error(e.erro, { description: first })
  } else {
    toast.error("Erro inesperado", {
      description: "Não foi possível concluir a operação. Tente novamente.",
    })
  }
}

function DroneForm({ onCreated }: { onCreated: () => void }) {
  const [identificador, setIdentificador] = useState("")
  const [capacidade, setCapacidade] = useState("")
  const [autonomia, setAutonomia] = useState("")
  const [errors, setErrors] = useState<Errors>({})
  const [loading, setLoading] = useState(false)

  async function submit(e: React.FormEvent) {
    e.preventDefault()
    setErrors({})
    setLoading(true)
    try {
      const drone = await api.createDrone({
        identificador,
        capacidadeMaxKg: Number.parseFloat(capacidade),
        autonomiaMaxKm: Number.parseFloat(autonomia),
      })
      toast.success("Drone cadastrado", { description: `${drone.identificador} adicionado à frota.` })
      setIdentificador("")
      setCapacidade("")
      setAutonomia("")
      onCreated()
    } catch (err) {
      handleError(err, setErrors)
    } finally {
      setLoading(false)
    }
  }

  return (
    <Card>
      <CardHeader className="flex flex-row items-center gap-2 space-y-0 pb-3">
        <PlaneTakeoff className="size-4 text-primary" aria-hidden />
        <CardTitle className="font-display text-sm">Cadastrar Drone</CardTitle>
      </CardHeader>
      <CardContent>
        <form onSubmit={submit} className="flex flex-col gap-3" noValidate>
          <div className="flex flex-col gap-1.5">
            <Label htmlFor="d-id">Identificador</Label>
            <Input
              id="d-id"
              value={identificador}
              onChange={(e) => setIdentificador(e.target.value)}
              placeholder="Drone-Alpha-05"
            />
            <FieldError msg={errors.identificador} />
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="d-cap">Capacidade (kg)</Label>
              <Input
                id="d-cap"
                type="number"
                step="0.1"
                value={capacidade}
                onChange={(e) => setCapacidade(e.target.value)}
                placeholder="10.0"
              />
              <FieldError msg={errors.capacidadeMaxKg} />
            </div>
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="d-aut">Autonomia (km)</Label>
              <Input
                id="d-aut"
                type="number"
                step="0.1"
                value={autonomia}
                onChange={(e) => setAutonomia(e.target.value)}
                placeholder="50.0"
              />
              <FieldError msg={errors.autonomiaMaxKm} />
            </div>
          </div>
          <Button type="submit" disabled={loading} className="mt-1">
            {loading && <Loader2 className="size-4 animate-spin" />}
            Cadastrar Drone
          </Button>
        </form>
      </CardContent>
    </Card>
  )
}

function PedidoForm({ onCreated }: { onCreated: () => void }) {
  const [peso, setPeso] = useState("")
  const [x, setX] = useState("")
  const [y, setY] = useState("")
  const [prioridade, setPrioridade] = useState<Prioridade>("MEDIA")
  const [errors, setErrors] = useState<Errors>({})
  const [loading, setLoading] = useState(false)

  async function submit(e: React.FormEvent) {
    e.preventDefault()
    setErrors({})
    setLoading(true)
    try {
      const pedido = await api.createPedido({
        peso: Number.parseFloat(peso),
        posXDestino: Number.parseFloat(x),
        posYDestino: Number.parseFloat(y),
        prioridade,
      })
      toast.success("Pedido adicionado", {
        description: `Pedido #${pedido.id} enviado para a fila.`,
      })
      setPeso("")
      setX("")
      setY("")
      setPrioridade("MEDIA")
      onCreated()
    } catch (err) {
      handleError(err, setErrors)
    } finally {
      setLoading(false)
    }
  }

  return (
    <Card>
      <CardHeader className="flex flex-row items-center gap-2 space-y-0 pb-3">
        <Package className="size-4 text-primary" aria-hidden />
        <CardTitle className="font-display text-sm">Adicionar Pedido</CardTitle>
      </CardHeader>
      <CardContent>
        <form onSubmit={submit} className="flex flex-col gap-3" noValidate>
          <div className="flex flex-col gap-1.5">
            <Label htmlFor="p-peso">Peso (kg)</Label>
            <Input
              id="p-peso"
              type="number"
              step="0.1"
              value={peso}
              onChange={(e) => setPeso(e.target.value)}
              placeholder="4.5"
            />
            <FieldError msg={errors.peso} />
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="p-x">Coord. X destino</Label>
              <Input
                id="p-x"
                type="number"
                step="0.1"
                value={x}
                onChange={(e) => setX(e.target.value)}
                placeholder="10.0"
              />
              <FieldError msg={errors.posXDestino} />
            </div>
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="p-y">Coord. Y destino</Label>
              <Input
                id="p-y"
                type="number"
                step="0.1"
                value={y}
                onChange={(e) => setY(e.target.value)}
                placeholder="15.0"
              />
              <FieldError msg={errors.posYDestino} />
            </div>
          </div>
          <div className="flex flex-col gap-1.5">
            <Label htmlFor="p-prio">Prioridade</Label>
            <Select value={prioridade} onValueChange={(v) => setPrioridade(v as Prioridade)}>
              <SelectTrigger id="p-prio">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="BAIXA">Baixa</SelectItem>
                <SelectItem value="MEDIA">Média</SelectItem>
                <SelectItem value="ALTA">Alta</SelectItem>
              </SelectContent>
            </Select>
            <FieldError msg={errors.prioridade} />
          </div>
          <Button type="submit" disabled={loading} className="mt-1">
            {loading && <Loader2 className="size-4 animate-spin" />}
            Adicionar Pedido
          </Button>
        </form>
      </CardContent>
    </Card>
  )
}

function ZonaForm({ onCreated }: { onCreated: () => void }) {
  const { zonas, refreshZonas } = useZonas()
  const [nome, setNome] = useState("")
  const [xMin, setXMin] = useState("")
  const [yMin, setYMin] = useState("")
  const [xMax, setXMax] = useState("")
  const [yMax, setYMax] = useState("")
  const [errors, setErrors] = useState<Errors>({})
  const [loading, setLoading] = useState(false)
  const [deletingId, setDeletingId] = useState<number | null>(null)

  async function remover(id: number) {
    setDeletingId(id)
    try {
      await api.deleteZona(id)
      toast.success("Zona removida", { description: `Zona #${id} excluída do mapa.` })
      refreshZonas()
    } catch (err) {
      handleError(err, () => {})
    } finally {
      setDeletingId(null)
    }
  }

  async function submit(e: React.FormEvent) {
    e.preventDefault()
    setErrors({})
    setLoading(true)
    try {
      const zona = await api.createZona({
        nome,
        xMin: Number.parseFloat(xMin),
        yMin: Number.parseFloat(yMin),
        xMax: Number.parseFloat(xMax),
        yMax: Number.parseFloat(yMax),
      })
      toast.success("Zona criada", { description: `"${zona.nome}" adicionada ao mapa.` })
      setNome("")
      setXMin("")
      setYMin("")
      setXMax("")
      setYMax("")
      onCreated()
    } catch (err) {
      handleError(err, setErrors)
    } finally {
      setLoading(false)
    }
  }

  return (
    <Card>
      <CardHeader className="flex flex-row items-center gap-2 space-y-0 pb-3">
        <ShieldAlert className="size-4 text-destructive" aria-hidden />
        <CardTitle className="font-display text-sm">Criar Zona de Exclusão</CardTitle>
      </CardHeader>
      <CardContent>
        <form onSubmit={submit} className="flex flex-col gap-3" noValidate>
          <div className="flex flex-col gap-1.5">
            <Label htmlFor="z-nome">Nome</Label>
            <Input
              id="z-nome"
              value={nome}
              onChange={(e) => setNome(e.target.value)}
              placeholder="Zona Hospitalar Central"
            />
            <FieldError msg={errors.nome} />
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="z-xmin">X Mínimo</Label>
              <Input
                id="z-xmin"
                type="number"
                step="0.1"
                value={xMin}
                onChange={(e) => setXMin(e.target.value)}
                placeholder="2.0"
              />
              <FieldError msg={errors.xMin} />
            </div>
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="z-ymin">Y Mínimo</Label>
              <Input
                id="z-ymin"
                type="number"
                step="0.1"
                value={yMin}
                onChange={(e) => setYMin(e.target.value)}
                placeholder="2.0"
              />
              <FieldError msg={errors.yMin} />
            </div>
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="z-xmax">X Máximo</Label>
              <Input
                id="z-xmax"
                type="number"
                step="0.1"
                value={xMax}
                onChange={(e) => setXMax(e.target.value)}
                placeholder="6.0"
              />
              <FieldError msg={errors.xMax} />
            </div>
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="z-ymax">Y Máximo</Label>
              <Input
                id="z-ymax"
                type="number"
                step="0.1"
                value={yMax}
                onChange={(e) => setYMax(e.target.value)}
                placeholder="8.0"
              />
              <FieldError msg={errors.yMax} />
            </div>
          </div>
          <Button type="submit" disabled={loading} className="mt-1">
            {loading && <Loader2 className="size-4 animate-spin" />}
            Criar Zona
          </Button>
        </form>

        {zonas.length > 0 && (
          <div className="mt-4 flex flex-col gap-1.5 border-t border-border pt-3">
            <p className="hud-label text-[10px] text-muted-foreground">Zonas ativas</p>
            {zonas.map((z) => (
              <div
                key={z.id}
                className="flex items-center justify-between gap-2 rounded-md border border-border bg-muted/40 px-2.5 py-1.5"
              >
                <div className="min-w-0">
                  <p className="truncate text-xs font-medium text-foreground">{z.nome}</p>
                  <p className="font-mono text-[10px] tabular-nums text-muted-foreground">
                    ({z.xMin}, {z.yMin}) → ({z.xMax}, {z.yMax})
                  </p>
                </div>
                <Button
                  type="button"
                  size="icon"
                  variant="ghost"
                  className="size-7 shrink-0 text-muted-foreground hover:text-destructive"
                  onClick={() => remover(z.id)}
                  disabled={deletingId === z.id}
                  aria-label={`Remover zona ${z.nome}`}
                >
                  {deletingId === z.id ? (
                    <Loader2 className="size-3.5 animate-spin" />
                  ) : (
                    <Trash2 className="size-3.5" />
                  )}
                </Button>
              </div>
            ))}
          </div>
        )}
      </CardContent>
    </Card>
  )
}
