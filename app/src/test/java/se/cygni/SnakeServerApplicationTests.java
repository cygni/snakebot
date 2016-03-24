package se.cygni;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.IntStream;

//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = SnakeServerApplication.class)
public class SnakeServerApplicationTests {

//	@Test
//	public void contextLoads() {
//	}

    @Test @Ignore
    public void streamTest() {
        int[][] arr = { {1, 2, 3}, {4, 5, 6}, {7, 8, 9} };
        IntStream stream = Arrays.stream(arr).flatMapToInt(x -> Arrays.stream(x));
        stream.forEach(System.out::println);
    }
}
