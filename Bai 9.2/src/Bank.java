import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lớp quản lý danh sách khách hàng và các thao tác xử lý dữ liệu ngân hàng.
 */
public class Bank {

    private static final Logger logger = LoggerFactory.getLogger(Bank.class);
    private static final Pattern ID_PATTERN = Pattern.compile("^\\d{9}$");
    private static final String ACCOUNT_TYPE_CHECKING = "CHECKING";
    private static final String ACCOUNT_TYPE_SAVINGS = "SAVINGS";

    private List<Customer> customerList;

    /**
     * Khởi tạo đối tượng Bank với danh sách khách hàng rỗng.
     */
    public Bank() {
        this.customerList = new ArrayList<>();
    }

    /**
     * Lấy danh sách khách hàng.
     *
     * @return danh sách khách hàng hiện tại
     */
    public List<Customer> getCustomerList() {
        return customerList;
    }

    /**
     * Cập nhật danh sách khách hàng.
     *
     * @param customerList danh sách khách hàng mới cần gán
     */
    public void setCustomerList(List<Customer> customerList) {
        if (customerList == null) {
            this.customerList = new ArrayList<>();
        } else {
            this.customerList = new ArrayList<>(customerList);
        }
    }

    /**
     * Đọc danh sách khách hàng từ luồng dữ liệu (InputStream).
     *
     * @param inputStream luồng dữ liệu chứa thông tin khách hàng
     */
    public void readCustomerList(InputStream inputStream) {
        logger.debug("Bắt đầu đọc dữ liệu từ InputStream...");
        if (inputStream == null) {
            logger.warn("InputStream đầu vào bị null, bỏ qua quá trình đọc dữ liệu.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            Customer currentCustomer = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                currentCustomer = processLine(line, currentCustomer);
            }
            logger.info("Đã hoàn tất đọc dữ liệu. Tổng số khách hàng hiện tại: {}", customerList.size());
        } catch (IOException e) {
            logger.error("Lỗi I/O xảy ra trong quá trình đọc dữ liệu khách hàng: {}", e.getMessage(), e);
        }
    }

    /**
     * Xử lý từng dòng dữ liệu từ file để phân loại Khách hàng hoặc Tài khoản.
     */
    private Customer processLine(String line, Customer currentCustomer) {
        int lastSpaceIndex = line.lastIndexOf(' ');
        if (lastSpaceIndex <= 0) {
            return currentCustomer;
        }

        String token = line.substring(lastSpaceIndex + 1).trim();
        if (ID_PATTERN.matcher(token).matches()) {
            String name = line.substring(0, lastSpaceIndex).trim();
            try {
                long id = Long.parseLong(token);
                Customer newCustomer = new Customer(id, name);
                customerList.add(newCustomer);
                logger.info("Đã thêm khách hàng mới: {} (Số ID: {})", name, id);
                return newCustomer;
            } catch (NumberFormatException e) {
                logger.error("Lỗi định dạng ID không hợp lệ cho khách hàng {}: {}", name, token);
                return currentCustomer;
            }
        }

        if (currentCustomer != null) {
            parseAndAddAccount(line, currentCustomer);
        }
        return currentCustomer;
    }

    /**
     * Phân tích và thêm tài khoản cho khách hàng hiện tại.
     */
    private void parseAndAddAccount(String line, Customer customer) {
        String[] parts = line.split("\\s+");
        if (parts.length < 3) {
            logger.warn("Dòng dữ liệu tài khoản không đủ thông tin cấu trúc: {}", line);
            return;
        }

        try {
            long accountNumber = Long.parseLong(parts[0]);
            String accountType = parts[1];
            double balance = Double.parseDouble(parts[2]);

            if (ACCOUNT_TYPE_CHECKING.equalsIgnoreCase(accountType)) {
                customer.addAccount(new CheckingAccount(accountNumber, balance));
                logger.debug("Đã thêm tài khoản CHECKING ({}) cho khách hàng ID {}",
                        accountNumber, customer.getIdNumber());
            } else if (ACCOUNT_TYPE_SAVINGS.equalsIgnoreCase(accountType)) {
                customer.addAccount(new SavingsAccount(accountNumber, balance));
                logger.debug("Đã thêm tài khoản SAVINGS ({}) cho khách hàng ID {}",
                        accountNumber, customer.getIdNumber());
            } else {
                logger.warn("Loại tài khoản không được hỗ trợ: {}", accountType);
            }
        } catch (NumberFormatException e) {
            logger.error("Lỗi định dạng số khi thêm tài khoản từ dòng: {}", line, e);
        }
    }

    /**
     * Trả về thông tin khách hàng đã được sắp xếp theo ID tăng dần.
     *
     * @return chuỗi thông tin toàn bộ khách hàng
     */
    public String getCustomersInfoByIdOrder() {
        customerList.sort(Comparator.comparingLong(Customer::getIdNumber));
        return formatCustomerList(customerList);
    }

    /**
     * Trả về thông tin khách hàng đã được sắp xếp theo Tên (sau đó theo ID nếu trùng tên).
     *
     * @return chuỗi thông tin toàn bộ khách hàng
     */
    public String getCustomersInfoByNameOrder() {
        List<Customer> copyList = new ArrayList<>(customerList);
        copyList.sort(Comparator.comparing(Customer::getFullName)
                .thenComparingLong(Customer::getIdNumber));
        return formatCustomerList(copyList);
    }

    /**
     * Nối thông tin các khách hàng thành chuỗi văn bản phân cách bằng dòng mới.
     */
    private String formatCustomerList(List<Customer> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i).getCustomerInfo());
            if (i < list.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}