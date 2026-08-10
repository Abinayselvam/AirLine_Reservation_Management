package examples.util;

import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

public final class PaginationUtil {

    private static final int PAGE_SIZE = 5;

    private PaginationUtil() {}

    public static <T> void paginate(List<T> items, Consumer<T> printer) {

        if (items.isEmpty()) {

            System.out.println("No results");

            return;
        }

        Scanner sc = new Scanner(System.in);

        int totalPages = (int) Math.ceil(items.size() / (double) PAGE_SIZE);

        int page = 0;

        while (true) {

            int start = page * PAGE_SIZE;

            int end = Math.min(start + PAGE_SIZE, items.size());

            System.out.printf("%n-- Page %d of %d (%d results) --%n", page + 1, totalPages, items.size());

            items.subList(start, end).forEach(printer);

            if (totalPages <= 1) return;

            System.out.print("[n]ext / [p]rev / [q]uit paging : ");

            String input = sc.nextLine().trim().toLowerCase();

            if (input.equals("n") && page < totalPages - 1) page++;
            else if (input.equals("p") && page > 0) page--;
            else if (input.equals("q")) return;
        }
    }
}