package com.rapiddweller.platform.mongodb.client;

import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

public class MongoDBClientProvider {

    private final String database;

    private final MongoCredential credential;
    private final MongoClientOptions options;
    private final ServerAddress serverAddress;

    public MongoDBClientProvider(String host, int port, String database, String user, String password) {
        this.database = database;
        this.credential = MongoCredential.createCredential(user, database, password.toCharArray());
        this.options = MongoClientOptions.builder().build(); //TODO make configurable
        this.serverAddress = new ServerAddress(host, port);

    }

    public MongoDBClient createMongoDBClient() {
        return new MongoDBClient(this);
    }

    public String getDatabase() {
        return database;
    }

    public MongoCredential getCredential() {
        return credential;
    }

    public MongoClientOptions getOptions() {
        return options;
    }

    public ServerAddress getServerAddress() {
        return serverAddress;
    }

}
