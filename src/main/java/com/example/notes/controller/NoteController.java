package com.example.notes.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.notes.model.Note;
import com.example.notes.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST контроллер для управления заметками.
 * Контроллер максимально тонкий - вся бизнес-логика вынесена в сервисный слой.
 */
@Slf4j
@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
@Tag(name = "Управление заметками", description = "CRUD операции для работы с заметками")
public class NoteController {

    private final NoteService noteService;

    /**
     * Получить все заметки или отфильтровать по тегу.
     *
     * @param tag опциональный параметр для фильтрации по тегу
     * @return список заметок
     */
    @Operation(
            summary = "Получить все заметки",
            description = "Возвращает список всех заметок. Можно отфильтровать по тегу."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное получение списка заметок",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Note.class),
                            examples = @ExampleObject(
                                    value = "[{\"id\":1,\"title\":\"Работа\",\"content\":\"Собрание в 15:00\",\"createdAt\":\"2026-07-08T02:07:13.360005672\",\"tags\":[\"work\"]}]"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера"
            )
    })
    @GetMapping
    public ResponseEntity<List<Note>> getAllNotes(
            @Parameter(
                    description = "Тег для фильтрации заметок",
                    example = "work"
            )
            @RequestParam(required = false) String tag
    ) {
        log.info("Запрос на получение заметок с фильтром по тегу: {}", tag);

        List<Note> notes = noteService.getAllNotes(tag);

        log.info("Успешно получено {} заметок", notes.size());
        return ResponseEntity.ok(notes);
    }

    /**
     * Получить заметку по ID.
     *
     * @param id идентификатор заметки
     * @return найденная заметка или статус 404
     */
    @Operation(
            summary = "Получить заметку по ID",
            description = "Возвращает заметку по указанному идентификатору"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Заметка найдена",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Note.class),
                            examples = @ExampleObject(
                                    value = "{\"id\":1,\"title\":\"Работа\",\"content\":\"Собрание в 15:00\",\"createdAt\":\"2026-07-08T02:07:13.360005672\",\"tags\":[\"work\"]}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Заметка не найдена",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\"timestamp\":\"2026-07-08 02:07:13\",\"status\":404,\"error\":\"Not Found\",\"message\":\"Заметка с ID 999 не найдена\"}"
                            )
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(
            @Parameter(
                    description = "Идентификатор заметки",
                    example = "1",
                    required = true
            )
            @PathVariable Long id
    ) {
        log.info("Запрос на получение заметки с ID: {}", id);

        try {
            Note note = noteService.getNoteById(id);
            log.info("Заметка с ID {} успешно найдена", id);
            return ResponseEntity.ok(note);
        } catch (RuntimeException e) {
            log.warn("Заметка с ID {} не найдена: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Создать новую заметку.
     *
     * @param note данные для создания заметки
     * @return созданная заметка со статусом 201 Created
     */
    @Operation(
            summary = "Создать новую заметку",
            description = "Создает новую заметку с переданными данными"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Заметка успешно создана",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Note.class),
                            examples = @ExampleObject(
                                    value = "{\"id\":1,\"title\":\"Новая заметка\",\"content\":\"Содержимое заметки\",\"createdAt\":\"2026-07-08T02:07:13.360005672\",\"tags\":[\"work\",\"test\"]}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации данных",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\"timestamp\":\"2026-07-08 02:07:13\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"Заголовок не может быть пустым\"}"
                            )
                    )
            )
    })
    @PostMapping
    public ResponseEntity<Note> createNote(
            @Parameter(
                    description = "Данные для создания заметки",
                    required = true,
                    schema = @Schema(implementation = Note.class),
                    examples = @ExampleObject(
                            value = "{\"title\":\"Новая заметка\",\"content\":\"Содержимое заметки\",\"tags\":[\"work\",\"test\"]}"
                    )
            )
            @RequestBody Note note
    ) {
        log.info("Запрос на создание новой заметки с заголовком: {}",
                note.getTitle() != null ? note.getTitle() : "null");

        try {
            Note createdNote = noteService.createNote(note);
            log.info("Заметка успешно создана с ID: {}", createdNote.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdNote);
        } catch (IllegalArgumentException e) {
            log.warn("Ошибка валидации при создании заметки: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Обновить существующую заметку.
     *
     * @param id   идентификатор заметки для обновления
     * @param note новые данные заметки
     * @return обновленная заметка или статус ошибки
     */
    @Operation(
            summary = "Обновить заметку",
            description = "Обновляет существующую заметку по ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Заметка успешно обновлена",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Note.class),
                            examples = @ExampleObject(
                                    value = "{\"id\":1,\"title\":\"Обновленная заметка\",\"content\":\"Новое содержимое\",\"createdAt\":\"2026-07-08T02:07:13.360005672\",\"tags\":[\"work\",\"updated\"]}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Заметка не найдена"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации данных"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(
            @Parameter(
                    description = "Идентификатор заметки для обновления",
                    example = "1",
                    required = true
            )
            @PathVariable Long id,
            @Parameter(
                    description = "Новые данные для заметки",
                    required = true,
                    schema = @Schema(implementation = Note.class),
                    examples = @ExampleObject(
                            value = "{\"title\":\"Обновленная заметка\",\"content\":\"Новое содержимое\",\"tags\":[\"work\",\"updated\"]}"
                    )
            )
            @RequestBody Note note
    ) {
        log.info("Запрос на обновление заметки с ID: {}", id);

        try {
            Note updatedNote = noteService.updateNote(id, note);
            log.info("Заметка с ID {} успешно обновлена", id);
            return ResponseEntity.ok(updatedNote);
        } catch (IllegalArgumentException e) {
            log.warn("Ошибка валидации при обновлении заметки с ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                log.warn("Заметка с ID {} не найдена для обновления", id);
                return ResponseEntity.notFound().build();
            }
            log.error("Ошибка при обновлении заметки с ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Удалить заметку по ID.
     *
     * @param id идентификатор заметки для удаления
     * @return статус 204 No Content при успехе или 404 Not Found
     */
    @Operation(
            summary = "Удалить заметку",
            description = "Удаляет заметку по указанному ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Заметка успешно удалена"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Заметка не найдена"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(
            @Parameter(
                    description = "Идентификатор заметки для удаления",
                    example = "1",
                    required = true
            )
            @PathVariable Long id
    ) {
        log.info("Запрос на удаление заметки с ID: {}", id);

        try {
            noteService.deleteNote(id);
            log.info("Заметка с ID {} успешно удалена", id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.warn("Заметка с ID {} не найдена для удаления", id);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Глобальный обработчик исключений RuntimeException.
     *
     * @param e исключение
     * @return сообщение об ошибке со статусом 404
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        log.error("Ошибка выполнения: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Ошибка: " + e.getMessage());
    }

    /**
     * Глобальный обработчик исключений IllegalArgumentException.
     *
     * @param e исключение
     * @return сообщение об ошибке со статусом 400
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("Ошибка валидации: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Ошибка валидации: " + e.getMessage());
    }
}