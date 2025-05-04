package com.weather.service.adapter.rest;

import com.weather.service.adapter.rest.dto.WeatherInfoResponseDTO;
import com.weather.service.app.api.GetWeatherDataInbound;
import com.weather.service.infrastructure.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
@Tag(name = "Weather API", description = "API для предоставления информации о температуре в городах")
public class WeatherController {

    private final GetWeatherDataInbound getWeatherDataInbound;

    @GetMapping
    @Operation(summary = "Получение погодных данных", description = "Эндпоинт для получения информации о температуре воздуха в разных городах за указанную дату.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение информации о температуре воздуха", content = @Content(schema = @Schema(implementation = WeatherInfoResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Город не найден", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    public ResponseEntity<List<WeatherInfoResponseDTO>> getWeatherData(@RequestParam String city, @RequestParam String country,
                                                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                                                       @RequestParam(required = false, defaultValue = "Europe/Moscow") String timezone) {
        List<WeatherInfoResponseDTO> weatherData = getWeatherDataInbound.execute(city, country, date,timezone);
        return ResponseEntity.ok(weatherData);
    }
}
