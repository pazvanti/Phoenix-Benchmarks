import org.example.model.Stock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import tech.petrepopescu.phoenix.spring.SecurityConfig;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@SpringBootTest(classes = {SecurityConfig.class})
@ImportAutoConfiguration(ThymeleafAutoConfiguration.class)
class ThymeleafBenchmark {

    @Autowired
    private TemplateEngine engine;


    @Test
    void benchmark() throws IOException {
        IContext context = new Context(Locale.getDefault(), getContext());

        System.out.println("Running benchmark...");
        long start = System.currentTimeMillis();
        for (int count = 0; count<100_000; count++) {
            Writer writer = new StringWriter();
            engine.process("stocks.thymeleaf.html", context, writer);
            writer.toString();
        }
        long end = System.currentTimeMillis();
        System.out.println("Thymeleaf Benchmark: " + (end - start) + "ms");
    }

    protected Map<String, Object> getContext() {
        Map<String, Object> context = new HashMap<>();
        context.put("items", Stock.dummyItems());
        return context;
    }
}
