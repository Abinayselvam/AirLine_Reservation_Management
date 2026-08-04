package examples.repository.irepository;
import examples.enums.SeatStatus;
import examples.model.Seat;

import java.util.List;

public interface ISeatRepository {

    List<Seat> findByFlightId(int flightId);

    boolean saveAll(List<Seat> seats);

    boolean updateStatus(int seatId, SeatStatus status);

    Seat findBySeatNumber(int flightId, String seatNumber);
}
