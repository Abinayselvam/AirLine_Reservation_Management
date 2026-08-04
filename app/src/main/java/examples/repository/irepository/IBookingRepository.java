package examples.repository.irepository;
import examples.enums.BookingStatus;
import examples.model.Booking;
import java.util.List;

public interface IBookingRepository {

    boolean save(Booking booking);

    Booking findByPNR(String pnr);

    Booking findByETicket(String eTicket);

    List<Booking> findByUserId(int userId);

    List<Booking> findByContact(String emailOrPhone);

    boolean updateStatus(int bookingId, BookingStatus status);

    boolean updateETicket(int bookingId, String eTicket);
}
