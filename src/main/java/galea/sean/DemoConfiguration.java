package galea.sean;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.logging.Level;
import java.util.logging.Logger;

@Configuration
@EnableScheduling
public class DemoConfiguration {

  @Value("${mongodb.connection.string.uri}")
  private String connectionStringURI;

  @Bean
  public MongoDatabase mongoDatabase() {
    Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
    mongoLogger.setLevel(Level.SEVERE);

    MongoClient mongoClient = MongoClients.create(connectionStringURI);

    return mongoClient.getDatabase("mydatabase");
  }
}
