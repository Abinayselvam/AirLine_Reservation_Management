package examples.service;

import examples.enums.Role;
import examples.model.Passenger;
import examples.model.User;
import examples.operations.AdminMenu;
import examples.operations.AirlineStaffMenu;
import examples.operations.UserProfileMenu;
import examples.repository.IUserRepository;
import examples.repository.UserRepository;
import examples.util.OTPGenerator;
import examples.util.PasswordUtil;
import examples.util.SessionManager;
import examples.util.ValidationUtil;
import java.util.Scanner;

public class UserService implements IUserService {

    private final Scanner sc =
            new Scanner(System.in);

    private final IUserRepository repository =
            new UserRepository();

    @Override
    public void register() {

        System.out.println("\nPassenger Registration");

        System.out.print("Name : ");

        String name = sc.nextLine();

        System.out.print("Email : ");

        String email = sc.nextLine();

        if (!ValidationUtil.isValidEmail(email)) {

            System.out.println(
                    "Invalid Email");

            return;
        }

        System.out.print("Phone : ");

        String phone = sc.nextLine();

        if (!ValidationUtil.isValidPhone(phone)) {

            System.out.println(
                    "Invalid Phone");

            return;
        }

        System.out.print("DOB (yyyy-MM-dd): ");

        String dob = sc.nextLine();

        System.out.print("Passport : ");

        String passport = sc.nextLine();

        System.out.print("Password : ");

        String password = sc.nextLine();

        int otp =
                OTPGenerator.generateOTP();

        System.out.println(
                "OTP : " + otp);

        System.out.print(
                "Enter OTP : ");

        int enteredOtp =
                Integer.parseInt(sc.nextLine());

        if (otp != enteredOtp) {

            System.out.println(
                    "OTP Verification Failed");

            return;
        }

        Passenger passenger =
                new Passenger();

        passenger.setName(name);

        passenger.setEmail(email);

        passenger.setPhone(phone);

        passenger.setDateOfBirth(dob);

        passenger.setPassportNo(passport);

        passenger.setPassword(
                PasswordUtil.encrypt(password));

        passenger.setRole(Role.AIRLINE_STAFF);

        if (repository.save(passenger)) {

            System.out.println(
                    "Registration Successful");

        } else {

            System.out.println(
                    "Registration Failed");
        }

    }


    @Override
    public void login() {

        System.out.print("Email : ");
        String email = sc.nextLine();

        System.out.print("Password : ");
        String password = sc.nextLine();

        User user = repository.login(email, password);

        if (user == null) {

            System.out.println("Invalid Email or Password");
            return;
        }

        SessionManager.login(user);

        System.out.println("Login Successful");

        switch (user.getRole()) {

            case PASSENGER -> UserProfileMenu.start();

            case ADMIN -> AdminMenu.start();

            case AIRLINE_STAFF -> AirlineStaffMenu.start();
        }
    }

    @Override
    public void updateProfile() {

        User user =
                SessionManager.getLoggedInUser();

        if (user == null) {

            System.out.println(
                    "Login Required");

            return;
        }

        System.out.print("Name : ");

        user.setName(sc.nextLine());

        System.out.print("Phone : ");

        user.setPhone(sc.nextLine());

        System.out.print("DOB : ");

        user.setDateOfBirth(sc.nextLine());

        System.out.print("Passport : ");

        user.setPassportNo(sc.nextLine());

        if (repository.update(user)) {

            System.out.println(
                    "Profile Updated");

        } else {

            System.out.println(
                    "Update Failed");
        }
    }

    @Override
    public void deleteAccount() {

        User user =
                SessionManager.getLoggedInUser();

        if (user == null) {

            System.out.println(
                    "Login Required");

            return;
        }

        if (repository.delete(user.getId())) {

            SessionManager.logout();

            System.out.println(
                    "Account Deactivated");

        } else {

            System.out.println(
                    "Operation Failed");
        }
    }
    @Override
    public void forgotPassword() {

        System.out.print("Enter Email : ");

        String email = sc.nextLine();

        User user =
                repository.findByEmail(email);

        if (user == null) {

            System.out.println(
                    "User Not Found");

            return;
        }

        int otp =
                OTPGenerator.generateOTP();

        System.out.println(
                "OTP : " + otp);

        System.out.print(
                "Enter OTP : ");

        int enteredOtp =
                Integer.parseInt(sc.nextLine());

        if (otp != enteredOtp) {

            System.out.println(
                    "OTP Verification Failed");

            return;
        }

        System.out.print(
                "New Password : ");

        String password =
                sc.nextLine();

        String encrypted =
                PasswordUtil.encrypt(password);

        if (repository.updatePassword(
                email,
                encrypted)) {

            System.out.println(
                    "Password Updated");

        } else {

            System.out.println(
                    "Unable to Update Password");
        }
    }

    @Override
    public void viewProfile() {

        User user =
                SessionManager.getLoggedInUser();

        if (user == null) {

            System.out.println(
                    "Login Required");

            return;
        }

        System.out.println("ID : " +
                user.getId());

        System.out.println("Name : " +
                user.getName());

        System.out.println("Email : " +
                user.getEmail());

        System.out.println("Phone : " +
                user.getPhone());

        System.out.println("Passport : " +
                user.getPassportNo());

        System.out.println("Role : " +
                user.getRole());
    }
}
