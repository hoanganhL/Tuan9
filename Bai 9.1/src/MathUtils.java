import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MathUtils {
    private static final Logger logger=LoggerFactory.getLogger(MathUtils.class.getName());
    public static int max(int a, int b) {
        logger.info("Dang tim max cua {} va {}",a,b);
        if (a >= b) return a;
        return b;
    }

    public static int divide(int a, int b) {
        logger.info("Dang tim chia cua {} va {}",a,b);
        if (b == 0) {
            throw new IllegalArgumentException("Divider must not be zero");
        }
        return a / b;
    }
}
