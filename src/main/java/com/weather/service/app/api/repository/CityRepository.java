package com.weather.service.app.api.repository;

import com.weather.service.domain.City;

import java.util.List;
import java.util.Optional;

public interface CityRepository {

    Optional<City> findByNameAndCountry(String name, String country);

    List<City> findAll();
}
