"use client"

import { useState, useEffect } from "react"
import { Loader2 } from "lucide-react"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { api, isApiValidationError, NetworkError } from "@/lib/api"
import { toast } from "sonner"
import type { Drone } from "@/lib/types"

interface EditDroneDialogProps {
  drone: Drone | null
  open: boolean
  onClose: () => void
  onSuccess: () => void
}

export function EditDroneDialog({ drone, open, onClose, onSuccess }: EditDroneDialogProps) {

  const [identificador, setIdentificador] = useState("")
  const [capacidade, setCapacidade] = useState("")
  const [autonomia, setAutonomia] = useState("")
  const [loading, setLoading] = useState(false)
  const [errors, setErrors] = useState<Record<string, string>>({})

  useEffect(() => {
    if (drone) {
      setIdentificador(drone.identificador)
      setCapacidade(String(drone.capacidadeMaxKg))
      setAutonomia(String(drone.autonomiaMaxKm))
      setErrors({})
    }
  }, [drone])

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    if (!drone) return
    setErrors({})
    setLoading(true)
    try {
      await api.updateDrone(drone.id, {
        identificador,
        capacidadeMaxKg: Number.parseFloat(capacidade),
        autonomiaMaxKm: Number.parseFloat(autonomia),
      })
      toast.success("Drone atualizado", { description: `Drone ${identificador} atualizado com sucesso.` })
      onSuccess()
    } catch (err) {
      if (err instanceof NetworkError) {
        toast.error("Sem conexão com o servidor")
      } else if (isApiValidationError(err)) {
        setErrors(err.validacoes ?? {})
        toast.error(err.erro, { description: err.mensagem })
      } else {
        toast.error("Erro inesperado", { description: "Não foi possível atualizar o drone." })
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <Dialog open={open} onOpenChange={(open) => !open && onClose()}>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>Editar Drone</DialogTitle>   
          <DialogDescription>
            Altere as informações do drone. Somente drones ociosos podem ser editados.
          </DialogDescription>
        </DialogHeader>
        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          <div className="flex flex-col gap-1.5">
            <Label htmlFor="edit-id">Identificador</Label>
            <Input
              id="edit-id"
              value={identificador}
              onChange={(e) => setIdentificador(e.target.value)}
              placeholder="Drone-Alpha-05"
            />
            {errors.identificador && <p className="text-xs text-destructive">{errors.identificador}</p>}
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="edit-cap">Capacidade (kg)</Label>
              <Input
                id="edit-cap"
                type="number"
                step="0.1"
                value={capacidade}
                onChange={(e) => setCapacidade(e.target.value)}
                placeholder="10.0"
              />
              {errors.capacidadeMaxKg && <p className="text-xs text-destructive">{errors.capacidadeMaxKg}</p>}
            </div>
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="edit-aut">Autonomia (km)</Label>
              <Input
                id="edit-aut"
                type="number"
                step="0.1"
                value={autonomia}
                onChange={(e) => setAutonomia(e.target.value)}
                placeholder="50.0"
              />
              {errors.autonomiaMaxKm && <p className="text-xs text-destructive">{errors.autonomiaMaxKm}</p>}
            </div>
          </div>
          <DialogFooter>
            <Button type="button" variant="outline" onClick={onClose} disabled={loading}>
              Cancelar
            </Button>
            <Button type="submit" disabled={loading}>
              {loading && <Loader2 className="size-4 animate-spin mr-2" />}
              Salvar
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}