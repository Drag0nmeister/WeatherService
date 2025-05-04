package com.weather.service.adapter.rest;

import com.weather.service.BaseDbTest;
import com.weather.service.adapter.rest.dto.WeatherInfoResponseDTO;
import com.weather.service.app.api.GetWeatherDataInbound;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class WeatherControllerIntegrationTest extends BaseDbTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetWeatherDataInbound getWeatherDataInbound;

    @Test
    void shouldReturnWeatherData() throws Exception {
        String city = "Moscow";
        String country = "Russia";
        LocalDate date = LocalDate.now();
        String timezone = "Europe/Moscow";

        WeatherInfoResponseDTO weatherInfo = new WeatherInfoResponseDTO();

        when(getWeatherDataInbound.execute(any(), any(), any(), any()))
                .thenReturn(List.of(weatherInfo));

        mockMvc.perform(get("/api/weather")
                        .param("city", city)
                        .param("country", country)
                        .param("date", date.toString())
                        .param("timezone", timezone))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void shouldReturnBadRequestWhenRequiredParamsMissing() throws Exception {
        mockMvc.perform(get("/api/weather"))
                .andExpect(status().isBadRequest());
    }
}
