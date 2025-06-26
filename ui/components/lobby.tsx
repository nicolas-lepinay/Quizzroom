"use client"

import { useEffect, useState, useCallback } from "react"
import { Button } from "@/components/ui/button"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { Podium } from "./podium"
import type { Player } from "@/types/game"
import { api } from "@/lib/api"
import { useSSE } from "@/hooks/use-sse"

interface LobbyProps {
  onStartGame: () => void
}

export function Lobby({ onStartGame }: LobbyProps) {
  const [players, setPlayers] = useState<Player[]>([])
  const [playerNames, setPlayerNames] = useState<Record<number, string>>({})
  const [canStart, setCanStart] = useState(false)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [isApiAvailable, setIsApiAvailable] = useState(true)

  const fetchPlayers = useCallback(async () => {
    try {
      setError(null)
      const response = await api.getPlayers()

      if (response.message.includes("Mock data")) {
        setIsApiAvailable(false)
      }

      if (response.status === "success" && Array.isArray(response.data)) {
        const playersWithNames = response.data.map((player) => ({
          ...player,
          givenName: playerNames[player.id] || player.givenName || "",
        }))
        setPlayers(playersWithNames)
        setCanStart(response.status === "success" && response.data.length >= 2)
      } else {
        setCanStart(false)
        setError("Invalid response from server")
      }
    } catch (error) {
      console.error("Error fetching players:", error)
      setError("Failed to connect to game server")
      setCanStart(false)
    }
  }, [playerNames])

  const handlePlayerUpdate = useCallback(() => {
    fetchPlayers()
  }, [fetchPlayers])

  // Only use SSE if API is available
  useSSE(isApiAvailable ? process.env.NEXT_PUBLIC_SSE_URL! : "", "playerUpdate", handlePlayerUpdate)

  useEffect(() => {
    fetchPlayers()
  }, [fetchPlayers])

  const handleNameChange = (playerId: number, name: string) => {
    setPlayerNames((prev) => ({ ...prev, [playerId]: name }))
  }

  const handleStartGame = async () => {
    setLoading(true)
    setError(null)
    console.log(playerNames)
    try {
      // 1. Mise à jour des noms dans l’API pour chaque joueur
      await Promise.all(
          Object.entries(playerNames).map(([id, name]) =>
              api.updatePlayerName(Number(id), name)
          )
      )
      // 2. Lancer la partie
      const response = await api.startGame()
      if (response.status === "success") {
        onStartGame()
      } else {
        setError("Failed to start game")
      }
    } catch (error) {
      console.error("Error starting game:", error)
      setError("Failed to start game")
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-gradient-to-b from-blue-900 to-purple-900 text-white">
      <div className="container mx-auto px-4 py-8">

        <div className="text-center mb-8">
          <h2 className="text-3xl font-semibold mb-4">Lobby Room</h2>
          {!isApiAvailable && (
            <Alert className="mb-4 bg-yellow-100 border-yellow-400">
              <AlertDescription className="text-yellow-800">
                Demo Mode: API server not available. Using mock data for demonstration.
              </AlertDescription>
            </Alert>
          )}
          <p className="text-xl">En attente d'autres joueurs..</p>
        </div>

        {error && (
          <Alert className="mb-4 bg-red-100 border-red-400">
            <AlertDescription className="text-red-800">{error}</AlertDescription>
          </Alert>
        )}

        {/* Players Podiums */}
        <div className="flex justify-center items-center flex-wrap gap-4 mb-12">
          {players.map((player) => (
            <Podium key={player.id} player={player} onNameChange={handleNameChange} />
          ))}
        </div>

        {players.length === 0 && (
          <div className="text-center text-xl mb-8">
            {loading ? "Loading players..." : "Aucun joueur connecté..."}
          </div>
        )}

        {/* Start Game Button */}
        <div className="text-center">
          <Button
            onClick={handleStartGame}
            disabled={!canStart || loading}
            className="bg-green-600 hover:bg-green-700 text-white text-2xl px-12 py-4 rounded-lg disabled:bg-gray-500"
          >
            {loading ? "Starting..." : "Start Game"}
          </Button>
          {!canStart && players.length > 0 && (
            <p className="mt-4 text-red-400">Deux joueurs minimum requis.</p>
          )}
          {!canStart && players.length < 2 && players.length > 0 && (
            <p className="mt-4 text-yellow-400">
              {players.length} player{players.length !== 1 ? "s" : ""} connected. Need at least 2 to start.
            </p>
          )}
        </div>
      </div>
    </div>
  )
}
