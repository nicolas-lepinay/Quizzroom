package com.ynov.kiwi.api.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@RestController
@RequestMapping("/sse")
public class SseController {

    private final Set<SseEmitter> emitters = new CopyOnWriteArraySet<>();

    @GetMapping(path = "/players", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamPlayers() {
        SseEmitter emitter = new SseEmitter(0L); // no timeout
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        return emitter;
    }

    public void sendPlayerUpdate() {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("playerUpdate")
                        .data("data"));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }

    /*
    public void sendPlayerControlUpdate() {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("controlUpdate")
                        .data("update"));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }
     */
}
