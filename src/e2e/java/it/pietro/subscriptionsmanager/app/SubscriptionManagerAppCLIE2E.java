package it.pietro.subscriptionsmanager.app;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

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
							"java", "-jar", "./target/subscriptionsmanager-0.0.1-SNAPSHOT-jar-with-dependencies.jar",
							"--mongo-host=" + containerIpAddress, 
							"--mongo-port=" + mappedPort.toString(),
							"--db-name=" + DB_NAME,
							"--db-collection=" + DB_COLLECTION,
							"--ui=cli");
		
		Process process = pBuilder.start();
		
        reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        
		String line = null;
		boolean initFinished = false;
		while (((line = reader.readLine()) != null) & !initFinished) {
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
	
	@Test
	public void testOnStartAllDatabaseElementsAreLoaded() throws Exception {

		writer.write("1\n5\n");
		writer.flush();
		writer.close();
		
		StringBuilder builder = new StringBuilder();
		String line = "";
		while ( (line = reader.readLine()) != null ) {
			builder.append(line+System.lineSeparator());
		}
		String[] result = builder.toString().split(System.lineSeparator());
		assertThat(result[1]).contains("Netflix", "1.0", "Monthly");
		assertThat(result[2]).contains("Test", "4.0", "Weekly");
	}
	
	@Test
	public void testAddSubscriptionSucces() throws Exception {
		
		writer.write("3\n3\nTestSub\n12\n3\n1\n2\n5");
		writer.flush();
		writer.close();
		
		StringBuilder builder = new StringBuilder();
		String line = "";
		while ( (line = reader.readLine()) != null ) {
			builder.append(line+System.lineSeparator());
		}
		String result = builder.toString();
		assertThat(result)
			.contains("TestSub", "12", "Annual")
			.contains("Total monthly spending: 18.0");
	}
	
	@Test
	public void testAddSubscriptionError() throws Exception {
		
		writer.write("3\n1\nTestSub\n12\n3\n5");
		writer.flush();
		writer.close();
		
		StringBuilder builder = new StringBuilder();
		String line = "";
		while ( (line = reader.readLine()) != null ) {
			builder.append(line+System.lineSeparator());
		}
		String result = builder.toString();
		System.out.println(result);
		assertThat(result)
			.contains("Error: Already existing subscription with 1");
	}
	
	@Test
	public void testRemoveSubscriptionSucces() throws Exception {
		
		writer.write("4\n1\n1\n5");
		writer.flush();
		writer.close();
		
		StringBuilder builder = new StringBuilder();
		String line = "";
		while ( (line = reader.readLine()) != null ) {
			builder.append(line+System.lineSeparator());
		}
		String result = builder.toString();
		assertThat(result)
			.doesNotContain("Netflix", "Monthly");
	}
	
	@Test
	public void testRemoveSubscriptionError() throws Exception {
		
		writer.write("4\n8\n5");
		writer.flush();
		writer.close();
		
		StringBuilder builder = new StringBuilder();
		String line = "";
		while ( (line = reader.readLine()) != null ) {
			builder.append(line+System.lineSeparator());
		}
		String result = builder.toString();
		assertThat(result)
			.contains("Error: No existing subscription with id 1");
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

}
