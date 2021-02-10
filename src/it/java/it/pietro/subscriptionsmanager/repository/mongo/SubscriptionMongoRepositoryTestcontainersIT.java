package it.pietro.subscriptionsmanager.repository.mongo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.testcontainers.containers.MongoDBContainer;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import it.pietro.subscriptionsmanager.model.Subscription;

public class SubscriptionMongoRepositoryTestcontainersIT {

	@ClassRule
	public static final MongoDBContainer mongo =new MongoDBContainer("mongo:4.2.3").withExposedPorts(27017);
	
	private MongoClient client;
	private SubscriptionMongoRepository repository;
	private MongoCollection<Subscription> collection;
	
	private static final String DB_NAME = "subscriptionsmanager";
	private static final String DB_COLLECTION = "subscriptions";
	
	private static final Subscription SUBSCRIPTION_FIXTURE = new Subscription("1", "Netflix", 1.0, "Month");
	private static final Subscription SUBSCRIPTION_FIXTURE2 = new Subscription("2", "Test", 1.0, "Month");
	
	@Before
	public void setup() {
		
		CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().automatic(true).build()));
		
		client = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(), mongo.getMappedPort(27017)));	
		repository = new SubscriptionMongoRepository(client, DB_NAME, DB_COLLECTION);
		MongoDatabase database = client.getDatabase(DB_NAME);
		database.drop();
		collection = database
				.getCollection(DB_COLLECTION, Subscription.class)
				.withCodecRegistry(pojoCodecRegistry);
	}
	
	@After
	public void tearDown() {
		client.close();
	}
	
	
	@Test
	public void testFindAll() {
		collection.insertOne(SUBSCRIPTION_FIXTURE);
		collection.insertOne(SUBSCRIPTION_FIXTURE2);
		assertThat(repository.findAll())
			.containsExactly(
				SUBSCRIPTION_FIXTURE,
				SUBSCRIPTION_FIXTURE2);
	}
	
	@Test
	public void testFindById() {
		collection.insertOne(SUBSCRIPTION_FIXTURE);
		assertThat(repository.findById(SUBSCRIPTION_FIXTURE.getId()))
			.isEqualTo(SUBSCRIPTION_FIXTURE);
	}
	
	@Test
	public void testSave() {
		repository.save(SUBSCRIPTION_FIXTURE);
		assertThat(readAllSubscriptionFormDB())
			.containsExactly(SUBSCRIPTION_FIXTURE);
	}
	
	@Test
	public void testDelete() {
		collection.insertOne(SUBSCRIPTION_FIXTURE);
		repository.delete(SUBSCRIPTION_FIXTURE.getId());
		assertThat(readAllSubscriptionFormDB())
			.isEmpty();
	}
	
	/*private void addTestSubscriptionToDatabase(Subscription sub) {
		collection.insertOne(sub);
	}*/
	
	private List<Subscription> readAllSubscriptionFormDB() {
		return StreamSupport
				.stream(collection.find().spliterator(), false)
				//.map(d -> new Subscription(""+d.get("id"), ""+d.get("name"), (double)d.get("price"), ""+d.get("repetition")))
				.collect(Collectors.toList());
	}

}
