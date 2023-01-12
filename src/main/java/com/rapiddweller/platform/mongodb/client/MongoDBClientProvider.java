package com.rapiddweller.platform.mongodb.client;

import com.mongodb.AuthenticationMechanism;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoDBClientProvider {
    Logger logger = LoggerFactory.getLogger(getClass());

    private final String database;

    private final String authenticationDatabase;

    private final MongoCredential credential;
    private final MongoClientOptions options;
    private final ServerAddress serverAddress;

    public MongoDBClientProvider(String host, int port, String database, String user, String password, String authenticationDatabase, String authenticationMechanism) {
        this.database = database;
        this.authenticationDatabase = authenticationDatabase;
        //authentication type  based credentials
        switch (authenticationMechanism) {
            case "GSSAPI":
                this.credential = MongoCredential.createGSSAPICredential(user);
                break;
            case "MONGODB-X509":
                this.credential = MongoCredential.createMongoX509Credential(user);
                break;
            case "PLAIN":
                this.credential = MongoCredential.createPlainCredential(user, authenticationDatabase, password.toCharArray());
                break;
            case "SCRAM-SHA-1":
                this.credential = MongoCredential.createScramSha1Credential(user, authenticationDatabase, password.toCharArray());
                break;
            case "SCRAM-SHA-256":
                this.credential = MongoCredential.createScramSha256Credential(user, authenticationDatabase, password.toCharArray());
                break;
            default:
                logger.warn("Authentication mechanism {} not supported. Using default mechanism", authenticationMechanism);
                this.credential = MongoCredential.createCredential(user, authenticationDatabase, password.toCharArray());
                break;
        }
        // create configurable MongoClientOptions builder
        this.options = MongoClientOptions.builder().build();
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
