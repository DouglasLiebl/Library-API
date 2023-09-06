package io.github.douglasliebl.libraryapi.api.exception;

import lombok.Getter;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
public class ErrorDetails {

    private final List<String> errors;

    public ErrorDetails(BindingResult bindingResult) {
        this.errors = new ArrayList<>();
        bindingResult.getAllErrors()
                .forEach(error -> this.errors.add(error.getDefaultMessage()));
    }

    public ErrorDetails(BusinessException e) {
        this.errors = Collections.singletonList(e.getMessage());
    }

}
