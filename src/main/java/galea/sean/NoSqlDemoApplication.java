package galea.sean;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
@SpringBootApplication
public class NoSqlDemoApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext run =
                SpringApplication.run(NoSqlDemoApplication.class, args);

        log.info("\n\n");
        log.info("---------------------------------------------------------------------");
        log.info(
                "Initialized NoSql Demo");
        log.info("---------------------------------------------------------------------");
    }
}
