"use client"
import { useEffect, useState } from "react"

interface CountdownInlineProps {
  duration: number // en ms
  running: boolean
  trigger: string
}

export function CountdownInline({ duration, running, trigger }: CountdownInlineProps) {
  const [timeLeft, setTimeLeft] = useState(duration)

  useEffect(() => {
    if (!running) return
    setTimeLeft(duration)
    const interval = setInterval(() => {
      setTimeLeft((prev) => (prev > 1000 ? prev - 1000 : 0))
    }, 1000)
    return () => clearInterval(interval)
  }, [duration, running, trigger])

  // Affichage en secondes, arrondi
  return (
    <div className="text-white font-bold text-lg">
      {timeLeft > 0 ? `${Math.ceil(timeLeft / 1000)}s` : null}
    </div>
  )
}
