package com.weather.service.infrastructure.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO для отображения ошибки в ответе API")
public class ErrorResponse {

    @Schema(description = "Код ошибки")
    private int errorCode;

    @Schema(description = "Сообщение об ошибке для пользователя")
    private String userMessage;

    @Schema(description = "Сообщение об ошибке для разработчика")
    private String developerMessage;
}
