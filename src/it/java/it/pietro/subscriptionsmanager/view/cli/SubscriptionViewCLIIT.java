package it.pietro.subscriptionsmanager.view.cli;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import it.pietro.subscriptionsmanager.controller.SubscriptionController;
import it.pietro.subscriptionsmanager.model.Subscription;
import it.pietro.subscriptionsmanager.repository.SubscriptionRepository;
import it.pietro.subscriptionsmanager.repository.mongo.SubscriptionMongoRepository;

public class SubscriptionViewCLIIT {
	@ClassRule
	public static final MongoDBContainer mongo =new MongoDBContainer("mongo:4.2.3").withExposedPorts(27017);
	
	private MongoClient client;
	
	private SubscriptionViewCLI cliView;
	private SubscriptionController controller;
	private SubscriptionRepository repository;
	
	private static final String DB_NAME = "subscriptionsmanager";
	private static final String DB_COLLECTION = "subscriptions";
	
	private static final Subscription SUBSCRIPTION_FIXTURE = new Subscription("1", "Netflix", 1.0, "Monthly");
	private static final Subscription SUBSCRIPTION_FIXTURE2 = new Subscription("2", "Test", 1.0, "Monthly");
	
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	
	@Before
	public void setUp() {
		client = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(), mongo.getMappedPort(27017)));	
		repository = new SubscriptionMongoRepository(client, DB_NAME, DB_COLLECTION);
		client.getDatabase(DB_NAME).drop();
		cliView = new SubscriptionViewCLI(new PrintStream(outContent));
		controller = new SubscriptionController(repository, cliView);
		cliView.setController(controller);
	}
	
	@After
	public void tearDown() {
		client.close();
	}
	
	@Test
	public void testAllSubscriptions() {
		repository.save(SUBSCRIPTION_FIXTURE);
		repository.save(SUBSCRIPTION_FIXTURE2);
		controller.allSubscriptions();
		assertThat(outContent.toString())
			.hasToString("All subscriptions:\n"+SUBSCRIPTION_FIXTURE.toString()+"\n"+SUBSCRIPTION_FIXTURE2.toString()+"\n");
	}
	
	
}
