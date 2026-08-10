package examples.model;

public class FareBreakdown {

    private double baseFare;
    private double demandSurcharge;
    private double seatCharges;
    private double gst;
    private double airportCharges;
    private double fuelSurcharge;
    private double discount;
    private double total;

    public double getBaseFare() { return baseFare; }
    public void setBaseFare(double baseFare) { this.baseFare = baseFare; }

    public double getDemandSurcharge() { return demandSurcharge; }
    public void setDemandSurcharge(double demandSurcharge) { this.demandSurcharge = demandSurcharge; }

    public double getSeatCharges() { return seatCharges; }
    public void setSeatCharges(double seatCharges) { this.seatCharges = seatCharges; }

    public double getGst() { return gst; }
    public void setGst(double gst) { this.gst = gst; }

    public double getAirportCharges() { return airportCharges; }
    public void setAirportCharges(double airportCharges) { this.airportCharges = airportCharges; }

    public double getFuelSurcharge() { return fuelSurcharge; }
    public void setFuelSurcharge(double fuelSurcharge) { this.fuelSurcharge = fuelSurcharge; }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    @Override
    public String toString() {

        return String.format("""
                Base Fare            : Rs.%.2f
                Demand Surcharge     : Rs.%.2f
                Seat Charges         : Rs.%.2f
                Airport Charges      : Rs.%.2f
                Fuel Surcharge       : Rs.%.2f
                GST                  : Rs.%.2f
                Discount             : -Rs.%.2f
                -----------------------------------
                Total                : Rs.%.2f
                """, baseFare, demandSurcharge, seatCharges, airportCharges,
                fuelSurcharge, gst, discount, total);
    }
}