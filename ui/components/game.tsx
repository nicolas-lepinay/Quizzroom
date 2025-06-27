"use client"

import { useEffect, useState, useCallback } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { Podium } from "./podium"
import { Countdown } from "./countdown"
import type { Player, Question } from "@/types/game"
import { api } from "@/lib/api"
import { useSSE } from "@/hooks/use-sse"

export function Game() {
  const [players, setPlayers] = useState<Player[]>([])
  const [currentQuestion, setCurrentQuestion] = useState<Question | null>(null)
  const [answerTime, setAnswerTime] = useState(7000)
  const [showAnswer, setShowAnswer] = useState(false)
  const [showCountdown, setShowCountdown] = useState(true)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const fetchPlayers = useCallback(async () => {
    try {
      const response = await api.getPlayers()

      if (response.status === "success" && Array.isArray(response.data)) {
        setPlayers(response.data)
      }
    } catch (error) {
      console.error("Error fetching players:", error)
    }
  }, [])

  const fetchCurrentQuestion = useCallback(async () => {
    try {
      setError(null)
      const response = await api.getCurrentQuestion()
      if (response.status === "success") {
        setCurrentQuestion(response.data)
        setShowAnswer(false)
      }
    } catch (error) {
      console.error("Error fetching current question:", error)
      setError("Failed to load question")
    }
  }, [])

  const handlePlayerUpdate = useCallback(() => {
    fetchPlayers()
  }, [fetchPlayers])

  const handleCountdownComplete = () => {
    setShowCountdown(false)
    fetchCurrentQuestion()
  }

  const handleScoreIncrease = async (playerId: number) => {
    try {
      await api.updatePlayerScore(playerId)
    } catch (error) {
      console.error("Error updating player score:", error)
    }
  }

  const handleNextQuestion = async () => {
    setLoading(true)
    setError(null)
    try {
      const response = await api.getNextQuestion()
      if (response.status === "success") {
        setCurrentQuestion(response.data)
        setShowAnswer(false)
        setShowCountdown(true)
      }
    } catch (error) {
      console.error("Error fetching next question:", error)
      setError("Failed to load next question")
    } finally {
      setLoading(false)
    }
  }

  // Only use SSE if API is available
  useSSE(error ? "" : process.env.NEXT_PUBLIC_SSE_URL!, "playerUpdate", handlePlayerUpdate)

  useEffect(() => {
    fetchPlayers()
  }, [fetchPlayers])

  useEffect(() => {
    api.getAnswerTime().then(setAnswerTime)
  }, [])



  return (
    <div className="min-h-screen bg-gradient-to-b from-blue-900 to-purple-900 text-white">
      {showCountdown && <Countdown onComplete={handleCountdownComplete} />}

      <div className="container mx-auto px-4 py-8">
        {/* <h1 className="text-4xl font-bold text-center mb-8 text-yellow-400">Question Pour Un Champion</h1> */}

        {error && (
          <Alert className="mb-4 bg-red-100 border-red-400">
            <AlertDescription className="text-red-800 text-center">{error}</AlertDescription>
          </Alert>
        )}

        {/* Current Question */}
          <div className={currentQuestion && !showCountdown ? "opacity-100": "opacity-0"}>
            <Card className="mb-8 bg-white text-black" >
              <CardContent className="p-6">
                {/* <h2 className="text-2xl font-bold mb-4">Question:</h2> */}
                <p className="text-xl mb-4">{currentQuestion?.text || "..."}</p>

                <Button onClick={() => setShowAnswer(!showAnswer)} className="bg-blue-600 hover:bg-blue-700 text-white">
                  {showAnswer ? "Cacher la réponse" : "Montrer la réponse"}
                </Button>

                {showAnswer && (
                  <div className="mt-4 p-4 bg-green-100 rounded-lg">
                    <p className="text-md font-semibold text-green-800">{currentQuestion?.answer || "..."}</p>
                  </div>
                )}
              </CardContent>
            </Card>
          </div>


        {/* Players Podiums */}
        <div className="flex justify-center items-center flex-wrap gap-4 mb-8">
          {players.map((player) => (
            <Podium 
                key={player.id} 
                player={player} 
                answerTime={answerTime}
                onScoreIncrease={handleScoreIncrease} 
                showScoreButton={true} 
            />
          ))}
        </div>

        {/* Next Question Button */}
        <div className="text-right">
          <Button
            onClick={handleNextQuestion}
            disabled={loading}
            className="bg-purple-600 hover:bg-purple-700 text-white text-xl px-8 py-3"
          >
            {loading ? "Loading..." : "Suivant"}
          </Button>
        </div>
      </div>
    </div>
  )
}