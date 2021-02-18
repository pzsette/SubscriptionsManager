package it.pietro.subscriptionsmanager.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;

import it.pietro.subscriptionsmanager.model.Subscription;

public class SubscriptionManagerAppCLIE2E {
	
	@ClassRule
	public static final MongoDBContainer mongo =new MongoDBContainer("mongo:4.2.3").withExposedPorts(27017);
	
	private static final String DB_NAME = "test-db";
	private static final String DB_COLLECTION = "test-collection";
	
	public static BufferedReader reader;
	public static BufferedWriter writer;
	
	private static final String EOL = System.getProperty("line.separator");
	
	private MongoClient client;
	
	private final CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
			fromProviders(PojoCodecProvider.builder().automatic(true).build()));
	
	@Before
	public void onSetUp() throws Exception {
		String containerIpAddress = mongo.getContainerIpAddress();
		Integer mappedPort = mongo.getMappedPort(27017);
		client = new MongoClient(new ServerAddress(containerIpAddress, mappedPort));
		client.getDatabase(DB_NAME).drop();
		
		addTestSubToDatabase(new Subscription("1", "Netflix", 1.0, "Monthly"));
		addTestSubToDatabase(new Subscription("2", "Test", 4.0, "Weekly"));
		Process process = new ProcessBuilder(
							"java", "-jar", "./target/subscriptionsmanager-0.0.1-SNAPSHOT-jar-with-dependencies.jar",
							"--mongo-host=" + containerIpAddress, 
							"--mongo-port=" + mappedPort.toString(),
							"--db-name=" + DB_NAME,
							"--db-collection=" + DB_COLLECTION,
							"--ui=cli").start();
        reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
	}

	@After
	public void tearDown() throws Exception {
		client.close();
	}
	
	@Test
	public void testOnStartAllDatabaseElementsAreLoaded() throws Exception {
		String output = getOutputWithGivenInput("1"+EOL+"5"+EOL+"");
		assertThat(output)
			.contains("Netflix", "1.0", "Monthly")
			.contains("Test", "4.0", "Weekly");
	}
	
	@Test
	public void testAddSubscriptionSucces() throws Exception {
		String output = getOutputWithGivenInput("3"+EOL+"3"+EOL+"TestSub"+EOL+"12"+EOL+"3"+EOL+"1"+EOL+"2"+EOL+"5");
		assertThat(output)
			.contains("TestSub", "12", "Annual")
			.contains("Total monthly spending: 18.0");
	}
	
	@Test
	public void testAddSubscriptionError() throws Exception {
		String output = getOutputWithGivenInput("3"+EOL+"1"+EOL+"TestSub"+EOL+"12"+EOL+"3"+EOL+"5");
		assertThat(output)
			.contains("Error: Already existing subscription with id 1");
	}
	
	@Test
	public void testRemoveSubscriptionSucces() throws Exception {
		String output = getOutputWithGivenInput("4"+EOL+"1"+EOL+"5");
		assertThat(output)
			.contains("Subscription [id= 1, name= Netflix, price= 1.0, repetition= Monthly] removed");
	}
	
	@Test
	public void testRemoveSubscriptionError() throws Exception {
		String output = getOutputWithGivenInput("4"+EOL+"8"+EOL+"5");
		assertThat(output)
			.contains("Error: No existing subscription with id 8");
	}
	
	private String getOutputWithGivenInput(String input) throws Exception {
		writer.write(input);
		writer.close();
		StringBuilder builder = new StringBuilder();
		String line = "";
		while ( (line = reader.readLine()) != null ) {
			builder.append(line+System.lineSeparator());
		}
		return builder.toString();
	}
	
	private void addTestSubToDatabase(Subscription sub) {
		client
			.getDatabase(DB_NAME)
			.getCollection(DB_COLLECTION, Subscription.class)
			.withCodecRegistry(pojoCodecRegistry)
			.insertOne(sub);
	}
}
