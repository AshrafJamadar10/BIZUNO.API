package com.bizuno.repositories.business;

import com.bizuno.constants.RegexPatterns;
import com.bizuno.models.business.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    boolean existsByNameAndCategoryIdNot(@NotBlank(message = "Name is required") @Size(max = 100, message = "Name must not exceed 100 characters") @Pattern(regexp = RegexPatterns.REGEX_LETTERS_NUMBERS_AND_SPACES, message = "Name should contain only letters, numbers, and spaces") String name, UUID categoryId);

    boolean existsByName(@NotBlank(message = "Name is required") @Size(max = 100, message = "Name must not exceed 100 characters") @Pattern(regexp = RegexPatterns.REGEX_LETTERS_NUMBERS_AND_SPACES, message = "Name should contain only letters, numbers, and spaces") String name);
}
