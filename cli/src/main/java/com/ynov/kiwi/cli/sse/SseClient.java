package com.ynov.kiwi.cli.sse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Consumer;

public class SseClient {
    private final String url;
    private Thread thread;
    private volatile boolean running = false;

    private Consumer<Integer> onEnable;
    private Consumer<Integer> onDisable;
    private Runnable onReset;

    public SseClient(String url) {
        this.url = url;
    }

    public void setOnEnable(Consumer<Integer> onEnable) { this.onEnable = onEnable; }
    public void setOnDisable(Consumer<Integer> onDisable) { this.onDisable = onDisable; }
    public void setOnReset(Runnable onReset) { this.onReset = onReset; }

    public void start() {
        if (running) return;
        running = true;
        thread = new Thread(this::connectAndListen, "SSE-Client-Thread");
        thread.setDaemon(true);
        thread.start();
    }

    public void stop() {
        running = false;
        if (thread != null) thread.interrupt();
    }

    private void connectAndListen() {
        while (running) {
            try {
                System.out.println("[SSE] Connecting to " + url);
                URL sseUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) sseUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "text/event-stream");
                connection.setReadTimeout(0); // infini
                connection.setConnectTimeout(3000);
                connection.connect();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line, event = null, data = null;
                    while (running && (line = reader.readLine()) != null) {
                        if (line.startsWith("event:")) {
                            event = line.substring(6).trim();
                        } else if (line.startsWith("data:")) {
                            data = line.substring(5).trim();
                        } else if (line.isEmpty()) {
                            // Fin d’event, on traite
                            if (event != null && data != null) {
                                handleEvent(event, data);
                            }
                            event = null;
                            data = null;
                        }
                    }
                }
            } catch (Exception ex) {
                System.err.println("[SSE] Connection error: " + ex.getMessage());
                // Attendre 3s et retenter
                try { Thread.sleep(3000); } catch (InterruptedException ignore) {}
            }
        }
        System.out.println("[SSE] Stopped");
    }

    private void handleEvent(String event, String data) {
        try {
            if ("enable".equals(event) && onEnable != null) {
                int id = Integer.parseInt(data);
                onEnable.accept(id);
            } else if ("disable".equals(event) && onDisable != null) {
                int id = Integer.parseInt(data);
                onDisable.accept(id);
            } else if ("reset".equals(event) && onReset != null) {
                onReset.run();
            }
        } catch (Exception e) {
            System.err.println("[SSE] Malformed event/data: event=" + event + ", data=" + data);
        }
    }

}
