"use client"

import { useEffect, useRef } from "react"

export function useSSE(url: string, eventName: string, onMessage: () => void) {
  const eventSourceRef = useRef<EventSource | null>(null)

  useEffect(() => {
    // Don't try to connect if URL is not available
    if (!url || url.includes("undefined")) {
      console.warn("SSE URL not available, skipping connection")
      return
    }

    try {
      eventSourceRef.current = new EventSource(url)
      eventSourceRef.current.addEventListener(eventName, (event) => {
        onMessage()
      })

      eventSourceRef.current.addEventListener("open", () => {
        console.log("[SSE] Connection opened : " + url)
      })

      eventSourceRef.current.onerror = (error) => {
        console.warn("[SSE] Connection error (this is normal if API is not running): ", error)
      }
    } catch (error) {
      console.warn("Failed to create SSE connection: ", error)
    }

    return () => {
      if (eventSourceRef.current) {
        eventSourceRef.current.close()
      }
    }
  }, [url, eventName, onMessage])

  return eventSourceRef.current
}
