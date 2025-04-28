package elastic.search.demo.service;

import com.mongodb.client.MongoChangeStreamCursor;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import elastic.search.demo.model.User;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.bson.Document;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class MongoChangeStreamListener {

    @Autowired
    private MongoDatabaseFactory mongoDatabaseFactory;

    @Autowired
    private ElasticsearchService elasticsearchService;

    @PostConstruct
    public void init() {
        MongoDatabase database = mongoDatabaseFactory.getMongoDatabase();
        MongoCollection<Document> collection = database.getCollection("users");

        new Thread(() -> {
            try (MongoChangeStreamCursor<ChangeStreamDocument<Document>> cursor =
                         collection.watch().cursor()) {

                while (cursor.hasNext()) {
                    ChangeStreamDocument<Document> change = cursor.next();
                    Document fullDoc = change.getFullDocument();

                    if (fullDoc != null) {
                        User user = new User(
                                fullDoc.getObjectId("_id").toHexString(),
                                fullDoc.getString("name"),
                                fullDoc.getString("email")
                        );

                        elasticsearchService.indexUser(user);
                    }
                }
            }
        }).start();
    }
}

