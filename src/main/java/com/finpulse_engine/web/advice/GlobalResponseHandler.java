package com.finpulse_engine.web.advice;


import com.finpulse_engine.dto.response.ApiResponseDto;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice<Object>{
    /**
     * Should this advice be applied?
     *
     * @param returnType    the return type
     * @param converterType the selected converter type
     * @return {@code true} if {@link #beforeBodyWrite} should be invoked;
     * {@code false} otherwise
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // We don't want to wrap our own ApiResponse class.
        // Also, we want to avoid wrapping responses for specific Spring Boot Actuator endpoints or other internal APIs.
        return !returnType.getParameterType().equals(ApiResponseDto.class);
    }


    /**
     * The main logic to wrap the response.
     *
     * @param body                  the body to be written
     * @param returnType            the return type of the controller method
     * @param selectedContentType the content type selected through content negotiation
     * @param selectedConverterType the converter type selected to write to the response
     * @param request               the current request
     * @param response              the current response
     * @return the final body to be written to the response
     */
    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        // If the body is already an ApiResponse, we don't need to wrap it again.
        if (body instanceof ApiResponseDto) {
            return body;
        }
        // Wrap the original response body in our ApiResponse.
        return ApiResponseDto.builder()
                .respId("")
                .data(body)
                .build();
    }
}

