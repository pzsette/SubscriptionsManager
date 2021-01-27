package it.pietro.subscriptionsmanager.view.swing;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MongoDBContainer;
import com.mongodb.MongoClient;

import com.mongodb.ServerAddress;

import it.pietro.subscriptionsmanager.controller.SubscriptionController;
import it.pietro.subscriptionsmanager.model.Subscription;
import it.pietro.subscriptionsmanager.repository.SubscriptionRepository;
import it.pietro.subscriptionsmanager.repository.mongo.SubscriptionMongoRepository;

@RunWith(GUITestRunner.class)
public class SubscriptionViewSwingIT extends AssertJSwingJUnitTestCase {
	@ClassRule
	public static final MongoDBContainer mongo =new MongoDBContainer("mongo:4.2.3").withExposedPorts(27017);
	
	private MongoClient client;
	private FrameFixture window;
	
	private SubscriptionViewSwing swingView;
	private SubscriptionController controller;
	private SubscriptionRepository repository;
	
	private static final String DB_NAME = "subscriptionsmanager";
	private static final String DB_COLLECTION = "subscriptions";
	
	private static final Subscription SUBSCRIPTION_FIXTURE = new Subscription("1", "Netflix", 1.0, "Monthly");
	private static final Subscription SUBSCRIPTION_FIXTURE2 = new Subscription("2", "Test", 1.0, "Monthly");
	
	@Override
	protected void onSetUp() throws Exception {
		client = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(), mongo.getMappedPort(27017)));	
		repository = new SubscriptionMongoRepository(client, DB_NAME, DB_COLLECTION);
		client.getDatabase(DB_NAME).drop();
		GuiActionRunner.execute(()->{
			swingView = new SubscriptionViewSwing();
			controller = new SubscriptionController(repository, swingView);
			swingView.setController(controller);
			return swingView;
		});
		window = new FrameFixture(robot(), swingView);
		window.show();
	}
	
	@Override
	@After
	public void onTearDown() {
		client.close();
	}
	
	@Test @GUITest
	public void testAllSubscriptions() {
		repository.save(SUBSCRIPTION_FIXTURE);
		repository.save(SUBSCRIPTION_FIXTURE2);
		GuiActionRunner.execute(() -> controller.allSubscriptions());
		assertThat(window.list().contents())
			.containsExactly(SUBSCRIPTION_FIXTURE.toString(), SUBSCRIPTION_FIXTURE2.toString());
	}
	
	@Test @GUITest
	public void testAddButtonSuccess() {
		window.textBox("idTextField").enterText("1");
		window.textBox("nameTextField").enterText("Netflix");
		window.textBox("priceTextField").enterText("1");
		window.comboBox("repetitionDropDown").selectItem("Monthly");
		window.button(JButtonMatcher.withName("addBtn")).click();
		assertThat(window.list().contents())
			.containsExactly(SUBSCRIPTION_FIXTURE.toString());
	}
	
	@Test @GUITest
	public void testAddButtonError() {
		repository.save(SUBSCRIPTION_FIXTURE);
		window.textBox("idTextField").enterText("1");
		window.textBox("nameTextField").enterText("Netflix");
		window.textBox("priceTextField").enterText("1");
		window.comboBox("repetitionDropDown").selectItem("Monthly");
		window.button("addBtn").click();
		assertThat(window.list().contents()).isEmpty();
		window.label("errorLbl").requireText("Error: Already existing subscription with id 1");
	}
	
	@Test @GUITest
	public void testDeleteButtonSuccess() {
		GuiActionRunner.execute(() -> controller.addSubscription(SUBSCRIPTION_FIXTURE));
		window.list().selectItem(0);
		window.button("deleteBtn").click();
		assertThat(window.list().contents()).isEmpty();
	}
	
	@Test @GUITest
	public void testDeleteButtonError() {
		GuiActionRunner.execute(() -> swingView.getListSubscriptionModel().addElement(SUBSCRIPTION_FIXTURE));
		window.list().selectItem(0);
		window.button("deleteBtn").click();
		window.label("errorLbl").requireText("Error: No existing subscription with id 1");
	}

}
