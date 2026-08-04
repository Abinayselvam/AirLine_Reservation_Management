package examples.service;

import examples.enums.BookingPriority;
import examples.model.BookingRequest;
import examples.service.iservice.IPriorityBookingQueueService;
import examples.util.BookingRequestComparator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class PriorityBookingQueueService implements IPriorityBookingQueueService {

    private final PriorityQueue<BookingRequest> queue =
            new PriorityQueue<>(new BookingRequestComparator());

    private final AtomicInteger requestIdGenerator = new AtomicInteger(1);

    private int expressServed = 0;

    private int regularServed = 0;

    private long totalWaitMillis = 0;

    private int totalServed = 0;

    @Override
    public BookingRequest submit(int userId, int flightId, int passengerCount, BookingPriority priority) {

        BookingRequest request = new BookingRequest();

        request.setRequestId(requestIdGenerator.getAndIncrement());
        request.setUserId(userId);
        request.setFlightId(flightId);
        request.setPassengerCount(passengerCount);
        request.setPriority(priority);
        request.setRequestTime(LocalDateTime.now());

        queue.offer(request);

        return request;
    }

    @Override
    public BookingRequest processNext() {

        reheapify();

        BookingRequest next = queue.poll();

        if (next == null) {
            return null;
        }

        long waitMillis = Duration.between(next.getRequestTime(), LocalDateTime.now()).toMillis();

        totalWaitMillis += waitMillis;

        totalServed++;

        if (next.getPriority() == BookingPriority.EXPRESS) {
            expressServed++;
        } else {
            regularServed++;
        }

        return next;
    }

    /**
     * java.util.PriorityQueue assumes element ordering doesn't change while
     * an element sits in the heap. Since our comparator's rank depends on
     * elapsed wait time (for starvation prevention), we force a re-evaluation
     * by draining and re-offering every element before extracting the head.
     */
    private void reheapify() {

        List<BookingRequest> snapshot = new ArrayList<>(queue);

        queue.clear();

        queue.addAll(snapshot);
    }

    @Override
    public int queueSize() {
        return queue.size();
    }

    @Override
    public void printReport() {

        reheapify();

        System.out.println("\n===== PRIORITY QUEUE REPORT =====");

        System.out.println("Current Queue Size : " + queue.size());

        Map<BookingPriority, Long> waiting = queue.stream()
                .collect(Collectors.groupingBy(BookingRequest::getPriority, Collectors.counting()));

        System.out.println("Waiting - Express : " + waiting.getOrDefault(BookingPriority.EXPRESS, 0L) +
                " | Regular : " + waiting.getOrDefault(BookingPriority.REGULAR, 0L));

        System.out.println("Processed So Far - Express : " + expressServed + " | Regular : " + regularServed);

        double avgWaitSeconds = totalServed == 0 ? 0 : (totalWaitMillis / 1000.0) / totalServed;

        System.out.printf("Average Wait Time : %.1f seconds (over %d processed)%n", avgWaitSeconds, totalServed);

        if (!queue.isEmpty()) {

            System.out.println("\nCurrently Waiting (processing order):");

            queue.stream()
                    .sorted(new BookingRequestComparator())
                    .forEach(r -> System.out.printf(
                            "  #%d | User %d | Flight %d | %s | Waiting %ds%n",
                            r.getRequestId(), r.getUserId(), r.getFlightId(), r.getPriority(),
                            Duration.between(r.getRequestTime(), LocalDateTime.now()).getSeconds()));
        }
    }
}