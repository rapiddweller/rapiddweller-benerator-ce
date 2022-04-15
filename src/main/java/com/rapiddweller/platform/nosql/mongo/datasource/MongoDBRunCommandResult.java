package com.rapiddweller.platform.nosql.mongo.datasource;

import org.bson.Document;

import java.util.Iterator;

public class MongoDBRunCommandResult {

    private final long cursorId;
    private final Iterator<Document> documentIterator;

    public MongoDBRunCommandResult(long cursorId, Iterator<Document> documentIterator) {
        this.cursorId = cursorId;
        this.documentIterator = documentIterator;
    }

    public long getCursorId() {
        return cursorId;
    }

    public Iterator<Document> getDocumentIterator() {
        return documentIterator;
    }

    public boolean isDone() {
        return this.cursorId == 0;
    }
}
