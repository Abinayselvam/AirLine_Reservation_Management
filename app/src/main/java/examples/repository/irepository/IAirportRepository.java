package examples.repository.irepository;

import examples.model.Airport;

import java.util.List;

public interface IAirportRepository {

    boolean save(Airport airport);

    boolean update(Airport airport);

    boolean setActive(String code, boolean active);

    Airport findByCode(String code);

    List<Airport> findByCity(String city);

    List<Airport> findByName(String name);

    List<Airport> findByCountry(String country);

    List<Airport> findAll();
}