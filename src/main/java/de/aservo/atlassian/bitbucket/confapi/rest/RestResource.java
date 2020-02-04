package de.aservo.atlassian.bitbucket.confapi.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import javax.validation.*;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RestResource {

    private ObjectMapper jacksonMapper = new JacksonMapper().getContext(null);

    protected Response buildErrorResponse(Exception e) {
        return buildErrorResponse(e.getMessage());
    }

    protected Response buildErrorResponse(String message) {
        try {
            String errorJson = jacksonMapper.writeValueAsString(new ErrorMessage(message));
            return Response.status(Response.Status.BAD_REQUEST).entity(errorJson).build();
        } catch (JsonProcessingException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.toString()).build();
        }
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

    protected String wrapObject(Object o) throws JsonProcessingException {
        return jacksonMapper.writeValueAsString(o);
    }
}
