package de.aservo.atlassian.bitbucket.confapi.rest;

import org.apache.commons.lang3.StringUtils;

import javax.validation.*;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RestResource {

    protected Response buildErrorResponse(Exception e) {
        return buildErrorResponse(e.getMessage());
    }

    protected Response buildErrorResponse(String message) {
        return Response.status(Response.Status.BAD_REQUEST).entity((new ErrorMessage(message))).build();
    }

    protected void validateInputs(Object bean) throws ValidationException {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<Object>> violations = validator.validate(bean);
        if (!violations.isEmpty()) {
            List<String> collect = violations.stream().map(v -> {
                return v.getPropertyPath() + ": " + v.getMessage();
            }).collect(Collectors.toList());
            throw new ValidationException(StringUtils.join(collect, "\n"));
        }
    }
}
