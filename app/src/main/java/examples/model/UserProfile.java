package examples.model;

import examples.enums.CommunicationPreference;
import examples.enums.MealPreference;
import examples.enums.SeatPreference;

public class UserProfile
{
    private int profileId;

    private int userId;

    private MealPreference mealPreference;

    private SeatPreference seatPreference;

    private String specialAssistance;

    private CommunicationPreference communicationPreference;

    private String emergencyName;

    private String emergencyPhone;

    public UserProfile()
    {
    }

    // getters
     public int getProfileId()
     {
         return profileId;
     }
     public int getUserId()
     {
         return userId;
     }
     public String getSpecialAssistance()
     {
         return specialAssistance;
     }
     public String getEmergencyName()
     {
         return emergencyName;
     }
     public String getEmergencyPhone()
     {
         return emergencyPhone;
     }
     public MealPreference getMealPreference()
     {
         return mealPreference;
     }
     public SeatPreference getSeatPreference()
     {
         return seatPreference;
     }
     public CommunicationPreference getCommunicationPreference()
     {
         return communicationPreference;
     }
    // setters
    public void setUserId(int userId)
    {
        this.userId=userId;
    }
    public void setProfileId(int profileId)
    {
        this.profileId=profileId;
    }
    public void setMealPreference(MealPreference mealPreference)
    {
        this.mealPreference = mealPreference;
    }
    public void setSeatPreference(SeatPreference seatPreference)
    {
        this.seatPreference=seatPreference;
    }
    public void setSpecialAssistance(String assistance)
    {
        this.specialAssistance = assistance;
    }
    public void setCommunicationPreference(CommunicationPreference communicationPreference)
    {
        this.communicationPreference=communicationPreference;
    }
    public void setEmergencyName(String emergencyName)
    {
        this.emergencyName=emergencyName;
    }
    public void setEmergencyPhone(String emergencyPhone)
    {
        this.emergencyPhone=emergencyPhone;
    }

    // toString()

    @Override
    public String toString() {

        return """
                ===============================
                Meal Preference : %s
                Seat Preference : %s
                Special Assistance : %s
                Communication : %s
                Emergency Contact : %s
                Emergency Phone : %s
                ===============================
                """.formatted(
                mealPreference,
                seatPreference,
                specialAssistance,
                communicationPreference,
                emergencyName,
                emergencyPhone
        );
    }
}
