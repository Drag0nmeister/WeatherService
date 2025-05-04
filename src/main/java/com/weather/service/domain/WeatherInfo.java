package com.weather.service.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Table(name = "weather_info")
public class WeatherInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    @ToString.Exclude
    private City city;

    @Column(name = "temperature", nullable = false)
    private BigDecimal temperature;

    @Column(name = "temperature_unit", nullable = false)
    @Enumerated(EnumType.STRING)
    private TemperatureUnit temperatureUnit;

    @Column(name = "provider", nullable = false)
    private String provider;

    @Column(name = "timestamp", nullable = false)
    private ZonedDateTime timestamp;
}
