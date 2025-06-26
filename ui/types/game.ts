export interface Player {
  id: number
  score: number
  enabled: boolean
  hasAttempted: boolean
  inControl: boolean
  givenName?: string
}

export interface ApiResponse<T> {
  status: string
  code: number
  message: string
  data: T
}

export interface Question {
  id: number
  text: string
  answer: string
}

export interface PlayersResponse {
  status: string
  code: number
  message: string
  data: Player[]
}

export interface QuestionResponse {
  status: string
  code: number
  message: string
  data: Question
}
