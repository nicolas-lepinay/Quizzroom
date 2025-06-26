package com.ynov.kiwi.api.repository;

import com.ynov.kiwi.api.entity.Question;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class QuestionRepository {
    private final Map<Integer, Question> questions = new ConcurrentHashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    public Question save(Question q) {
        if (q.getId() == 0) q.setId(nextId.getAndIncrement());
        questions.put(q.getId(), q);
        return q;
    }
    public Optional<Question> findById(int id) { return Optional.ofNullable(questions.get(id)); }
    public Collection<Question> findAll() { return questions.values(); }
    public void clear() { questions.clear(); nextId.set(1); }
}
