package com.rapiddweller.platform.mongodb.client;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCursor;
import com.rapiddweller.platform.mongodb.datasource.MongoDBRunCommandResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Collections;
import java.util.List;

public class MongoDBClient extends MongoClient {

    private final String database;

    public MongoDBClient(MongoDBClientProvider mongoDBClientProvider) {
        super(List.of(mongoDBClientProvider.getServerAddress()),
                mongoDBClientProvider.getCredential(),
                mongoDBClientProvider.getOptions());
        this.database = mongoDBClientProvider.getDatabase();
    }

    public MongoCursor<Document> findAll(String collection) {
        return this.getDatabase(database).getCollection(collection).find().iterator();
    }

    public MongoDBRunCommandResult runCommand(Bson query) {
        Document result = this.getDatabase(this.database).runCommand(query);
        if (((Document) query).containsKey("delete")) { // Exception: delete command don't have cursor
            long cursorId = 0L;
            List<Document> documents = Collections.emptyList();
            return new MongoDBRunCommandResult(cursorId, documents.iterator());
        }
        Document cursor = result.get("cursor", Document.class);
        long cursorId = cursor.get("id", Long.class);
        List<Document> documents = getDocuments(cursor);
        return new MongoDBRunCommandResult(cursorId, documents.iterator());
    }

    private static List<Document> getDocuments(Document cursor) {
        if (cursor.containsKey("firstBatch")) {
            return cursor.get("firstBatch", List.class);
        }
        if (cursor.containsKey("nextBatch")) {
            return cursor.get("nextBatch", List.class);
        }
        return Collections.emptyList();

    }

    public void insertDocument(String collectionName, Document document) {
        this.getDatabase(this.database).getCollection(collectionName).insertOne(document);
    }

    public void replaceDocument(String collectionName, Bson filter, Document document) {
        this.getDatabase(this.database).getCollection(collectionName).replaceOne(filter, document);
    }

    public void cleanDatabase() {
        dropAllCollections();
    }

    private void dropAllCollections() {
        for (String collectionName : this.getDatabase(this.database).listCollectionNames()) {
            this.dropCollection(collectionName);
        }
    }

    private void dropCollection(String collectionName) {
        this.getDatabase(this.database).getCollection(collectionName).drop();
    }

}
