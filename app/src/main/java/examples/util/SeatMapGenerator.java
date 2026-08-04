package examples.util;

import examples.enums.SeatCategory;
import examples.enums.SeatStatus;
import examples.enums.SeatType;
import examples.model.Seat;
import java.util.ArrayList;
import java.util.List;

public final class SeatMapGenerator {

    private static final char[] COLUMNS = {'A', 'B', 'C', 'D', 'E', 'F'};

    private static final int EXIT_ROW = 12;

    private static final int PREMIUM_ROWS = 2;

    private SeatMapGenerator() {}

    public static List<Seat> generate(int flightId, int totalSeats) {

        List<Seat> seats = new ArrayList<>();

        int seatsPerRow = COLUMNS.length;

        int totalRows = (int) Math.ceil((double) totalSeats / seatsPerRow);

        int count = 0;

        for (int row = 1; row <= totalRows && count < totalSeats; row++) {

            for (char col : COLUMNS) {

                if (count >= totalSeats) break;

                Seat seat = new Seat();

                seat.setFlightId(flightId);
                seat.setSeatNumber(row + String.valueOf(col));
                seat.setSeatType(seatType(col));
                seat.setStatus(SeatStatus.AVAILABLE);

                if (row == EXIT_ROW) {

                    seat.setCategory(SeatCategory.EMERGENCY_EXIT);
                    seat.setExtraLegroom(true);
                    seat.setExtraCharge(0);

                } else if (row <= PREMIUM_ROWS) {

                    seat.setCategory(SeatCategory.PREMIUM);
                    seat.setExtraCharge(1500);
                    seat.setPowerOutlet(true);

                } else {

                    seat.setCategory(SeatCategory.STANDARD);
                    seat.setExtraCharge(0);
                }

                seats.add(seat);

                count++;
            }
        }

        return seats;
    }

    private static SeatType seatType(char col) {

        return switch (col) {
            case 'A', 'F' -> SeatType.WINDOW;
            case 'C', 'D' -> SeatType.AISLE;
            default -> SeatType.MIDDLE;
        };
    }
}