
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lớp đại diện cho tài khoản tiết kiệm (Savings Account).
 * Thực thi các quy định đặc thù về hạn mức rút tiền và bảo toàn số dư tối thiểu.
 */
public class SavingsAccount extends Account {

    private static final Logger logger = LoggerFactory.getLogger(SavingsAccount.class);

    public static final double MAX_WITHDRAWAL_AMOUNT = 1000.0;
    public static final double MIN_BALANCE = 5000.0;

    public static final int TYPE_DEPOSIT_SAVINGS = 3;
    public static final int TYPE_WITHDRAW_SAVINGS = 4;

    /**
     * Khởi tạo một tài khoản tiết kiệm mới.
     *
     * @param accountNumber số tài khoản
     * @param balance số dư ban đầu
     */
    public SavingsAccount(long accountNumber, double balance) {
        super(accountNumber, balance);
    }

    @Override
    public void deposit(double amount) throws InvalidFundingAmountException {
        logger.debug("Đang xử lý giao dịch nạp tiền vào tài khoản tiết kiệm: {}", getAccountNumber());
        double initialBalance = getBalance();

        try {
            doDepositing(amount);
            double finalBalance = getBalance();

            Transaction transaction = new Transaction(
                    TYPE_DEPOSIT_SAVINGS, amount, initialBalance, finalBalance);
            addTransaction(transaction);

            logger.info("Nạp tiền vào tài khoản tiết kiệm {} thành công. Số tiền: +{}, Số dư mới: {}",
                    getAccountNumber(), amount, finalBalance);
        } catch (InvalidFundingAmountException e) {
            logger.error("Lỗi nạp tiền vào tài khoản tiết kiệm {}: {}",
                    getAccountNumber(), e.getMessage());
            throw e;
        }
    }

    @Override
    public void withdraw(double amount)
            throws InvalidFundingAmountException, InsufficientFundsException {
        double initialBalance = getBalance();

        if (amount > MAX_WITHDRAWAL_AMOUNT) {
            logger.warn("Rút tiền thất bại cho tài khoản {}: Số tiền ({}) vượt quá hạn mức tối đa ({})",
                    getAccountNumber(), amount, MAX_WITHDRAWAL_AMOUNT);
            throw new InvalidFundingAmountException(amount);
        }

        if (initialBalance - amount < MIN_BALANCE) {
            logger.warn("Rút tiền thất bại cho tài khoản {}: Số dư còn lại sẽ thấp hơn mức tối thiểu ({})",
                    getAccountNumber(), MIN_BALANCE);
            throw new InsufficientFundsException(amount);
        }

        try {
            doWithdrawing(amount);
            double finalBalance = getBalance();

            Transaction transaction = new Transaction(
                    TYPE_WITHDRAW_SAVINGS, amount, initialBalance, finalBalance);
            addTransaction(transaction);

            logger.info("[SAVINGS] Rút tiền từ tài khoản {} thành công. Số tiền: {}, Số dư còn lại: {}",
                    getAccountNumber(), amount, finalBalance);
        } catch (InvalidFundingAmountException | InsufficientFundsException e) {
            logger.error("Rút tiền bị lỗi cho tài khoản {}: {}", getAccountNumber(), e.getMessage(), e);
            throw e;
        }
    }
}