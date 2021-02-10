package it.pietro.subscriptionsmanager.app;

import javax.swing.JFrame;

import java.util.regex.Pattern;
import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.*;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.model.Filters;

import it.pietro.subscriptionsmanager.model.Subscription;

@RunWith(GUITestRunner.class) 
public class SubscriptionManagerAppSwingE2E extends AssertJSwingJUnitTestCase {
	
	@ClassRule
	public static final MongoDBContainer mongo =new MongoDBContainer("mongo:4.2.3").withExposedPorts(27017);
	
	private static final String DB_NAME = "test-db";
	private static final String DB_COLLECTION = "test-collection";
	
	private static final Subscription SUBSCRIPTION_FIXTURE = new Subscription("1", "Netflix", 1.0, "Monthly");
	private static final Subscription SUBSCRIPTION_FIXTURE2 = new Subscription("2", "Test", 4.0, "Weekly");
	
	private final CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
			fromProviders(PojoCodecProvider.builder().automatic(true).build()));
	
	private FrameFixture window;
	
	private MongoClient client;

	@Override
	protected void onSetUp() throws Exception {
		String containerIpAddress = mongo.getHost();
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
					"--ui=gui"
			)
			.start();
		
		window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
			@Override
			protected boolean isMatching(JFrame jFrame) {
				return "Subscriptions manager".equals(jFrame.getTitle()) && jFrame.isShowing();
			}
		}).using(robot());
		
		robot().waitForIdle();
	}
	
	@Override
	protected void onTearDown() {
		client.close();
	}
	
	@Test @GUITest
	public void testOnStartAllDatabaseElementsAreShown() {
		assertThat(window.list().contents())
			.anySatisfy(e -> assertThat(e).contains(
					SUBSCRIPTION_FIXTURE.getName(), 
					SUBSCRIPTION_FIXTURE.getPrice().toString(), 
					SUBSCRIPTION_FIXTURE.getRepetition()
			))
			.anySatisfy(e -> assertThat(e).contains(
					SUBSCRIPTION_FIXTURE2.getName(),
					SUBSCRIPTION_FIXTURE2.getPrice().toString(), 
					SUBSCRIPTION_FIXTURE2.getRepetition()
			));
		window.label(JLabelMatcher.withName("amountTextLabel")).requireText("17.0");
	}
	
	@Test @GUITest
	public void testAddSubscriptionSucces() {
		window.textBox("idTextField").enterText("3");
		window.textBox("nameTextField").enterText("TestSub");
		window.textBox("priceTextField").enterText("12");
		window.comboBox("repetitionDropDown").selectItem("Annual");
		window.button(JButtonMatcher.withText("Add subscription")).click();
		assertThat(window.list().contents())
			.anySatisfy(e -> assertThat(e).contains("3", "TestSub", "12", "Annual"));
		window.label(JLabelMatcher.withName("amountTextLabel")).requireText("18.0");
	}
	
	@Test @GUITest
	public void testAddSubscriptionError() {
		window.textBox("idTextField").enterText("1");
		window.textBox("nameTextField").enterText("TestSub");
		window.textBox("priceTextField").enterText("12");
		window.comboBox("repetitionDropDown").selectItem("Annual");
		window.button(JButtonMatcher.withText("Add subscription")).click();
		assertThat(window.label("errorLbl").text())
			.contains(SUBSCRIPTION_FIXTURE.getId());	
	}
	
	@Test @GUITest
	public void testRemoveButtonSucces() {
		window.list()
			.selectItem(1);
		window.button(JButtonMatcher.withText("Delete selected")).click();
		assertThat(window.list().contents())
			.noneMatch(e -> e.contains(SUBSCRIPTION_FIXTURE2.getName()));
		window.label(JLabelMatcher.withName("amountTextLabel")).requireText("1.0");
	}
	
	@Test @GUITest
	public void testRemoveButtonError() {
		window.list("subscriptionList")
			.selectItem(Pattern.compile(".*" + SUBSCRIPTION_FIXTURE2.getName() + ".*"));
		removeSubFromDatabase(SUBSCRIPTION_FIXTURE2.getId());
		window.button(JButtonMatcher.withText("Delete selected")).click();
		assertThat(window.label("errorLbl").text())
			.contains(SUBSCRIPTION_FIXTURE2.getId());
	}
	
	private void addTestSubToDatabase(Subscription sub) {
		client
			.getDatabase(DB_NAME)
			.getCollection(DB_COLLECTION, Subscription.class)
			.withCodecRegistry(pojoCodecRegistry)
			.insertOne(sub);
	}
	
	private void removeSubFromDatabase(String id) {
		client
			.getDatabase(DB_NAME)
			.getCollection(DB_COLLECTION)
			.withCodecRegistry(pojoCodecRegistry)
			.deleteOne(Filters.eq("_id", id));	
	}

}
