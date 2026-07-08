package com.example.notes.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import com.example.notes.model.Note;
import com.example.notes.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Реализация сервиса для работы с заметками.
 * Содержит всю бизнес-логику и валидацию.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;

    @Override
    public List<Note> getAllNotes(String tag) {
        log.debug("Вызов метода getAllNotes с тегом: {}", tag);

        if (tag != null && !tag.trim().isEmpty()) {
            log.debug("Фильтрация заметок по тегу: {}", tag);
            return noteRepository.findByTag(tag);
        }

        return noteRepository.findAll();
    }

    @Override
    public Note getNoteById(Long id) {
        log.debug("Поиск заметки с ID: {}", id);

        return noteRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Заметка с ID {} не найдена в getNoteById()", id);
                    return new RuntimeException("Заметка с ID " + id + " не найдена");
                });
    }

    @Override
    public Note createNote(Note note) {
        log.debug("Создание новой заметки: {}", note.getTitle());

        // Валидация
        validateNote(note);

        // Установка даты создания
        note.setCreatedAt(LocalDateTime.now());

        // Сохранение
        Note savedNote = noteRepository.save(note);
        log.debug("Заметка успешно создана с ID: {}", savedNote.getId());

        return savedNote;
    }

    @Override
    public Note updateNote(Long id, Note note) {
        log.debug("Обновление заметки с ID: {}", id);

        // Проверка существования заметки
        if (!noteRepository.existsById(id)) {
            log.warn("Заметка с ID {} не найдена для обновления", id);
            throw new RuntimeException("Заметка с ID " + id + " не найдена");
        }

        // Валидация новых данных
        validateNote(note);

        // Получение существующей заметки с использованием orElseThrow
        Note existingNote = noteRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Заметка с ID {} не найдена в updateNote", id);
                    return new RuntimeException("Заметка с ID " + id + " не найдена");
                });

        // Обновление полей
        existingNote.setTitle(note.getTitle());
        existingNote.setContent(note.getContent());
        existingNote.setTags(note.getTags());

        // Сохранение обновленной заметки
        Note updatedNote = noteRepository.save(existingNote);
        log.debug("Заметка с ID {} успешно обновлена", id);

        return updatedNote;
    }

    @Override
    public void deleteNote(Long id) {
        log.debug("Удаление заметки с ID: {}", id);

        // Проверка существования заметки
        if (!noteRepository.existsById(id)) {
            log.warn("Заметка с ID {} не найдена для удаления", id);
            throw new RuntimeException("Заметка с ID " + id + " не найдена");
        }

        // Удаление
        noteRepository.deleteById(id);
        log.debug("Заметка с ID {} успешно удалена", id);
    }

    /**
     * Валидация данных заметки.
     *
     * @param note заметка для валидации
     * @throws IllegalArgumentException если данные невалидны
     */
    private void validateNote(Note note) {
        log.debug("Валидация заметки: {}", note.getTitle());

        // Проверка заголовка
        if (note.getTitle() == null || note.getTitle().trim().isEmpty()) {
            log.warn("Ошибка валидации: пустой заголовок");
            throw new IllegalArgumentException("Заголовок не может быть пустым");
        }

        if (note.getTitle().length() > 100) {
            log.warn("Ошибка валидации: заголовок слишком длинный ({} символов)",
                    note.getTitle().length());
            throw new IllegalArgumentException("Заголовок не может превышать 100 символов");
        }

        // Проверка содержимого
        if (note.getContent() == null || note.getContent().trim().isEmpty()) {
            log.warn("Ошибка валидации: пустое содержимое");
            throw new IllegalArgumentException("Содержимое не может быть пустым");
        }

        if (note.getContent().length() > 1000) {
            log.warn("Ошибка валидации: содержимое слишком длинное ({} символов)",
                    note.getContent().length());
            throw new IllegalArgumentException("Содержимое не может превышать 1000 символов");
        }

        // Проверка количества тегов
        if (note.getTags() != null && note.getTags().size() > 10) {
            log.warn("Ошибка валидации: слишком много тегов ({})", note.getTags().size());
            throw new IllegalArgumentException("Максимальное количество тегов - 10");
        }

        log.debug("Валидация заметки успешно пройдена");
    }
}