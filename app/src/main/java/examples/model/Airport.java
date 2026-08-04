package examples.model;


import java.util.ArrayList;
import java.util.List;

public class Airport {

    private int airportId;
    private String code; // IATA/ICAO
    private String name;
    private String city;
    private String country;
    private String timezone;
    private List<String> terminals = new ArrayList<>();
    private List<String> facilities = new ArrayList<>();
    private String contactPhone;
    private String contactEmail;
    private boolean active = true;

    public int getAirportId() { return airportId; }
    public void setAirportId(int airportId) { this.airportId = airportId; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public List<String> getTerminals() { return terminals; }
    public void setTerminals(List<String> terminals) { this.terminals = terminals; }

    public List<String> getFacilities() { return facilities; }
    public void setFacilities(List<String> facilities) { this.facilities = facilities; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {

        return String.format(
                "[%s] %s - %s, %s | Terminals: %s | %s",
                code, name, city, country,
                String.join(", ", terminals),
                active ? "Active" : "Inactive");
    }
}
