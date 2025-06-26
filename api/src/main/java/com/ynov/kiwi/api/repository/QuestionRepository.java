package com.ynov.kiwi.api.repository;

import com.ynov.kiwi.api.entity.Question;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class QuestionRepository {
    private final List<Question> questions = new ArrayList<>();

    public List<Question> findAll() {
        return questions;
    }

    public Optional<Question> findById(int id) {
        return questions.stream().filter(q -> q.getId() == id).findFirst();
    }

    public void saveAll(List<Question> list) {
        questions.clear();
        questions.addAll(list);
    }
}
