"use client"

import { useEffect, useState } from "react"

interface CountdownProps {
  onComplete: () => void
}

export function Countdown({ onComplete }: CountdownProps) {
  const [count, setCount] = useState(3)

  useEffect(() => {
    if (count > 0) {
      const timer = setTimeout(() => {
        setCount(count - 1)
      }, 1000)
      return () => clearTimeout(timer)
    } else {
      onComplete()
    }
  }, [count, onComplete])

  if (count === 0) return null

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="text-9xl font-bold text-white animate-pulse">{count}</div>
    </div>
  )
}
