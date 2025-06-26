package com.ynov.kiwi.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ynov.kiwi.api.entity.Question;
import com.ynov.kiwi.api.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {
    private final QuestionRepository repo;

    public QuestionService(QuestionRepository repo) { this.repo = repo; }

    @PostConstruct
    public void init() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("questions.json")) {
            if (is != null) {
                ObjectMapper mapper = new ObjectMapper();
                List<Question> questions = mapper.readValue(is, new TypeReference<List<Question>>() {});
                Collections.shuffle(questions); // Mélange l'ordre des questions
                repo.saveAll(questions);
                System.out.println("[Questions] " + questions.size() + " questions chargées (ordre aléatoire)");
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement questions : " + e.getMessage());
        }
    }

    public List<Question> getQuestions() {
        return repo.findAll();
    }

    public Optional<Question> getQuestionById(int id) {
        return repo.findById(id);
    }
}
