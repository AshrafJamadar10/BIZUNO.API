package com.bizuno.utils;

import com.bizuno.exception.ServiceException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

public class ValidationUtil {
    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    public static <T> void validate(T object) throws ServiceException {
        try {
            if (object == null) {
                throw new ServiceException("All Fields are required");
            }

            Set<ConstraintViolation<T>> violations = validator.validate(object);

            if (!violations.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (ConstraintViolation<T> violation : violations) {
                    sb.append(violation.getMessage())
                            .append(", ");
                }
                throw new ServiceException(sb.toString());
            }
        } catch (NullPointerException e){
            throw new ServiceException("All Fields are required");
        }
    }

    public static boolean isValidData(String data) {
        return data != null && !data.trim().isEmpty();
    }
}
