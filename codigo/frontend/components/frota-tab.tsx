"use client"

import { useState } from "react"
import { Loader2, BatteryMedium, Pencil, Trash2 } from "lucide-react"
import { Card } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { useDrones } from "@/hooks/use-live-data"
import { STATUS_META, type Drone } from "@/lib/types"
import { EditDroneDialog } from "@/components/edit-drone-dialog"
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog"
import { toast } from "sonner"
import { api, isApiValidationError, NetworkError } from "@/lib/api"
import { useSWRConfig } from "swr"

export function FrotaTab() {
  const { drones, isLoading } = useDrones()
  const { mutate } = useSWRConfig()

  const [editingDrone, setEditingDrone] = useState<Drone | null>(null)
  const [deletingId, setDeletingId] = useState<number | null>(null)
  const [deleting, setDeleting] = useState(false)

  async function handleDelete(id: number) {
    setDeleting(true)
    try {
      await api.deleteDrone(id)
      toast.success("Drone removido", { description: `Drone #${id} removido da frota.` })
      mutate("drones")
      mutate("dashboard")
    } catch (err) {
      if (err instanceof NetworkError) {
        toast.error("Sem conexão com o servidor")
      } else if (isApiValidationError(err)) {
        toast.error(err.erro, { description: err.mensagem })
      } else {
        toast.error("Erro inesperado", { description: "Não foi possível remover o drone." })
      }
    } finally {
      setDeleting(false)
      setDeletingId(null)
    }
  }

  if (isLoading && drones.length === 0) {
    return (
      <div className="flex h-40 items-center justify-center text-muted-foreground">
        <Loader2 className="size-5 animate-spin" />
      </div>
    )
  }

  return (
    <>
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
                <div className="flex items-center gap-1.5">
                  <span
                    className="rounded-full px-2 py-0.5 text-[10px] font-semibold"
                    style={{
                      background: `color-mix(in srgb, ${meta.color} 18%, transparent)`,
                      color: meta.color,
                    }}
                  >
                    {meta.label}
                  </span>
                  <Button
                    variant="ghost"
                    size="icon"
                    className="size-7 text-muted-foreground hover:text-foreground"
                    onClick={() => setEditingDrone(d)}
                    disabled={d.statusAtual !== "IDLE"}
                    title={d.statusAtual !== "IDLE" ? "Só é possível editar drones ociosos" : "Editar drone"}
                  >
                    <Pencil className="size-3.5" />
                  </Button>
                  <Button
                    variant="ghost"
                    size="icon"
                    className="size-7 text-muted-foreground hover:text-destructive"
                    onClick={() => setDeletingId(d.id)}
                    disabled={d.statusAtual !== "IDLE"}
                    title={d.statusAtual !== "IDLE" ? "Só é possível remover drones ociosos" : "Remover drone"}
                  >
                    <Trash2 className="size-3.5" />
                  </Button>
                </div>
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
                <div>
                  <p className="text-muted-foreground">Autonomia</p>
                  <p className="tabular-nums text-foreground">{d.autonomiaMaxKm.toFixed(1)} km</p>
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

      {/* Modal de edição */}
      <EditDroneDialog
        drone={editingDrone}
        open={!!editingDrone}
        onClose={() => setEditingDrone(null)}
        onSuccess={() => {
          mutate("drones")
          mutate("dashboard")
          setEditingDrone(null)
        }}
      />

      {/* Diálogo de confirmação de exclusão */}
      <AlertDialog open={!!deletingId} onOpenChange={(open) => !open && setDeletingId(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Remover drone?</AlertDialogTitle>
            <AlertDialogDescription>
              Esta ação não pode ser desfeita. O drone será permanentemente removido da frota.
              {deletingId && drones.find(d => d.id === deletingId)?.statusAtual !== "IDLE" && (
                <span className="block mt-2 text-destructive">Atenção: este drone não está ocioso. A remoção pode falhar.</span>
              )}
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel disabled={deleting}>Cancelar</AlertDialogCancel>
            <AlertDialogAction
              onClick={() => deletingId && handleDelete(deletingId)}
              disabled={deleting}
              className="bg-destructive hover:bg-destructive/90"
            >
              {deleting ? <Loader2 className="size-4 animate-spin mr-2" /> : null}
              Remover
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </>
  )
}