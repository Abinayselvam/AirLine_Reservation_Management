package examples.operations;

import examples.service.CheckInService;
import examples.service.iservice.ICheckInService;

public class CheckInMenu {

    public static void start() {

        ICheckInService service = new CheckInService();

        service.checkIn();
    }
}