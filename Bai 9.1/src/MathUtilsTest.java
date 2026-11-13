import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MathUtilsTest {
    MathUtils m=new MathUtils();
    @BeforeAll
    static void setUp(){
        System.out.println("Bat dau chay MathUtilsTest");
        MathUtils m=new MathUtils();
    }
    @Test
    public void testMax1() {
        assertEquals(9999,m.max(-9999,9999));
    }
    @Test
    public void testMax2() {
        assertEquals(0,m.max(0,0));
    }
    @Test
    public void testMax3() {
        assertEquals(9999,m.max(9999,-9999));
    }
    @Test
    public void testMax4() {
        assertEquals(1,m.max(-1,1));
    }
    @Test
    public void testMax5() {
        assertEquals(0,m.max(0,0));
    }
    @Test
    public void testMax6() {
        assertEquals(1,m.max(1,-1));
    }
    @Test
    public void testDivine1() {
        assertEquals(333,m.divide(999, 3));
    }
    @Test
    public void testDivine2() {
        assertThrows(IllegalArgumentException.class, ()->m.divide(5, 0));
    }

    @Test
    public void testDivine3() {
        assertEquals(333,m.divide(-999, -3));
    }
    @Test
    public void testDivine4() {
        assertEquals(1,m.divide(1, 1));
    }
    @Test
    public void testDivine5() {
        assertThrows(IllegalArgumentException.class, ()->m.divide(0, 0));
    }
    @Test
    public void testDivine6() {
        assertEquals(1,m.divide(-1, -1));
    }
    @AfterAll
    static void end(){
        System.out.println("Ket thuc!");
    }
}
