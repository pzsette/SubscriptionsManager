package it.pietro.subscriptionsmanager.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;
import static org.junit.Assert.*;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.model.Filters;

import it.pietro.subscriptionsmanager.model.Subscription;

public class SubscriptionManagerAppCLIE2E {
	
	@ClassRule
	public static final MongoDBContainer mongo =new MongoDBContainer("mongo:4.2.3").withExposedPorts(27017);
	
	private static final String DB_NAME = "test-db";
	private static final String DB_COLLECTION = "test-collection";
	
	private static final Subscription SUBSCRIPTION_FIXTURE = new Subscription("1", "Netflix", 1.0, "Monthly");
	private static final Subscription SUBSCRIPTION_FIXTURE2 = new Subscription("2", "Test", 4.0, "Weekly");
	
	private MongoClient client;
	
	@Before
	protected void onSetUp() throws Exception {
		String containerIpAddress = mongo.getContainerIpAddress();
		Integer mappedPort = mongo.getMappedPort(27017);
		client = new MongoClient(new ServerAddress(containerIpAddress, mappedPort));	
		client.getDatabase(DB_NAME).drop();
		addTestSubToDatabase(SUBSCRIPTION_FIXTURE);
		addTestSubToDatabase(SUBSCRIPTION_FIXTURE2);
		application("it.pietro.subscriptionsmanager.app.SubscriptionManagerApp")
			.withArgs(
					"--mongo-host=" + containerIpAddress,
					"--mongo-port=" + mappedPort.toString(),
					"--db-name=" + DB_NAME,
					"--db-collection=" + DB_COLLECTION,
					"--ui=cli"
			)
			.start();
	}

	@After
	public void tearDown() throws Exception {
		client.close();
	}
	
	@Test @GUITest
	public void testOnStartAllDatabaseElementsAreLoaded() {
		
	}
	
	private void addTestSubToDatabase(Subscription sub) {
		client
			.getDatabase(DB_NAME)
			.getCollection(DB_COLLECTION)
			.insertOne(new Document()
				.append("id", sub.getId())
				.append("name", sub.getName())
				.append("price", sub.getPrice())
				.append("repetition", sub.getRepetition()));
	}
	
	private void removeSubFromDatabase(String id) {
		client
			.getDatabase(DB_NAME)
			.getCollection(DB_COLLECTION)
			.deleteOne(Filters.eq("id", id));	
	}

}
