package com.example.notes.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.notes.model.Note;
import com.example.notes.repository.NoteRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoteServiceImplTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteServiceImpl noteService;

    private Note testNote;

    @BeforeEach
    void setUp() {
        testNote = new Note();
        testNote.setId(1L);
        testNote.setTitle("Test Note");
        testNote.setContent("Test Content");
        testNote.addTag("work");
        testNote.addTag("important");
    }

    @Test
    void createNote_ShouldReturnCreatedNote_WhenValid() {
        // Arrange
        when(noteRepository.save(any(Note.class))).thenReturn(testNote);

        // Act
        Note created = noteService.createNote(testNote);

        // Assert
        assertNotNull(created);
        assertEquals("Test Note", created.getTitle());
        verify(noteRepository, times(1)).save(any(Note.class));
    }

    @Test
    void createNote_ShouldThrowException_WhenTitleIsEmpty() {
        // Arrange
        testNote.setTitle("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> noteService.createNote(testNote));
        verify(noteRepository, never()).save(any(Note.class));
    }

    @Test
    void getNoteById_ShouldReturnNote_WhenExists() {
        // Arrange
        when(noteRepository.findById(1L)).thenReturn(Optional.of(testNote));

        // Act
        Note found = noteService.getNoteById(1L);

        // Assert
        assertNotNull(found);
        assertEquals(1L, found.getId());
        verify(noteRepository, times(1)).findById(1L);
    }

    @Test
    void getAllNotes_ShouldFilterByTag_WhenTagProvided() {
        // Arrange
        List<Note> expectedNotes = Collections.singletonList(testNote);
        when(noteRepository.findByTag("work")).thenReturn(expectedNotes);

        // Act
        List<Note> result = noteService.getAllNotes("work");

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(0).getTags().contains("work"));
        verify(noteRepository, times(1)).findByTag("work");
    }

    @Test
    void deleteNote_ShouldDelete_WhenExists() {
        // Arrange
        when(noteRepository.existsById(1L)).thenReturn(true);

        // Act
        noteService.deleteNote(1L);

        // Assert
        verify(noteRepository, times(1)).deleteById(1L);
    }

    @Test
    void updateNote_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(noteRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> noteService.updateNote(999L, testNote));
        verify(noteRepository, never()).save(any(Note.class));
    }

    @Test
    void getAllNotes_ShouldReturnAllNotes_WhenTagIsNull() {
        List<Note> expectedNotes = Collections.singletonList(testNote);
        when(noteRepository.findAll()).thenReturn(expectedNotes);

        List<Note> result = noteService.getAllNotes(null);

        assertEquals(1, result.size());
        verify(noteRepository, times(1)).findAll();
        verify(noteRepository, never()).findByTag(any());
    }

    @Test
    void getNoteById_ShouldThrowException_WhenNotFound() {
        when(noteRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> noteService.getNoteById(999L));
        verify(noteRepository, times(1)).findById(999L);
    }

    @Test
    void updateNote_ShouldUpdate_WhenValid() {
        Note updatedNote = new Note();
        updatedNote.setTitle("Updated Title");
        updatedNote.setContent("Updated Content");

        when(noteRepository.existsById(1L)).thenReturn(true);
        when(noteRepository.findById(1L)).thenReturn(Optional.of(testNote));
        when(noteRepository.save(any(Note.class))).thenReturn(testNote);

        Note result = noteService.updateNote(1L, updatedNote);

        assertNotNull(result);
        verify(noteRepository, times(1)).save(any(Note.class));
    }
}