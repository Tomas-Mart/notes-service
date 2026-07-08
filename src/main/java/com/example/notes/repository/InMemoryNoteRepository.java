package com.example.notes.repository;

import com.example.notes.model.Note;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryNoteRepository implements NoteRepository {
    private final Map<Long, Note> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public List<Note> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<Note> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Note save(Note note) {
        if (note.getId() == null) {
            note.setId(idGenerator.getAndIncrement());
        }
        storage.put(note.getId(), note);
        return note;
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }

    @Override
    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }

    @Override
    public List<Note> findByTag(String tag) {
        return storage.values().stream()
                .filter(note -> note.getTags().contains(tag.toLowerCase()))
                .collect(Collectors.toList());
    }
}