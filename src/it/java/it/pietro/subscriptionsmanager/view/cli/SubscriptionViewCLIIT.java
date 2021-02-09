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
	
	private static final String EOL = System.getProperty("line.separator");
	
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
		assertThat(cliView.getList())
			.containsExactly(SUBSCRIPTION_FIXTURE,SUBSCRIPTION_FIXTURE2);
	}
	
	@Test
	public void testAddSubscriptionSucces() {
		String input = "3"+EOL+"1"+EOL+"Netflix"+EOL+"1.0"+EOL+"2"+EOL+"5"+EOL+"";
		System.setIn(new ByteArrayInputStream(input.getBytes()));
		cliView.runView();
		assertThat(outContent.toString())
			.contains("Subscription [id= 1, name= Netflix, price= 1.0, repetition= Monthly] added");
	}
	
	@Test
	public void testAddSubscriptionError() {
		repository.save(SUBSCRIPTION_FIXTURE);
		String input = "3"+EOL+"1"+EOL+"TestFail"+EOL+"1.0"+EOL+"2"+EOL+"5"+EOL;
		System.setIn(new ByteArrayInputStream(input.getBytes()));
		cliView.runView();
		assertThat(outContent.toString())
			.contains("Error: Already existing subscription with id 1");
	}
	
	@Test
	public void testRemoveSubscriptionSucces() {
		controller.addSubscription(SUBSCRIPTION_FIXTURE);
		String input = "4"+EOL+"1"+EOL+"5";
		System.setIn(new ByteArrayInputStream(input.getBytes()));
		cliView.runView();
		assertThat(outContent.toString())
			.contains("Subscription [id= 1, name= Netflix, price= 1.0, repetition= Monthly] removed");	
	}
	
	@Test
	public void testRemoveSubscriptionError() {
		cliView.getList().add(SUBSCRIPTION_FIXTURE);
		String input = "4"+EOL+"1"+EOL+"5";
		System.setIn(new ByteArrayInputStream(input.getBytes()));
		cliView.runView();
		assertThat(outContent.toString())
			.contains("Error: No existing subscription with id 1");	
	}
	
	
}
