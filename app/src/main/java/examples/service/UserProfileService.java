package examples.service;

import examples.enums.CommunicationPreference;
import examples.enums.MealPreference;
import examples.enums.SeatPreference;
import examples.model.User;
import examples.model.UserProfile;
import examples.repository.irepository.IUserProfileRepository;
import examples.repository.UserProfileRepository;
import examples.service.iservice.IUserProfileService;
import examples.util.EnumUtil;
import examples.util.SessionManager;

import java.util.Scanner;

public class UserProfileService implements IUserProfileService {
    private final Scanner scanner =
            new Scanner(System.in);

    private final IUserProfileRepository repository =
            new UserProfileRepository();

    @Override
    public void addProfile() {

        User user = SessionManager.getLoggedInUser();

        if (user == null) {

            System.out.println("Please login first.");
            return;
        }

        UserProfile profile = new UserProfile();

        profile.setUserId(user.getId());

        System.out.print("Meal Preference : ");
        profile.setMealPreference(
                EnumUtil.chooseEnum(MealPreference.class, scanner));

        System.out.print("Seat Preference : ");
        profile.setSeatPreference(
                EnumUtil.chooseEnum(SeatPreference.class, scanner));

        System.out.print("Special Assistance : ");
        profile.setSpecialAssistance(scanner.nextLine());

        System.out.print("Communication Preference : ");
        profile.setCommunicationPreference(
                EnumUtil.chooseEnum(CommunicationPreference.class, scanner));

        System.out.print("Emergency Contact Name : ");
        profile.setEmergencyName(scanner.nextLine());

        System.out.print("Emergency Contact Phone : ");
        profile.setEmergencyPhone(scanner.nextLine());

        if (repository.save(profile)) {

            System.out.println("Profile Saved Successfully.");

        } else {

            System.out.println("Profile Save Failed.");
        }
    }

    @Override
    public void viewProfile() {

        User user = SessionManager.getLoggedInUser();

        if (user == null) {

            System.out.println("Please login first.");
            return;
        }

        UserProfile profile =
                repository.findByUserId(user.getId());

        if (profile == null) {

            System.out.println("Profile Not Found.");
            return;
        }

        System.out.println(profile);
    }

    @Override
    public void updateProfile() {

        User user = SessionManager.getLoggedInUser();

        if (user == null) {

            System.out.println("Please login first.");
            return;
        }

        UserProfile profile =
                repository.findByUserId(user.getId());

        if (profile == null) {

            System.out.println("Profile Not Found.");
            return;
        }

        System.out.print("Meal Preference : ");
        profile.setMealPreference(
                EnumUtil.chooseEnum(MealPreference.class, scanner));


        System.out.print("Seat Preference : ");
        profile.setSeatPreference(
                EnumUtil.chooseEnum(SeatPreference.class, scanner));


        System.out.print("Special Assistance : ");
        profile.setSpecialAssistance(scanner.nextLine());

        System.out.print("Communication Preference : ");
        profile.setCommunicationPreference(
                EnumUtil.chooseEnum(CommunicationPreference.class, scanner));


        System.out.print("Emergency Contact Name : ");
        profile.setEmergencyName(scanner.nextLine());

        System.out.print("Emergency Contact Phone : ");
        profile.setEmergencyPhone(scanner.nextLine());

        if (repository.update(profile)) {

            System.out.println("Profile Updated Successfully.");

        } else {

            System.out.println("Update Failed.");
        }
    }
}
