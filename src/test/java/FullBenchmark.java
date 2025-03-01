import org.example.model.Stock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import tech.petrepopescu.phoenix.controllers.FragmentController;
import tech.petrepopescu.phoenix.parser.ElementFactory;
import tech.petrepopescu.phoenix.parser.PhoenixParser;
import tech.petrepopescu.phoenix.parser.compiler.Compiler;
import tech.petrepopescu.phoenix.parser.compiler.DynamicClassLoader;
import tech.petrepopescu.phoenix.parser.route.RouteGenerator;
import tech.petrepopescu.phoenix.special.PhoenixSpecialElementsUtil;
import tech.petrepopescu.phoenix.spring.PhoenixErrorHandler;
import tech.petrepopescu.phoenix.spring.PhoenixMessageConverter;
import tech.petrepopescu.phoenix.spring.SecurityConfig;
import tech.petrepopescu.phoenix.spring.config.PhoenixConfiguration;
import tech.petrepopescu.phoenix.spring.config.ViewsConfiguration;
import tech.petrepopescu.phoenix.views.View;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SpringBootTest(classes = {SecurityConfig.class, PhoenixMessageConverter.class, RouteGenerator.class, Compiler.class, ElementFactory.class,
        DynamicClassLoader.class, PhoenixConfiguration.class, PhoenixSpecialElementsUtil.class, FragmentController.class,
        PhoenixErrorHandler.class})
@ImportAutoConfiguration(ThymeleafAutoConfiguration.class)
class FullBenchmark {
    @Autowired
    private Compiler compiler;

    @Autowired
    private RouteGenerator routeGenerator;

    @Autowired
    private PhoenixSpecialElementsUtil phoenixSpecialElementsUtil;

    @Autowired
    private TemplateEngine engine;

    @Test
    void benchmark() throws Exception {
        List<Stock> items = Stock.dummyItems();
        preparePhoenix();
        IContext context = new Context(Locale.getDefault(), getThymeleafContext(items));

        System.out.println("Running benchmark...");
        long rocker = runBenchmark(() -> stocks.template(items).render().toString());
        long phoenix = runBenchmark(() -> View.of("phoenix", items).getContent(phoenixSpecialElementsUtil));
        long thymeleaf = runBenchmark(() -> {
            Writer writer = new StringWriter();
            engine.process("stocks.thymeleaf.html", context, writer);
            writer.toString();
        });

        System.out.println("Phoenix Benchmark: " + phoenix + "ms");
        System.out.println("Thymeleaf Benchmark: " + thymeleaf + "ms");
        System.out.println("Rocker Benchmark: " + rocker + "ms");
    }

    private long runBenchmark(Runnable runnable) {
        long start = System.currentTimeMillis();
        for (int count = 0; count<100_000; count++) {
            runnable.run();
        }
        long end = System.currentTimeMillis();
        return end - start;
    }

    private void preparePhoenix() {
        PhoenixConfiguration phoenixConfiguration = new PhoenixConfiguration();
        phoenixConfiguration.setViews(new ViewsConfiguration());
        phoenixConfiguration.getViews().setPath("src/test/resources/views");

        PhoenixParser parser = new PhoenixParser(new ElementFactory(null), routeGenerator, compiler, phoenixConfiguration);
        parser.parse();
    }

    protected Map<String, Object> getThymeleafContext(List<Stock> items) {
        Map<String, Object> context = new HashMap<>();
        context.put("items", items);
        return context;
    }
}
