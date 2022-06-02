package com.rapiddweller.platform.mongodb.datasource;

import com.rapiddweller.common.Context;
import com.rapiddweller.common.Converter;
import com.rapiddweller.format.DataIterator;
import com.rapiddweller.format.script.ScriptConverterForStrings;
import com.rapiddweller.format.util.AbstractDataSource;
import com.rapiddweller.platform.mongodb.MongoDBUtils;
import com.rapiddweller.platform.mongodb.client.MongoDBClientProvider;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MongoDBDataSource extends AbstractDataSource<Document> {

    private static final List<String> MONGODB_COMMANDS = Arrays.asList(
            "find", "aggregate", "count", "distinct", "mapReduce");

    private final MongoDBClientProvider mongoDBClientProvider;
    private String collection;
    private final String rawQuery;
    private final Converter<String, Object> scriptConverterForStrings;

    public MongoDBDataSource(MongoDBClientProvider connection, String collection, String query, Context context) {
        super(Document.class);
        this.rawQuery = query;
        this.mongoDBClientProvider = connection;
        this.collection = collection;
        this.scriptConverterForStrings = new ScriptConverterForStrings(context);
    }

    @Override
    public DataIterator<Document> iterator() {
        Document mongoQuery;
        if (Objects.isNull(rawQuery)) {
            mongoQuery = MongoDBUtils.toDocument(String.format("'find': '%s'", collection));
        } else {
            mongoQuery = MongoDBUtils.toDocument(scriptConverterForStrings.convert(rawQuery).toString());
            collection = getCollection(mongoQuery);
        }
        return new MongoDBRunCommandIterator(this.mongoDBClientProvider.createMongoDBClient(), this.collection, mongoQuery);
    }

    private String getCollection(Document mongoQuery) {
        if (Objects.isNull(collection)) {
            for (String command : MONGODB_COMMANDS) {
                if (mongoQuery.containsKey(command)) {
                    return mongoQuery.get(command).toString();
                }
            }
        }
        return collection;
    }
}
