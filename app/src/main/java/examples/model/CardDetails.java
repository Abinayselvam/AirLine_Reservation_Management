package examples.model;

public class CardDetails {

    private String cardNumber;
    private String cardHolderName;
    private int expiryMonth;
    private int expiryYear;
    private String cvv;

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getCardHolderName() { return cardHolderName; }
    public void setCardHolderName(String cardHolderName) { this.cardHolderName = cardHolderName; }

    public int getExpiryMonth() { return expiryMonth; }
    public void setExpiryMonth(int expiryMonth) { this.expiryMonth = expiryMonth; }

    public int getExpiryYear() { return expiryYear; }
    public void setExpiryYear(int expiryYear) { this.expiryYear = expiryYear; }

    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }

    public String maskedNumber() {

        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }

        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
}