package com.example.notes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class NoteRequest {
    
    @NotBlank(message = "Заголовок не может быть пустым")
    @Size(max = 100, message = "Заголовок не может превышать 100 символов")
    private String title;
    
    @NotBlank(message = "Содержимое не может быть пустым")
    @Size(max = 1000, message = "Содержимое не может превышать 1000 символов")
    private String content;
    
    @Size(max = 10, message = "Максимальное количество тегов - 10")
    private Set<String> tags;
}
