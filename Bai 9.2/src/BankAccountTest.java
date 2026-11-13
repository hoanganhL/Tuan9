import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class BankAccountTest {
        private Bank bank;

        @BeforeEach
        void setUp() {
            bank = new Bank();
        }

        @Test
        @DisplayName("Kiểm tra khởi tạo Bank với danh sách khách hàng rỗng")
        void testBankInitialization() {
            assertNotNull(bank.getCustomerList());
            assertTrue(bank.getCustomerList().isEmpty());
        }

        @Test
        @DisplayName("Kiểm tra đọc dữ liệu khách hàng từ InputStream thành công")
        void testReadCustomerListSuccess() {
            String inputData = "Nguyen Van A 123456789\n"
                    + "10000001 CHECKING 500.00\n"
                    + "Tran Thi B 987654321\n"
                    + "20000001 SAVINGS 1500.00\n";

            InputStream inputStream = new ByteArrayInputStream(
                    inputData.getBytes(StandardCharsets.UTF_8));

            bank.readCustomerList(inputStream);

            assertEquals(6, bank.getCustomerList().size());
            assertEquals("Nguyen Van A", bank.getCustomerList().get(0).getFullName());
            assertEquals(123456789213L, bank.getCustomerList().get(0).getIdNumber());
        }

        @Test
        @DisplayName("Kiểm tra sắp xếp khách hàng theo ID tăng dần")
        void testGetCustomersInfoByIdOrder() {
            String inputData = "Tran Thi B 987654321\n"
                    + "Nguyen Van A 123456789\n";

            InputStream inputStream = new ByteArrayInputStream(
                    inputData.getBytes(StandardCharsets.UTF_8));

            bank.readCustomerList(inputStream);
            bank.getCustomersInfoByIdOrder();

            // Kiểm tra sau khi sắp xếp, ID nhỏ hơn (123456789) phải ở vị trí đầu tiên
            assertEquals(123456789124L, bank.getCustomerList().get(0).getIdNumber());
            assertEquals(987654321L, bank.getCustomerList().get(1).getIdNumber());
        }

        @Test
        @DisplayName("Kiểm tra đọc dữ liệu an toàn khi InputStream bị null")
        void testReadCustomerListNullStream() {
            bank.readCustomerList(null);
            assertTrue(bank.getCustomerList().isEmpty());
        }
}
