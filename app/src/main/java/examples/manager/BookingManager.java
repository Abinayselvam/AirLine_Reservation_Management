package examples.manager;

import examples.enums.BookingStatus;
import examples.model.Booking;
import examples.repository.BookingRepository;
import examples.repository.irepository.IBookingRepository;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BookingManager {

    private static volatile BookingManager instance;

    private final IBookingRepository repository = new BookingRepository();

    private final Map<String, Booking> bookingCache = new ConcurrentHashMap<>();

    // Guards against the same user double-submitting for the same flight
    // before the first request has finished writing to the DB.
    private final Map<String, Boolean> inProgress = new ConcurrentHashMap<>();

    private BookingManager() {}

    public static BookingManager getInstance() {

        if (instance == null) {

            synchronized (BookingManager.class) {

                if (instance == null) {
                    instance = new BookingManager();
                }
            }
        }

        return instance;
    }

    public boolean tryLockBookingAttempt(int userId, int flightId) {

        String key = userId + ":" + flightId;

        return inProgress.putIfAbsent(key, Boolean.TRUE) == null; // true = lock acquired
    }

    public void releaseBookingAttempt(int userId, int flightId) {
        inProgress.remove(userId + ":" + flightId);
    }

    public boolean saveBooking(Booking booking) {

        boolean saved = repository.save(booking);

        if (saved) {
            bookingCache.put(booking.getPnr(), booking);
        }

        return saved;
    }

    public Booking getBookingByPNR(String pnr) {
        return bookingCache.computeIfAbsent(pnr, repository::findByPNR);
    }

    public boolean updateStatus(int bookingId, String pnr, BookingStatus status) {

        boolean updated = repository.updateStatus(bookingId, status);

        if (updated) {

            Booking cached = bookingCache.get(pnr);

            if (cached != null) {
                cached.setStatus(status);
            }
        }

        return updated;
    }

    public void invalidate(String pnr) {
        bookingCache.remove(pnr);
    }

    public int cacheSize() {
        return bookingCache.size();
    }
}