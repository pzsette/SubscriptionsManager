package it.pietro.subscriptionsmanager.repository.mongo;

import static org.assertj.core.api.Assertions.assertThat;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.testcontainers.containers.GenericContainer;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import it.pietro.subscriptionsmanager.model.Subscription;

public class SubscriptionMongoRepositoryTestcontainersIT {

	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final GenericContainer mongo =
		new GenericContainer("mongo:4.2.3")
			.withExposedPorts(27017);
	
	private MongoClient client;
	private SubscriptionMongoRepository repository;
	private MongoCollection<Document> collection;
	
	private static final String DB_NAME = "subscriptionsmanager";
	private static final String DB_COLLECTION = "subscriptions";
	
	private static final Subscription SUBSCRIPTION_FIXTURE = new Subscription("1", "Netflix", 1.0, "Month");
	private static final Subscription SUBSCRIPTION_FIXTURE2 = new Subscription("2", "Test", 1.0, "Month");
	
	@Before
	public void setup() {
		client = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(), mongo.getMappedPort(27017)));	
		repository = new SubscriptionMongoRepository(client, DB_NAME, DB_COLLECTION);
		MongoDatabase database = client.getDatabase(DB_NAME);
		database.drop();
		collection = database.getCollection(DB_COLLECTION);
	}
	
	@After
	public void tearDown() {
		client.close();
	}
	
	
	@Test
	public void testFindAllWhenDBIsNotEmpty() {
		addTestSubscriptiontToDatabase(SUBSCRIPTION_FIXTURE);
		addTestSubscriptiontToDatabase(SUBSCRIPTION_FIXTURE2);
		assertThat(repository.findAll())
			.containsExactly(
				SUBSCRIPTION_FIXTURE,
				SUBSCRIPTION_FIXTURE2);
	}
	
	@Test
	public void testFindAllWhenDBIsEmpty() {
		assertThat(repository.findAll()).isEmpty();
	}
	
	@Test
	public void testFindById() {
		addTestSubscriptiontToDatabase(SUBSCRIPTION_FIXTURE);
		assertThat(repository.findById(SUBSCRIPTION_FIXTURE.getId()))
			.isEqualTo(SUBSCRIPTION_FIXTURE);
	}
	
	@Test 
	void testFindByIdWhenSubNotInDB() {
		assertThat(repository.findById(SUBSCRIPTION_FIXTURE.getId()))
			.isNull();		
	}
	
	private void addTestSubscriptiontToDatabase(Subscription sub) {
		collection.insertOne(
				new Document()
					.append("id", sub.getId())
					.append("name", sub.getName())
					.append("price", sub.getPrice())
					.append("repetition", sub.getRepetition()));
	}

}
