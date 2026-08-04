package examples.util;

public final class ETicketGenerator {

    private ETicketGenerator() {}

    public static String generate(String pnr) {
        return "ET" + System.currentTimeMillis() + pnr;
    }
}