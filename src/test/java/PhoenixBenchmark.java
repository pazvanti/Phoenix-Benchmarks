import org.example.model.Stock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

import java.io.IOException;
import java.util.List;

@SpringBootTest(classes = {SecurityConfig.class, PhoenixMessageConverter.class, RouteGenerator.class, Compiler.class, ElementFactory.class,
        DynamicClassLoader.class, PhoenixConfiguration.class, PhoenixSpecialElementsUtil.class, FragmentController.class,
        PhoenixErrorHandler.class})
class PhoenixBenchmark {
    @Autowired
    private Compiler compiler;

    @Autowired
    private RouteGenerator routeGenerator;

    @Autowired
    private PhoenixSpecialElementsUtil phoenixSpecialElementsUtil;

    @Test
    void benchmark() throws IOException {
        PhoenixConfiguration phoenixConfiguration = new PhoenixConfiguration();
        phoenixConfiguration.setViews(new ViewsConfiguration());
        phoenixConfiguration.getViews().setPath("src/test/resources/views");

        PhoenixParser parser = new PhoenixParser(new ElementFactory(null), routeGenerator, compiler, phoenixConfiguration);
        parser.parse();
        List<Stock> items = Stock.dummyItems();

        System.out.println("Running benchmark...");
        long start = System.currentTimeMillis();
        for (int count = 0; count<100_000; count++) {
            View.of("phoenix", items).getContent(phoenixSpecialElementsUtil);
        }
        long end = System.currentTimeMillis();
        System.out.println("Phoenix Benchmark: " + (end - start) + "ms");
    }
}
