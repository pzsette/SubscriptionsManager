package it.pietro.subscriptionsmanager.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;

import com.mongodb.ServerAddress;

import it.pietro.subscriptionsmanager.model.Subscription;
import it.pietro.subscriptionsmanager.repository.SubscriptionRepository;
import it.pietro.subscriptionsmanager.repository.mongo.SubscriptionMongoRepository;
import it.pietro.subscriptionsmanager.view.SubscriptionView;

public class SubscriptionControllerIT {
	@ClassRule
	public static final MongoDBContainer mongo =new MongoDBContainer("mongo:4.2.3").withExposedPorts(27017);
	
	@Mock
	private SubscriptionView view;
	private SubscriptionRepository repository;
	private SubscriptionController controller;
	private MongoClient client;
	
	private static final String DB_NAME = "subscriptionsmanager";
	private static final String DB_COLLECTION = "subscriptions";
	
	private static final Subscription SUBSCRIPTION_FIXTURE = new Subscription("1", "Netflix", 1.0, "Month");
	
	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		client = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(), mongo.getMappedPort(27017)));	
		repository = new SubscriptionMongoRepository(client, DB_NAME, DB_COLLECTION);
		client.getDatabase(DB_NAME).drop();
		controller = new SubscriptionController(repository, view);
	}
	
	@After
	public void tearDown() {
		client.close();
	}

	@Test
	public void testAllSubscriptions() {
		repository.save(SUBSCRIPTION_FIXTURE);
		controller.allSubscriptions();
		verify(view).showAllSubscriptions(asList(SUBSCRIPTION_FIXTURE));
	}
	
	@Test
	public void testAddSubscription() {
		controller.addSubscription(SUBSCRIPTION_FIXTURE);
		verify(view)
			.subscriptionAdded(SUBSCRIPTION_FIXTURE);
	}
	
	@Test
	public void testDeleteSubscription() {
		repository.save(SUBSCRIPTION_FIXTURE);
		controller.deleteSubscription(SUBSCRIPTION_FIXTURE);
		verify(view)
			.subscriptionRemoved(SUBSCRIPTION_FIXTURE);
	}

}
