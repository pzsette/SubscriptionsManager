package it.pietro.subscriptionsmanager.app;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.assertj.swing.annotation.GUITest;
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
	
	public static BufferedReader reader;
	public static BufferedWriter writer;
	
	private MongoClient client;
	
	@Before
	public void onSetUp() throws Exception {
		String containerIpAddress = mongo.getContainerIpAddress();
		Integer mappedPort = mongo.getMappedPort(27017);
		client = new MongoClient(new ServerAddress(containerIpAddress, mappedPort));
		client.getDatabase(DB_NAME).drop();
		addTestSubToDatabase(SUBSCRIPTION_FIXTURE);
		addTestSubToDatabase(SUBSCRIPTION_FIXTURE2);
		ProcessBuilder pBuilder = new ProcessBuilder(
							"java",
							"-jar",
							"./target/subscriptionsmanager-0.0.1-SNAPSHOT-jar-with-dependencies.jar",
							"--ui=cli");
		//pBuilder.redirectErrorStream(true);
		Process process = pBuilder.start();
		
        reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        
        //System.out.println(reader.readLine());
		String line = null;
		boolean initFinished = false;
		while (((line = reader.readLine()) != null) & !initFinished) {
			System.out.println("ProcessOut: " + line);
			if (line.contains("Exit")) {
				initFinished = true;
				return;
			}
		}
	}

	@After
	public void tearDown() throws Exception {
		client.close();
	}
	
	@Test @GUITest
	public void testOnStartAllDatabaseElementsAreLoaded() throws Exception {

		writer.write("1\n5");
		writer.flush();
		writer.close();
		
		StringBuilder builder = new StringBuilder();
		String line = null;
		while ( (line = reader.readLine()) != null) {
			builder.append(line);
		}
		System.out.println(builder.toString());
		assertThat(builder.toString()).contains("Exit");
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
