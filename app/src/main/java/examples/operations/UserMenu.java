package examples.operations;

import examples.service.iservice.IUserService;
import examples.service.UserService;
import java.util.Scanner;

public class UserMenu {

    public static void start() {

        Scanner sc =
                new Scanner(System.in);

        IUserService service =
                new UserService();

        while (true) {

            System.out.println();

            System.out.println("===== USER MENU =====");

            System.out.println("1.Register");

            System.out.println("2.Login");

            System.out.println("3.Exit");

            System.out.print("Choice : ");

            int choice =
                    Integer.parseInt(sc.nextLine());

            switch (choice) {

                case 1 -> service.register();

                case 2 -> service.login();

                case 3 -> System.exit(0);

                default ->
                        System.out.println(
                                "Invalid Choice");
            }
        }
    }
}
