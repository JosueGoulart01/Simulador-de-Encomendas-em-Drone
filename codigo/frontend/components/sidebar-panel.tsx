"use client"

import { FilePlus2, ListOrdered, LayoutGrid } from "lucide-react"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { CadastrosTab } from "@/components/cadastros-tab"
import { FilaTab } from "@/components/fila-tab"
import { FrotaTab } from "@/components/frota-tab"

export function SidebarPanel() {
  return (
    <Tabs defaultValue="cadastros" className="flex h-full flex-col">
      <TabsList className="grid w-full grid-cols-3 bg-muted/50">
        <TabsTrigger value="cadastros" className="hud-label gap-1.5 text-[10px]">
          <FilePlus2 className="size-4" aria-hidden />
          <span className="hidden sm:inline">Cadastros</span>
        </TabsTrigger>
        <TabsTrigger value="fila" className="hud-label gap-1.5 text-[10px]">
          <ListOrdered className="size-4" aria-hidden />
          <span className="hidden sm:inline">Fila</span>
        </TabsTrigger>
        <TabsTrigger value="frota" className="hud-label gap-1.5 text-[10px]">
          <LayoutGrid className="size-4" aria-hidden />
          <span className="hidden sm:inline">Frota</span>
        </TabsTrigger>
      </TabsList>

      <div className="mt-3 flex-1 overflow-y-auto pr-1">
        <TabsContent value="cadastros" className="mt-0">
          <CadastrosTab />
        </TabsContent>
        <TabsContent value="fila" className="mt-0">
          <FilaTab />
        </TabsContent>
        <TabsContent value="frota" className="mt-0">
          <FrotaTab />
        </TabsContent>
      </div>
    </Tabs>
  )
}