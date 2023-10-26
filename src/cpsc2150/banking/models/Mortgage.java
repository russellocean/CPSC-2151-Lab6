package cpsc2150.banking.models;

public class Mortgage extends AbsMortgage {

    private double Payment;
    private double Rate;
    private ICustomer Customer;
    private double DebtToIncomeRatio;
    private double Principal;
    private int NumberOfPayments;
    private double PercentDown;

    /**
     * Constructor for the Mortgage class.
     * @param costOfHome cost of the house
     * @param downPayment the down payment made
     * @param years number of years the loan is taken for
     * @param customer the associated customer
     * @pre costOfHome > 0 && downPayment >= 0 && downPayment < costOfHome && years >= MIN_YEARS && years <= MAX_YEARS
     * @post this.Payment = [calculated based on provided formula] AND
     *       this.Rate = [calculated based on provided instructions] AND
     *       this.Customer = customer AND
     *       this.DebtToIncomeRatio = [calculated based on customer's data] AND
     *       this.Principal = costOfHome - downPayment AND
     *       this.NumberOfPayments = years * MONTHS_IN_YEAR AND
     *       this.PercentDown = downPayment / costOfHome
     */
    public Mortgage(double costOfHome, double downPayment, int years, ICustomer customer) {
        // Constructor logic
        this.Customer = customer;
        this.Principal = costOfHome - downPayment;
        this.NumberOfPayments = years * MONTHS_IN_YEAR;
        this.PercentDown = downPayment / costOfHome;

        // Calculate the APR
        this.Rate = (BASERATE / MONTHS_IN_YEAR);

        // If the loan is for less than 30 years, add 0.5%; otherwise, add 1%
        if(years < MAX_YEARS){
            this.Rate += (GOODRATEADD / MONTHS_IN_YEAR);
        }else{
            this.Rate += (NORMALRATEADD / MONTHS_IN_YEAR);
        }

        // If the percent down is not at least 20%, add 0.5% to the APR
        if(PercentDown < PREFERRED_PERCENT_DOWN){
            this.Rate += (GOODRATEADD / MONTHS_IN_YEAR);
        }

        // The customer's credit score also adds to the rate:
        int creditScore = Customer.getCreditScore();

        // Modify rate based off credit score
        if (creditScore < BADCREDIT) {
            this.Rate += (VERYBADRATEADD / MONTHS_IN_YEAR);
        } else if (creditScore < FAIRCREDIT) {
            this.Rate += (BADRATEADD / MONTHS_IN_YEAR);
        } else if (creditScore < GOODCREDIT) {
            this.Rate += (NORMALRATEADD / MONTHS_IN_YEAR);
        } else if (creditScore < GREATCREDIT) {
            this.Rate += (GOODRATEADD / MONTHS_IN_YEAR);
        }

        // Calculate the monthly payment
        this.Payment = (Rate * Principal) / (1 - Math.pow((1 + Rate), -NumberOfPayments));

        // Calculate the debt to income ratio
        this.DebtToIncomeRatio = (Customer.getMonthlyDebtPayments() + Payment) / (Customer.getIncome() / MONTHS_IN_YEAR);
    }

    @Override
    public boolean loanApproved() {
        return Rate * 12 < RATETOOHIGH && PercentDown >= MIN_PERCENT_DOWN && DebtToIncomeRatio <= DTOITOOHIGH;
    }

    @Override
    public double getPayment() {
        return Payment;
    }

    @Override
    public double getRate() {
        return Rate * 12; // Convert monthly rate to annual rate (APR)
    }

    @Override
    public double getPrincipal() {
        return Principal;
    }

    @Override
    public int getYears() {
        return NumberOfPayments / MONTHS_IN_YEAR;
    }
}
