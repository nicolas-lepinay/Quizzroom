"use client"

import { useState } from "react"
import { Lobby } from "@/components/lobby"
import { Game } from "@/components/game"

export default function Home() {
  const [gameStarted, setGameStarted] = useState(false)

  const handleStartGame = () => {
    setGameStarted(true)
  }

  return <main>{gameStarted ? <Game /> : <Lobby onStartGame={handleStartGame} />}</main>
}
