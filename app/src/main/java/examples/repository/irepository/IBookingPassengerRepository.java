package examples.repository.irepository;

import examples.model.BookingPassenger;

import java.util.List;

public interface IBookingPassengerRepository {

    boolean saveAll(List<BookingPassenger> passengers);

    List<BookingPassenger> findByBookingId(int bookingId);
}