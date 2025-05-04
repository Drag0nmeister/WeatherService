package com.weather.service.adapter.persistence;

import com.weather.service.app.api.repository.WeatherInfoRepository;
import com.weather.service.domain.City;
import com.weather.service.domain.WeatherInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface WeatherInfoJpaRepository extends WeatherInfoRepository, JpaRepository<WeatherInfo, Long> {

    @Query("""
            SELECT w FROM WeatherInfo w
            WHERE w.city = :city
              AND w.timestamp BETWEEN :startOfDay AND :endOfDay
            ORDER BY w.timestamp DESC
            """)
    List<WeatherInfo> findWeatherInfoForCityBetween(@Param("city") City city, @Param("startOfDay") ZonedDateTime startOfDay,
                                                    @Param("endOfDay") ZonedDateTime endOfDay);
}
