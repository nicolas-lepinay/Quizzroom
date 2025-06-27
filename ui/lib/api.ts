import type { PlayersResponse, ApiResponse, QuestionResponse } from "@/types/game"

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL

export const api = {
  async getPlayers(): Promise<PlayersResponse> {
    try {
      const response = await fetch(`${API_BASE_URL}/players`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        },
      })

      if (!response.ok) {
        throw new Error(`HTTP error. Status: ${response.status}`)
      }

      return await response.json()
    } catch (error) {
      console.error("API not available: ", error)
      throw new Error("API not available.")
    }
  },

  async startGame(): Promise<ApiResponse<any>> {
    try {
      const response = await fetch(`${API_BASE_URL}/game/start`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
      })

      if (!response.ok) {
        throw new Error(`HTTP error. Status: ${response.status}`)
      }

      return await response.json()
    } catch (error) {
      console.error("API not available: ", error)
      throw new Error("API not available.")
    }
  },

  async getCurrentQuestion(): Promise<QuestionResponse> {
    try {
      const response = await fetch(`${API_BASE_URL}/game/current-question`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        },
      })

      if (!response.ok) {
        throw new Error(`HTTP error. Status: ${response.status}`)
      }

      return await response.json()
    } catch (error) {
      console.error("API not available: ", error)
      throw new Error("API not available.")
    }
  },

  async getNextQuestion(): Promise<QuestionResponse> {
    try {
      const response = await fetch(`${API_BASE_URL}/game/next-question`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
      })

      if (!response.ok) {
        throw new Error(`HTTP error. Status: ${response.status}`)
      }

      return await response.json()
    } catch (error) {
      console.error("API not available: ", error)
      throw new Error("API not available.")
    }
  },

  async updatePlayerScore(playerId: number): Promise<ApiResponse<any>> {
    try {
      const response = await fetch(`${API_BASE_URL}/players/${playerId}/score`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
      })

      if (!response.ok) {
        throw new Error(`HTTP error. Status: ${response.status}`)
      }

      return await response.json()
    } catch (error) {
      console.error("API not available: ", error)
      throw new Error("API not available.")
    }
  },

  async updatePlayerName(playerId: number, name: string): Promise<ApiResponse<any>> {
    try {
      const response = await fetch(`${API_BASE_URL}/players/${playerId}/name`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ name }),
      })

      if (!response.ok) {
        throw new Error(`HTTP error. Status: ${response.status}`)
      }

      return await response.json()
    } catch (error) {
      console.warn("API not available: ", error)
      throw new Error("API not available.")
    }
  },
}
