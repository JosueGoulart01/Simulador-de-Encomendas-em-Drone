"use client"

import useSWR from "swr"
import { api } from "@/lib/api"

const POLL_INTERVAL = 2000

export function useDrones() {
  const { data, error, isLoading } = useSWR("drones", () => api.getDrones(), {
    refreshInterval: POLL_INTERVAL,
    keepPreviousData: true,
  })
  return { drones: data ?? [], error, isLoading, online: !error }
}

export function useFila() {
  const { data, error, isLoading } = useSWR("fila", () => api.getFila(), {
    refreshInterval: POLL_INTERVAL,
    keepPreviousData: true,
  })
  return { fila: data ?? [], error, isLoading }
}

export function usePedidosAtivos() {
  const { data, error, isLoading } = useSWR("pedidos-ativos", () => api.getPedidosAtivos(), {
    refreshInterval: POLL_INTERVAL,
    keepPreviousData: true,
  })
  return { pedidos: data ?? [], error, isLoading }
}

export function useDashboard() {
  const { data, error, isLoading } = useSWR("dashboard", () => api.getDashboard(), {
    refreshInterval: POLL_INTERVAL,
    keepPreviousData: true,
  })
  return { kpis: data, error, isLoading }
}

export function useZonas() {
  const { data, error, isLoading, mutate } = useSWR("zonas", () => api.getZonas(), {
    keepPreviousData: true,
  })
  return { zonas: data ?? [], error, isLoading, refreshZonas: mutate }
}
