package it.pietro.subscriptionsmanager.view.cli;

import static org.assertj.core.api.Assertions.assertThat;
import static java.util.Arrays.asList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import it.pietro.subscriptionsmanager.controller.SubscriptionController;
import it.pietro.subscriptionsmanager.model.Subscription;

public class SubscriptionViewCLITest {
	
	@Mock
	private SubscriptionController controller;
	
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	
	private static final String EOL = System.getProperty("line.separator");
	
	SubscriptionViewCLI cliView; 

	private static final Subscription SUBSCRIPTION_FIXTURE = new Subscription("1", "Netflix", 1.0, "Monthly");
	private static final Subscription SUBSCRIPTION_FIXTURE2 = new Subscription("2", "Test", 1.0, "Weekly");
	 
	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		cliView = new SubscriptionViewCLI(new PrintStream(outContent));
		cliView.setController(controller);
	}

	@Test
	public void testLoadAllSubscriptions() {
		cliView.loadAllSubscriptions(asList(SUBSCRIPTION_FIXTURE,SUBSCRIPTION_FIXTURE2));
		String input = "1"+EOL+"5";
		System.setIn(new ByteArrayInputStream(input.getBytes()));
		assertThat(cliView.getList())
			.containsExactly(SUBSCRIPTION_FIXTURE,SUBSCRIPTION_FIXTURE2);
	}
	
	@Test
	public void testShowSubscriptions() {
		cliView.getList().add(SUBSCRIPTION_FIXTURE);
		cliView.getList().add(SUBSCRIPTION_FIXTURE2);
		String input = "1"+EOL+"5";
		System.setIn(new ByteArrayInputStream(input.getBytes()));
		cliView.runView();
		assertThat(outContent.toString())
			.contains("All subscriptions:"+EOL+SUBSCRIPTION_FIXTURE.toString()+EOL+SUBSCRIPTION_FIXTURE2.toString());
	}
	
	@Test
	public void testShowSubscriptionWhenThereAreNoSubsAdded() {
		String input = "1"+EOL+"5";
		System.setIn(new ByteArrayInputStream(input.getBytes()));
		cliView.runView();
		assertThat(outContent.toString())
			.contains("No subscriptions added");
	}
	
	@Test
	public void testShowSpendingWhenThereAreNoSubAdded() {
		String input = "2"+EOL+"5";
		System.setIn(new ByteArrayInputStream(input.getBytes()));
		cliView.runView();
		assertThat(outContent.toString())
			.contains("Total monthly spending: 0.0");
	}
	
	@Test
	public void testForceDigitChoiceShouldPrintErrorWhenInvalidDigitIsTyped() {
		String input = "eee"+EOL+"5";
		System.setIn(new ByteArrayInputStream(input.getBytes()));
		cliView.runView();
		System.out.println(outContent.toString());
		assertThat(outContent.toString())
			.contains("Invalid digit");
	}
	
	@Test
	public void testForceDigitChoiceShouldPrintErrorWhenInvalidIntegerIsTyped() {
		String input = "8"+EOL+"5";
		System.setIn(new ByteArrayInputStream(input.getBytes()));
		cliView.runView();
		assertThat(outContent.toString())
			.contains("Input should be between 1 and 5");
	}
	
	@Test
	public void testForceDoubleChoiceShouldPrintErrorWhenInputIsANegativeDouble() {
		String input = "3"+EOL+"1"+EOL+"test"+EOL+"-7"+EOL+"1"+EOL+"1"+EOL+"5";
		System.setIn(new ByteArrayInputStream(input.getBytes()));
		cliView.runView();
		assertThat(outContent.toString())
			.contains("Input should be a positive double");		
	}
	
	@Test
	public void testForceDoubleChoiceShouldPrintErrorWhenInputCanNotBeParsedAsDouble() {
		String input = "3"+EOL+"1"+EOL+"test"+EOL+"NotADouble"+EOL+"1"+EOL+"1"+EOL+"5";
		System.setIn(new ByteArrayInputStream(input.getBytes()));
		cliView.runView();
		assertThat(outContent.toString())
			.contains("Input should be a positive double");	
	}
	
	@Test public void testChooseRepetitionMethodAssignsCorrectValue() {
		String input = "3"+EOL+"1"+EOL+"test"+EOL+"1"+EOL+"3"+EOL+"5"+EOL;
		System.setIn(new ByteArrayInputStream(input.getBytes()));
		cliView.runView();
		verify(controller).addSubscription(new Subscription("1", "test", 1.0, "Annual"));
	}
	
	@Test
	public void testSpendindWhenThereAreSubsAdded() {
		cliView.getList().add(SUBSCRIPTION_FIXTURE);
		cliView.getList().add(SUBSCRIPTION_FIXTURE2);
		String input = "2"+EOL+"5";
		System.setIn(new ByteArrayInputStream(input.getBytes()));
		cliView.runView();
		assertThat(outContent.toString())
			.contains("Total monthly spending: 5.0");
	}
	
	@Test
	public void testExit() {
		String input = "5";
		System.setIn(new ByteArrayInputStream(input.getBytes()));
		cliView.runView();
		assertThat(outContent.toString()).contains("Goodbye!");
	}
	
	@Test
	public void testAddSubscriptionShouldDelegateToController() {
		String input = "3"+EOL+"1"+EOL+"Netflix"+EOL+"1.0"+EOL+"2"+EOL+"5";
		System.setIn(new ByteArrayInputStream(input.getBytes()));
		cliView.runView();
		verify(controller).addSubscription(SUBSCRIPTION_FIXTURE);
	}
	
	@Test
	public void testDeleteSubscriptionShouldDelegateToController() {
		cliView.getList().add(SUBSCRIPTION_FIXTURE);
		String input = "4"+EOL+"1"+EOL+"5";
		System.setIn(new ByteArrayInputStream(input.getBytes()));
		cliView.runView();
		verify(controller).deleteSubscription(SUBSCRIPTION_FIXTURE.getId());
	}
	
	@Test
	public void testshowSubscriptionAlreadyExistsError() {
		cliView.showSubscriptionAlreadyExistsError(SUBSCRIPTION_FIXTURE);
		assertThat(outContent.toString())
			.hasToString("Error: Already existing subscription with id 1"+EOL);
	}
	
	@Test
	public void testshowNonExistingSubscritptionError() {
		cliView.showNonExistingSubscritptionError("1");
		assertThat(outContent.toString())
			.hasToString("Error: No existing subscription with id 1"+EOL);	
	}
	
	@Test
	public void testMonthlySubscriptionAdded() {
		cliView.subscriptionAdded(SUBSCRIPTION_FIXTURE);
		assertThat(outContent.toString())
			.hasToString(SUBSCRIPTION_FIXTURE.toString()+" added"+EOL);	
	}
	
	@Test
	public void testSubscriptionRemoved() {
		cliView.subscriptionRemoved(SUBSCRIPTION_FIXTURE);
		assertThat(outContent.toString())
			.hasToString(SUBSCRIPTION_FIXTURE.toString()+" removed"+EOL);		
	}
}
