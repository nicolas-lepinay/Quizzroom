package com.ynov.kiwi.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ynov.kiwi.api.entity.Question;
import com.ynov.kiwi.api.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.*;

@Service
public class QuestionService {
    private final QuestionRepository repo;

    public QuestionService(QuestionRepository repo) { this.repo = repo; }

    @PostConstruct
    public void init() {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("questions.json")) {
            if (is != null) {
                Question[] questions = mapper.readValue(is, Question[].class);
                for (Question q : questions) repo.save(q);
                System.out.println("[Questions] " + questions.length + " questions chargées");
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement questions : " + e.getMessage());
        }
    }
    public Question addQuestion(Question q) { return repo.save(q); }
    public Collection<Question> getQuestions() { return repo.findAll(); }
    public Optional<Question> getQuestion(int id) { return repo.findById(id); }
}
