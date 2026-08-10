package examples.util;

import examples.model.FareBreakdown;
import examples.model.Flight;

public final class FareCalculator {

    private static final double DOMESTIC_GST_RATE = 0.05;

    private static final double INTERNATIONAL_GST_RATE = 0.00; // exports of service - illustrative only

    private static final double AIRPORT_CHARGE_FLAT = 350;

    private static final double FUEL_SURCHARGE_FLAT = 500;

    // Occupancy-based demand pricing: fuller flights cost more per seat.
    private static final double HIGH_DEMAND_THRESHOLD = 80.0;

    private static final double HIGH_DEMAND_MULTIPLIER = 0.15;

    private static final double MEDIUM_DEMAND_THRESHOLD = 50.0;

    private static final double MEDIUM_DEMAND_MULTIPLIER = 0.07;

    private FareCalculator() {}

    public static FareBreakdown calculate(
            Flight flight, int passengerCount, double seatCharges,
            double promoDiscountRate, boolean international) {

        FareBreakdown breakdown = new FareBreakdown();

        double baseFare = flight.getFare() * passengerCount;

        breakdown.setBaseFare(baseFare);

        double demandRate = demandMultiplier(flight.occupancyPercentage());

        double demandSurcharge = baseFare * demandRate;

        breakdown.setDemandSurcharge(demandSurcharge);

        breakdown.setSeatCharges(seatCharges);

        double airportCharges = AIRPORT_CHARGE_FLAT * passengerCount;

        breakdown.setAirportCharges(airportCharges);

        double fuelSurcharge = FUEL_SURCHARGE_FLAT * passengerCount;

        breakdown.setFuelSurcharge(fuelSurcharge);

        double taxableAmount = baseFare + demandSurcharge + seatCharges + airportCharges + fuelSurcharge;

        double gstRate = international ? INTERNATIONAL_GST_RATE : DOMESTIC_GST_RATE;

        double gst = taxableAmount * gstRate;

        breakdown.setGst(gst);

        double subtotal = taxableAmount + gst;

        double discount = subtotal * promoDiscountRate;

        breakdown.setDiscount(discount);

        breakdown.setTotal(subtotal - discount);

        return breakdown;
    }

    private static double demandMultiplier(double occupancyPercentage) {

        if (occupancyPercentage >= HIGH_DEMAND_THRESHOLD) {
            return HIGH_DEMAND_MULTIPLIER;
        }

        if (occupancyPercentage >= MEDIUM_DEMAND_THRESHOLD) {
            return MEDIUM_DEMAND_MULTIPLIER;
        }

        return 0.0;
    }
}