package examples.model;

import examples.enums.MealPreference;

public class BookingPassenger {

    private int passengerBookingId;
    private int bookingId;
    private String name;
    private int age;
    private String gender;
    private String idProof;
    private MealPreference mealPreference;
    private String specialAssistance;
    private String frequentFlyerNumber;
    private String seatNumber;
    private String contactEmail;
    private String contactPhone;
    private boolean cancelled;

    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }

    public int getPassengerBookingId() { return passengerBookingId; }
    public void setPassengerBookingId(int passengerBookingId) { this.passengerBookingId = passengerBookingId; }

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getIdProof() { return idProof; }
    public void setIdProof(String idProof) { this.idProof = idProof; }

    public MealPreference getMealPreference() { return mealPreference; }
    public void setMealPreference(MealPreference mealPreference) { this.mealPreference = mealPreference; }

    public String getSpecialAssistance() { return specialAssistance; }
    public void setSpecialAssistance(String specialAssistance) { this.specialAssistance = specialAssistance; }

    public String getFrequentFlyerNumber() { return frequentFlyerNumber; }
    public void setFrequentFlyerNumber(String frequentFlyerNumber) { this.frequentFlyerNumber = frequentFlyerNumber; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public boolean isInfant() { return age < 2; }
}