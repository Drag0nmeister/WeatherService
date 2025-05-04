package com.weather.service.adapter.persistence;

import com.weather.service.app.api.repository.CityRepository;
import com.weather.service.domain.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityJpaRepository extends CityRepository, JpaRepository<City, Long> {
}
