package examples.service.iservice;

import examples.enums.BookingPriority;
import examples.model.BookingRequest;

public interface IPriorityBookingQueueService {

    BookingRequest submit(int userId, int flightId, int passengerCount, BookingPriority priority);

    BookingRequest processNext();

    int queueSize();

    void printReport();
}