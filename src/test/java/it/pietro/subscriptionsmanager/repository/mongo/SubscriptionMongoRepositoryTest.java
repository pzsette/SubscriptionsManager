package it.pietro.subscriptionsmanager.repository.mongo;

import static org.junit.Assert.*;

import org.bson.Document;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.ServerAddress;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.net.InetSocketAddress;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import it.pietro.subscriptionsmanager.repository.mongo.SubscriptionMongoRepository;

public class SubscriptionMongoRepositoryTest {
	
	private static MongoServer server;
	private static InetSocketAddress serverAddress;
	
	private MongoClient client;
	private SubscriptionMongoRepository repository;
	private MongoCollection<Document> collection;
	
	@BeforeClass
	public static void setupServer() {
		server = new MongoServer(new MemoryBackend());
		serverAddress = server.bind();
	}
	
	@AfterClass
	public static void shutdownServer() {
		server.shutdown();
	}
	
	@Before
	public void setup() {
		client = new MongoClient(new ServerAddress(serverAddress));
		repository = new SubscriptionMongoRepository(client, "app", "subscriptions");
		MongoDatabase database = client.getDatabase("app");
		database.drop();
		collection = database.getCollection("subscriptions");
	}
	
	@After
	public void tearDown() {
		client.close();
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
