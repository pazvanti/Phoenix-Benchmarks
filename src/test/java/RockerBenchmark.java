import org.example.model.Stock;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import tech.petrepopescu.phoenix.spring.SecurityConfig;

import java.io.IOException;
import java.util.List;

@SpringBootTest(classes = {SecurityConfig.class})
class RockerBenchmark {

    @Test
    void benchmark() throws IOException {
        List<Stock> items = Stock.dummyItems();

        System.out.println("Running benchmark...");
        long start = System.currentTimeMillis();
        for (int count = 0; count<100_000; count++) {
            stocks.template(items).render().toString();
        }
        long end = System.currentTimeMillis();
        System.out.println("Phoenix Benchmark: " + (end - start) + "ms");
    }
}
