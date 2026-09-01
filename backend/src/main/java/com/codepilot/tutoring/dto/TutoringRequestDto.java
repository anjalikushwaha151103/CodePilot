package com.codepilot.tutoring.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public class TutoringRequestDto {

    @NotNull(message = "Problem context is required")
    @Valid
    private ProblemContextDto problemContext;

    @NotBlank(message = "Code is required")
    @Size(max = 30000, message = "Code exceeds maximum allowed length of 30,000 characters")
    private String code;

    @NotBlank(message = "Language is required")
    @Pattern(regexp = "^(?i)(python|cpp|java|javascript)$", message = "Unsupported language")
    private String language;

    @Min(value = 0, message = "Hint level must be between 0 and 4")
    @Max(value = 4, message = "Hint level must be between 0 and 4")
    private int hintLevel;

    @Size(max = 2000, message = "User question exceeds maximum allowed length of 2,000 characters")
    private String userQuestion;

    public ProblemContextDto getProblemContext() {
        return problemContext;
    }

    public void setProblemContext(ProblemContextDto problemContext) {
        this.problemContext = problemContext;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getHintLevel() {
        return hintLevel;
    }

    public void setHintLevel(int hintLevel) {
        this.hintLevel = hintLevel;
    }

    public String getUserQuestion() {
        return userQuestion;
    }

    public void setUserQuestion(String userQuestion) {
        this.userQuestion = userQuestion;
    }
}
