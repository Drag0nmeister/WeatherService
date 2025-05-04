package com.weather.service.adapter.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для получения информации о погоде")
public class WeatherInfoResponseDTO {

    @Schema(description = "Город где определяется погода", example = "Moscow")
    private String city;

    @Schema(description = "Показатель температуры", example = "20.0")
    private BigDecimal temperature;

    @Schema(description = "Единица измерения температуры", example = "CELSIUS")
    private String temperatureUnit;

    @Schema(description = "Дата и время получения информации", example = "2022-01-01T00:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private ZonedDateTime timestamp;
}
