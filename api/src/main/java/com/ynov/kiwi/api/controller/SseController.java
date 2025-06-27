package com.ynov.kiwi.api.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

@RestController
@RequestMapping("/sse")
public class SseController {
    private final List<SseEmitter> playerEmitters = new CopyOnWriteArrayList<>();
    private final List<SseEmitter> buzzerEmitters = new CopyOnWriteArrayList<>();

    @GetMapping(path = "/players", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamPlayers() {
        SseEmitter emitter = new SseEmitter(0L); // no timeout
        playerEmitters.add(emitter);
        emitter.onCompletion(() -> playerEmitters.remove(emitter));
        emitter.onTimeout(() -> playerEmitters.remove(emitter));
        return emitter;
    }

    @GetMapping(path = "/buzzers", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamBuzzers() {
        SseEmitter emitter = new SseEmitter(0L);
        buzzerEmitters.add(emitter);
        emitter.onCompletion(() -> buzzerEmitters.remove(emitter));
        emitter.onTimeout(() -> buzzerEmitters.remove(emitter));
        emitter.onError((e) -> buzzerEmitters.remove(emitter));
        return emitter;
    }

    // Diffuser aux UIs le nouvel état des joueurs
    public void sendPlayerUpdate() {
        for (SseEmitter emitter : playerEmitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("playerUpdate")
                        .data("data"));
            } catch (IOException e) {
                playerEmitters.remove(emitter);
            }
        }
    }

    // Diffuser l’event enable ou disable au client Swing
    public void sendEnable(int playerId) {
        for (SseEmitter emitter : buzzerEmitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("enable")
                        .data(playerId));
            } catch (IOException e) {
                buzzerEmitters.remove(emitter);
            }
        }
    }
    public void sendDisable(int playerId) {
        for (SseEmitter emitter : buzzerEmitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("disable")
                        .data(playerId));
            } catch (IOException e) {
                buzzerEmitters.remove(emitter);
            }
        }
    }
}
