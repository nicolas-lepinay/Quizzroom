import type { PlayersResponse, ApiResponse, QuestionResponse } from "@/types/game"

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL

// Mock data for development when API is not available
const mockPlayers = [
  { id: 1, score: 0, enabled: true, hasAttempted: false, inControl: false },
  { id: 2, score: 0, enabled: true, hasAttempted: false, inControl: false },
  { id: 3, score: 0, enabled: true, hasAttempted: false, inControl: false },
]

const mockQuestion = {
  id: 1,
  text: "What is the capital city of France?",
  answer: "Paris",
}

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
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      return await response.json()
    } catch (error) {
      console.warn("API not available, using mock data:", error)
      // Return mock data when API is not available
      return {
        status: "success",
        code: 200,
        message: "Mock data - API not available",
        data: mockPlayers,
      }
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
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      return await response.json()
    } catch (error) {
      console.warn("API not available, using mock response:", error)
      return {
        status: "success",
        code: 200,
        message: "Mock game start - API not available",
        data: {},
      }
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
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      return await response.json()
    } catch (error) {
      console.warn("API not available, using mock question:", error)
      return {
        status: "success",
        code: 200,
        message: "Mock question - API not available",
        data: mockQuestion,
      }
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
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      return await response.json()
    } catch (error) {
      console.warn("API not available, using mock question:", error)
      return {
        status: "success",
        code: 200,
        message: "Mock next question - API not available",
        data: {
          ...mockQuestion,
          id: mockQuestion.id + 1,
          text: "What is the capital city of the USA?",
          answer: "Washington",
        },
      }
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
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      return await response.json()
    } catch (error) {
      console.warn("API not available, using mock response:", error)
      return {
        status: "success",
        code: 200,
        message: "Mock score update - API not available",
        data: {},
      }
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
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      return await response.json()
    } catch (error) {
      console.warn("API not available, using mock response:", error)
      return {
        status: "success",
        code: 200,
        message: "Mock name update - API not available",
        data: {},
      }
    }
  },
}
