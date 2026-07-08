package com.example.notes.service;

import java.util.List;
import com.example.notes.model.Note;

/**
 * Интерфейс сервиса для работы с заметками.
 * Определяет контракт для бизнес-логики.
 */
public interface NoteService {

    /**
     * Получить все заметки или отфильтровать по тегу.
     *
     * @param tag тег для фильтрации (опционально)
     * @return список заметок
     */
    List<Note> getAllNotes(String tag);

    /**
     * Получить заметку по ID.
     *
     * @param id идентификатор заметки
     * @return найденная заметка
     * @throws RuntimeException если заметка не найдена
     */
    Note getNoteById(Long id);

    /**
     * Создать новую заметку.
     *
     * @param note данные для создания
     * @return созданная заметка
     * @throws IllegalArgumentException если данные невалидны
     */
    Note createNote(Note note);

    /**
     * Обновить существующую заметку.
     *
     * @param id   идентификатор заметки
     * @param note новые данные
     * @return обновленная заметка
     * @throws RuntimeException         если заметка не найдена
     * @throws IllegalArgumentException если данные невалидны
     */
    Note updateNote(Long id, Note note);

    /**
     * Удалить заметку по ID.
     *
     * @param id идентификатор заметки
     * @throws RuntimeException если заметка не найдена
     */
    void deleteNote(Long id);
}