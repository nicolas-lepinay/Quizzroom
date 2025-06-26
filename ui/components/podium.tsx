"use client"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Card, CardContent } from "@/components/ui/card"
import type { Player } from "@/types/game"

interface PodiumProps {
  player: Player
  onNameChange?: (playerId: number, name: string) => void
  onScoreIncrease?: (playerId: number) => void
  showScoreButton?: boolean
}

export function Podium({ player, onNameChange, onScoreIncrease, showScoreButton = false }: PodiumProps) {
  const [name, setName] = useState(player.givenName || "")

  const getBuzzerColor = () => {
    if (player.inControl) return "bg-green-500 hover:bg-green-600"
    if (player.enabled) return "bg-orange-500 hover:bg-orange-600"
    return "bg-gray-400 hover:bg-gray-500"
  }

  const handleNameChange = (value: string) => {
    setName(value)
    onNameChange?.(player.id, value)
  }

  const displayName = name || `Buzzer no. ${player.id}`

  return (
    <Card className="w-48 mx-4">
      <CardContent className="p-4 text-center">
        {/* Buzzer Button */}
        <Button
          className={`w-24 h-24 rounded-full text-white font-bold text-lg mb-4 ${getBuzzerColor()}`}
          disabled={!player.enabled}
        >
          BUZZ
        </Button>

        {/* Name Input or Display */}
        {onNameChange ? (
          <Input
            value={name}
            onChange={(e) => handleNameChange(e.target.value)}
            placeholder={`Buzzer no. ${player.id}`}
            className="mb-4 text-center"
          />
        ) : (
          <div className="mb-4 font-semibold text-lg">{displayName}</div>
        )}

        {/* Score Display */}
        <div className="bg-blue-100 border-2 border-blue-300 rounded-lg p-4 mb-4">
          <div className="text-3xl font-bold text-blue-800">{player.score}</div>
        </div>

        {/* Score Increase Button */}
        {showScoreButton && (
          <Button onClick={() => onScoreIncrease?.(player.id)} className="bg-green-600 hover:bg-green-700 text-white">
            +1
          </Button>
        )}
      </CardContent>
    </Card>
  )
}
