package com.back.domain.wiseSaying.service;

import com.back.domain.wiseSaying.entity.WiseSaying;
import com.back.domain.wiseSaying.repository.WiseSayingRepository;

import java.util.List;

public class WiseSayingService {
    private final WiseSayingRepository repo = new WiseSayingRepository();

    public List<WiseSaying> findAll()
    {
        return repo.findAll();
    }
    public WiseSaying findById(int id)
    {
        return repo.findById(id);
    }

    public int create(String text, String author) {
        return repo.save(text, author);
    }
    public void delete(int id) {
        repo.delete(id);
    }
    public void update(int id, String text, String author) {
        repo.update(id, text, author);
    }

    public boolean buildDataJson() {
        return repo.buildDataJson();
    }
}
