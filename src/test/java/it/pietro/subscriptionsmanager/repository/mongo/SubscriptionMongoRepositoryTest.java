package it.pietro.subscriptionsmanager.repository.mongo;

import static org.assertj.core.api.Assertions.*;

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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import it.pietro.subscriptionsmanager.model.Subscription;

public class SubscriptionMongoRepositoryTest {
	
	private static MongoServer server;
	private static InetSocketAddress serverAddress;
	
	private MongoClient client;
	private SubscriptionMongoRepository repository;
	private MongoCollection<Document> collection;
	
	private static final Subscription SUBSCRIPTION_FIXTURE = new Subscription("1", "Netflix", 1.0, "Month");
	private static final Subscription SUBSCRIPTION_FIXTURE2 = new Subscription("2", "Test", 1.0, "Month");
	
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

	public void testFindAllWhenDatabaseIsEmpty() {
		assertThat(repository.findAll()).isEmpty();
	}
	
	@Test
	public void testFindAllWhenDatabaseIsNotEmpty() {
		addTestSubscriptiontToDatabase(SUBSCRIPTION_FIXTURE);
		addTestSubscriptiontToDatabase(SUBSCRIPTION_FIXTURE2);
		assertThat(repository.findAll())
				.containsExactly(
						SUBSCRIPTION_FIXTURE,
						SUBSCRIPTION_FIXTURE2);
	}
	
	@Test
	public void testFindByIdWhenSubscriptionFound() {
		addTestSubscriptiontToDatabase(SUBSCRIPTION_FIXTURE2);
		assertThat(repository.findById(SUBSCRIPTION_FIXTURE2.getId()))
				.isEqualTo(SUBSCRIPTION_FIXTURE2);
	}
	
	@Test
	public void testFindByWhenSubscriptionNotFound() {
		assertThat(repository.findById(SUBSCRIPTION_FIXTURE.getId()))
				.isNull();
	}
	
	@Test
	public void testSave() {
		repository.save(SUBSCRIPTION_FIXTURE);
		assertThat(readAllSubscriptionFormDB())
			.containsExactly(SUBSCRIPTION_FIXTURE);
				
	}
	
	@Test
	public void testDelete() {
		addTestSubscriptiontToDatabase(SUBSCRIPTION_FIXTURE);
		repository.delete(SUBSCRIPTION_FIXTURE.getId());
		assertThat(readAllSubscriptionFormDB()).isEmpty();
	}
	
	private void addTestSubscriptiontToDatabase(Subscription sub) {
		collection.insertOne(
				new Document()
					.append("id", sub.getId())
					.append("name", sub.getName())
					.append("price", sub.getPrice())
					.append("repetition", sub.getRepetition()));
	}
	
	private List<Subscription> readAllSubscriptionFormDB() {
		return StreamSupport
				.stream(collection.find().spliterator(), false)
				.map(d -> new Subscription(""+d.get("id"), ""+d.get("name"), (double)d.get("price"), ""+d.get("repetition")))
				.collect(Collectors.toList());
	}

}
