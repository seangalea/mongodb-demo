package galea.sean.service;

import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Field;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.lte;
import static java.util.Arrays.asList;

@Slf4j
@Service
public class TokenService {
  MongoDatabase mongoDatabase;

  @Autowired
  public void setMongoDatabase(MongoDatabase mongoDatabase) {
    this.mongoDatabase = mongoDatabase;
  }

  public List<Map<String, Object>> getTokens() {
    MongoCollection<Document> tokensCollection = mongoDatabase.getCollection("tokens");
    FindIterable<Document> results = tokensCollection.find();

    List<Map<String, Object>> res = new ArrayList();
    results
        .map(
            r -> {
              Set<Map.Entry<String, Object>> es = r.entrySet();
              return es.stream()
                  .collect(
                      Collectors.toMap(
                          entry -> ((Map.Entry<String, Object>) entry).getKey(),
                          entry -> ((Map.Entry<String, Object>) entry).getValue()));
            })
        .into(res);

    return res;
  }

  public List<String> getAllTokenTags() {
    MongoCollection<Document> tokensCollection = mongoDatabase.getCollection("tokens");
    DistinctIterable<String> results = tokensCollection.distinct("tag", String.class);

    List<String> tags = new ArrayList<>();
    results.map(tag -> tag).into(tags);

    return tags;
  }

  public List<Map<String, Object>> getTokens(String tag) {
    MongoCollection<Document> tokensCollection = mongoDatabase.getCollection("tokens");
    FindIterable<Document> results = tokensCollection.find().filter(eq("tag", tag));

    List<Map<String, Object>> res = new ArrayList();
    results
        .map(
            r -> {
              Set<Map.Entry<String, Object>> es = r.entrySet();
              return es.stream()
                  .collect(
                      Collectors.toMap(
                          entry -> ((Map.Entry<String, Object>) entry).getKey(),
                          entry -> ((Map.Entry<String, Object>) entry).getValue()));
            })
        .into(res);

    return res;
  }

  public Document addToken(Map input) {
    MongoCollection<Document> tokensCollection = mongoDatabase.getCollection("tokens");

    Document tokenDoc = createToken(input);
    tokensCollection.insertOne(tokenDoc);

    return tokenDoc;
  }

  private Document createToken(Map input) {
    Document tokenDocDetail = new Document();
    input
        .keySet()
        .forEach(
            key -> {
              tokenDocDetail.put((String) key, input.get(key));
            });

    return tokenDocDetail;
  }

  @Scheduled(fixedRate = 1000)
  public void purgeExpiredTasks() {
    MongoCollection<Document> tokensCollection = mongoDatabase.getCollection("tokens");
    tokensCollection
        .aggregate(
            asList(
                addFields(new Field("idDate", Document.parse("{$toDate: '$_id'}"))),
                project(Document.parse("{ ttl:1, expirationDate : {$add : ['$idDate', '$ttl']} }")),
                match(lte("expirationDate", new Date()))))
        .forEach(
            (Consumer<? super Document>)
                doc -> {
                  log.info("Deleting expired token - {}", doc.toJson());
                  tokensCollection.deleteOne(new Document("_id", doc.get("_id")));
                });
  }

  @PostConstruct
  public void init() {
    MongoCollection<Document> tokensCollection = mongoDatabase.getCollection("tokens");
    tokensCollection.drop();

    List<Document> tokenList = new ArrayList<>();
    // add token
    Document tokenDocDetail1 = new Document();
    tokenDocDetail1.put("name", "full token");
    tokenDocDetail1.put("tag", "productive");
    tokenDocDetail1.put("priority", 12);
    tokenDocDetail1.put("ttl", 60000);
    tokenList.add(tokenDocDetail1);

    // add token
    Document tokenDocDetail2 = new Document();
    tokenDocDetail2.put("name", "expiry token");
    tokenDocDetail2.put("ttl", 300000);
    tokenList.add(tokenDocDetail2);

    // add token
    Document tokenDocDetail3 = new Document();
    tokenDocDetail3.put("name", "tagged token");
    tokenDocDetail3.put("tag", "registration");
    tokenList.add(tokenDocDetail3);

    // add token
    Document tokenDocDetail4 = new Document();
    tokenDocDetail4.put("name", "priority token");
    tokenDocDetail4.put("priority", 8);
    tokenList.add(tokenDocDetail4);

    tokensCollection.insertMany(tokenList);

    FindIterable<Document> results = tokensCollection.find();

    for (Document doc : results) {
      System.out.println("Available tokens: " + doc.toJson());
    }
  }
}
