

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lớp trừu tượng đại diện cho một tài khoản ngân hàng tổng quát.
 * Cung cấp các thao tác quản lý số dư, lịch sử giao dịch và thực thi nạp/rút tiền.
 */
public abstract class Account {

    private static final Logger logger = LoggerFactory.getLogger(Account.class);

    public static final String CHECKING_TYPE = "CHECKING";
    public static final String SAVINGS_TYPE = "SAVINGS";

    private long accountNumber;
    private double balance;
    protected List<Transaction> transactionList;

    /**
     * Khởi tạo một tài khoản ngân hàng mới.
     *
     * @param accountNumber số tài khoản
     * @param balance số dư ban đầu
     */
    public Account(long accountNumber, double balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.transactionList = new ArrayList<>();
    }

    public long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    protected void setBalance(double balance) {
        this.balance = balance;
    }

    public List<Transaction> getTransactionList() {
        return transactionList;
    }

    /**
     * Cập nhật danh sách giao dịch cho tài khoản.
     *
     * @param transactionList danh sách giao dịch mới
     */
    public void setTransactionList(List<Transaction> transactionList) {
        if (transactionList == null) {
            this.transactionList = new ArrayList<>();
        } else {
            this.transactionList = new ArrayList<>(transactionList);
        }
    }

    /**
     * Nạp tiền vào tài khoản.
     *
     * @param amount số tiền cần nạp
     * @throws InvalidFundingAmountException nếu số tiền nạp không hợp lệ
     */
    public abstract void deposit(double amount) throws InvalidFundingAmountException;

    /**
     * Rút tiền khỏi tài khoản.
     *
     * @param amount số tiền cần rút
     * @throws InvalidFundingAmountException nếu số tiền rút không hợp lệ
     * @throws InsufficientFundsException nếu số dư không đủ
     */
    public abstract void withdraw(double amount)
            throws InvalidFundingAmountException, InsufficientFundsException;

    /**
     * Thực hiện cộng tiền vào số dư tài khoản.
     *
     * @param amount số tiền nạp
     * @throws InvalidFundingAmountException nếu số tiền nạp nhỏ hơn hoặc bằng 0
     */
    protected void doDepositing(double amount) throws InvalidFundingAmountException {
        if (amount <= 0) {
            logger.warn("Thao tác nạp tiền thất bại cho tài khoản {}: Số tiền ({}) không hợp lệ",
                    accountNumber, amount);
            throw new InvalidFundingAmountException(amount);
        }
        balance += amount;
        logger.info("Nạp tiền thành công cho tài khoản {}. Số tiền: {}, Số dư mới: {}",
                accountNumber, amount, balance);
    }

    /**
     * Thực hiện trừ tiền khỏi số dư tài khoản.
     *
     * @param amount số tiền rút
     * @throws InvalidFundingAmountException nếu số tiền rút nhỏ hơn hoặc bằng 0
     * @throws InsufficientFundsException nếu số dư tài khoản không đủ
     */
    protected void doWithdrawing(double amount)
            throws InvalidFundingAmountException, InsufficientFundsException {
        if (amount <= 0) {
            logger.warn("Thao tác rút tiền thất bại cho tài khoản {}: Số tiền ({}) không hợp lệ",
                    accountNumber, amount);
            throw new InvalidFundingAmountException(amount);
        }
        if (amount > balance) {
            logger.error("Thao tác rút tiền thất bại cho tài khoản {}: Số dư không đủ "
                    + "(Số dư hiện tại: {}, Số tiền rút: {})", accountNumber, balance, amount);
            throw new InsufficientFundsException(amount);
        }
        balance -= amount;
        logger.info("Rút tiền thành công cho tài khoản {}. Số tiền: {}, Số dư mới: {}",
                accountNumber, amount, balance);
    }

    /**
     * Thêm một giao dịch vào lịch sử giao dịch của tài khoản.
     *
     * @param transaction đối tượng giao dịch cần thêm
     */
    public void addTransaction(Transaction transaction) {
        if (transaction != null) {
            transactionList.add(transaction);
            logger.debug("Đã thêm giao dịch vào tài khoản {}", accountNumber);
        }
    }

    /**
     * Lấy lịch sử giao dịch dạng chuỗi văn bản.
     *
     * @return chuỗi biểu diễn toàn bộ lịch sử giao dịch
     */
    public String getTransactionHistory() {
        StringBuilder builder = new StringBuilder();
        builder.append("Lịch sử giao dịch của tài khoản ").append(accountNumber).append(":\n");

        for (int i = 0; i < transactionList.size(); i++) {
            builder.append(transactionList.get(i).getTransactionSummary());
            if (i < transactionList.size() - 1) {
                builder.append("\n");
            }
        }

        logger.debug("Đã lấy lịch sử cho tài khoản: {}", accountNumber);
        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Account)) {
            return false;
        }
        Account other = (Account) obj;
        return this.accountNumber == other.accountNumber;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(accountNumber);
    }
}