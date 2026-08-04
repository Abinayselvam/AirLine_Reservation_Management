package examples.service;

import examples.enums.Permission;
import examples.model.Airport;
import examples.repository.AirportRepository;
import examples.repository.irepository.IAirportRepository;
import examples.service.iservice.IAirportService;
import examples.util.AccessValidator;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class AirportService implements IAirportService {

    private final Scanner sc = new Scanner(System.in);

    private final IAirportRepository repository = new AirportRepository();

    @Override
    public void addAirport() {

        if (!AccessValidator.validate(Permission.MANAGE_AIRPORTS)) return;

        Airport airport = new Airport();

        System.out.print("IATA/ICAO Code : ");
        airport.setCode(sc.nextLine().toUpperCase());

        if (repository.findByCode(airport.getCode()) != null) {

            System.out.println("An airport with this code already exists");

            return;
        }

        System.out.print("Name : ");
        airport.setName(sc.nextLine());

        System.out.print("City : ");
        airport.setCity(sc.nextLine());

        System.out.print("Country : ");
        airport.setCountry(sc.nextLine());

        System.out.print("Timezone (e.g. Asia/Kolkata) : ");
        airport.setTimezone(sc.nextLine());

        System.out.print("Terminals (comma separated, e.g. T1,T2) : ");
        airport.setTerminals(splitList(sc.nextLine()));

        System.out.print("Facilities (comma separated, e.g. WiFi,Lounge,Parking) : ");
        airport.setFacilities(splitList(sc.nextLine()));

        System.out.print("Contact Phone : ");
        airport.setContactPhone(sc.nextLine());

        System.out.print("Contact Email : ");
        airport.setContactEmail(sc.nextLine());

        boolean saved = repository.save(airport);

        System.out.println(saved ? "Airport added successfully" : "Failed to add airport");
    }

    @Override
    public void updateAirport() {

        if (!AccessValidator.validate(Permission.MANAGE_AIRPORTS)) return;

        System.out.print("Airport Code to Update : ");

        Airport airport = repository.findByCode(sc.nextLine().toUpperCase());

        if (airport == null) {

            System.out.println("Airport not found");

            return;
        }

        System.out.print("Name (blank to keep '" + airport.getName() + "') : ");
        String name = sc.nextLine();
        if (!name.isBlank()) airport.setName(name);

        System.out.print("City (blank to keep '" + airport.getCity() + "') : ");
        String city = sc.nextLine();
        if (!city.isBlank()) airport.setCity(city);

        System.out.print("Country (blank to keep '" + airport.getCountry() + "') : ");
        String country = sc.nextLine();
        if (!country.isBlank()) airport.setCountry(country);

        System.out.print("Timezone (blank to keep '" + airport.getTimezone() + "') : ");
        String tz = sc.nextLine();
        if (!tz.isBlank()) airport.setTimezone(tz);

        System.out.print("Terminals (comma separated, blank to keep) : ");
        String terminals = sc.nextLine();
        if (!terminals.isBlank()) airport.setTerminals(splitList(terminals));

        System.out.print("Facilities (comma separated, blank to keep) : ");
        String facilities = sc.nextLine();
        if (!facilities.isBlank()) airport.setFacilities(splitList(facilities));

        System.out.print("Contact Phone (blank to keep) : ");
        String phone = sc.nextLine();
        if (!phone.isBlank()) airport.setContactPhone(phone);

        System.out.print("Contact Email (blank to keep) : ");
        String email = sc.nextLine();
        if (!email.isBlank()) airport.setContactEmail(email);

        boolean updated = repository.update(airport);

        System.out.println(updated ? "Airport updated" : "Update failed");
    }

    @Override
    public void toggleActive() {

        if (!AccessValidator.validate(Permission.MANAGE_AIRPORTS)) return;

        System.out.print("Airport Code : ");

        String code = sc.nextLine().toUpperCase();

        Airport airport = repository.findByCode(code);

        if (airport == null) {

            System.out.println("Airport not found");

            return;
        }

        boolean newStatus = !airport.isActive();

        repository.setActive(code, newStatus);

        System.out.println("Airport " + code + " marked as " + (newStatus ? "Active" : "Inactive"));
    }

    @Override
    public void searchByCode() {

        System.out.print("Airport Code : ");

        Airport airport = repository.findByCode(sc.nextLine().toUpperCase());

        if (airport == null) {

            System.out.println("No airport found with that code");

            return;
        }

        printDetails(airport);
    }

    @Override
    public void searchByCity() {

        System.out.print("City : ");

        List<Airport> results = repository.findByCity(sc.nextLine());

        printResults(results);
    }

    @Override
    public void searchByName() {

        System.out.print("Airport Name : ");

        List<Airport> results = repository.findByName(sc.nextLine());

        printResults(results);
    }

    @Override
    public void listByCountry() {

        System.out.print("Country : ");

        List<Airport> results = repository.findByCountry(sc.nextLine());

        printResults(results);
    }

    private void printResults(List<Airport> airports) {

        if (airports.isEmpty()) {

            System.out.println("No airports found");

            return;
        }

        airports.forEach(this::printDetails);
    }

    private void printDetails(Airport a) {

        System.out.println("\n" + a.getCode() + " - " + a.getName());
        System.out.println("  " + a.getCity() + ", " + a.getCountry() + " | " + a.getTimezone());
        System.out.println("  Terminals : " + String.join(", ", a.getTerminals()));
        System.out.println("  Facilities : " + String.join(", ", a.getFacilities()));
        System.out.println("  Contact : " + a.getContactPhone() + " / " + a.getContactEmail());
        System.out.println("  Status : " + (a.isActive() ? "Active" : "Inactive"));
    }

    private List<String> splitList(String input) {

        return Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}