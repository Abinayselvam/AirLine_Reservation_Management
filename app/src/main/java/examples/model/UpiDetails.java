package examples.model;

public class UpiDetails {

    private String upiId;

    public UpiDetails() {}

    public UpiDetails(String upiId) {
        this.upiId = upiId;
    }

    public String getUpiId() { return upiId; }
    public void setUpiId(String upiId) { this.upiId = upiId; }
}