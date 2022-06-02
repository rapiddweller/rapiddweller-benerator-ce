package com.rapiddweller.platform.mongodb.datasource;

import com.rapiddweller.common.ConfigurationError;
import com.rapiddweller.format.DataContainer;
import com.rapiddweller.format.DataIterator;
import com.rapiddweller.platform.mongodb.client.MongoDBClient;
import org.bson.Document;
import org.bson.conversions.Bson;

public class MongoDBRunCommandIterator implements DataIterator<Document> {

    private final MongoDBClient mongoDBClient;
    private final String collection;
    private MongoDBRunCommandResult mongoDBRunCommandResult;

    public MongoDBRunCommandIterator(MongoDBClient mongoDBClient, String collection, Bson query) {
        this.mongoDBClient = mongoDBClient;
        this.collection = collection;
        this.mongoDBRunCommandResult = this.mongoDBClient.runCommand(query);
    }

    @Override
    public Class<Document> getType() {
        return Document.class;
    }

    @Override
    public DataContainer<Document> next(DataContainer<Document> dataContainer) throws ConfigurationError {
        if (!this.mongoDBRunCommandResult.getDocumentIterator().hasNext() && !this.mongoDBRunCommandResult.isDone()) {
            getMore();
        }
        if (this.mongoDBRunCommandResult.getDocumentIterator().hasNext()) {
            return dataContainer.setData(this.mongoDBRunCommandResult.getDocumentIterator().next());
        }
        return null;
    }

    private void getMore() {
        Document getMoreQuery = new Document()
                .append("getMore", this.mongoDBRunCommandResult.getCursorId())
                .append("collection", this.collection)
                .append("batchSize", 100);
        this.mongoDBRunCommandResult = this.mongoDBClient.runCommand(getMoreQuery);
    }

    @Override
    public void close() {
        this.mongoDBClient.close();
    }
}
