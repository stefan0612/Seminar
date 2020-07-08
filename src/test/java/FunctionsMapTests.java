import static org.junit.jupiter.api.Assertions.*;
import static fj.data.List.list;
import fj.data.List;

import org.junit.jupiter.api.Test;

public class FunctionsMapTests {
    @Test
    public void multiplicationOfZeroIntegersShouldReturnZero() {
        FunctionsMap tester = new FunctionsMap();
        final List<Integer> testlist = list(1, 2, 3);

        // assert statements
        assertEquals(list(2,3,4), tester.mapPlusOne(testlist), "10 x 0 must be 0");
    }
}