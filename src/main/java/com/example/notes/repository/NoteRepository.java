package com.example.notes.repository;

import java.util.List;
import java.util.Optional;
import com.example.notes.model.Note;

public interface NoteRepository {

    List<Note> findAll();

    Optional<Note> findById(Long id);

    Note save(Note note);

    void deleteById(Long id);

    boolean existsById(Long id);

    List<Note> findByTag(String tag);
}