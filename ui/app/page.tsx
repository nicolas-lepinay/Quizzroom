"use client"

import { useState } from "react"
import { Lobby } from "@/components/lobby"
import { Game } from "@/components/game"

export default function Home() {
  const [gameStarted, setGameStarted] = useState(false)

  const handleStartGame = () => {
    setGameStarted(true)
  }

  const handleReset = () => {
    setGameStarted(false)
  }

  return (
    <main>
      {gameStarted
        ? <Game onResetGame={handleReset} />
        : <Lobby onStartGame={handleStartGame} />}
    </main>
  )
}
