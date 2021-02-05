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
	
	SubscriptionViewCLI cliView; 

	private static final Subscription SUBSCRIPTION_FIXTURE = new Subscription("1", "Netflix", 1.0, "Monthly");
	private static final Subscription SUBSCRIPTION_FIXTURE2 = new Subscription("2", "Test", 1.0, "Weekly");
	 
	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		cliView = new SubscriptionViewCLI(new PrintStream(outContent));
		cliView.setController(controller);
	    System.setOut(new PrintStream(outContent));
	}

	@Test
	public void testLoadAllSubscriptions() {
		cliView.loadAllSubscriptions(asList(SUBSCRIPTION_FIXTURE,SUBSCRIPTION_FIXTURE2));
		String input = "1\n5";
		System.setIn(new ByteArrayInputStream(input.getBytes()));
		assertThat(cliView.getList())
			.containsExactly(SUBSCRIPTION_FIXTURE,SUBSCRIPTION_FIXTURE2);
	}
	
	@Test
	public void testShowSubscriptions() {
		cliView.getList().add(SUBSCRIPTION_FIXTURE);
		cliView.getList().add(SUBSCRIPTION_FIXTURE2);
		String input = "1\n5";
		System.setIn(new ByteArrayInputStream(input.getBytes()));
		cliView.runView();
		assertThat(outContent.toString())
			.contains("All subscriptions:\n"+SUBSCRIPTION_FIXTURE.toString()+"\n"+SUBSCRIPTION_FIXTURE2.toString()+"\n");
	}
	
	@Test
	public void testShowSubscriptionWhenThereAreNoSubsAdded() {
		String input = "1\n5";
		System.setIn(new ByteArrayInputStream(input.getBytes()));
		cliView.runView();
		assertThat(outContent.toString())
			.contains("No subscriptions added\n");
	}
	
	@Test
	public void testShowSpendingWhenThereAreNoSubAdded() {
		String input = "2\n5";
		System.setIn(new ByteArrayInputStream(input.getBytes()));
		cliView.runView();
		assertThat(outContent.toString())
			.contains("Total monthly spending: 0.0\n");
	}
	
	@Test
	public void testForceDigitChoiceShouldPrintErrorWhenInvalidDigitIsTyped() {
		String input = "eee\n5\n";
		System.setIn(new ByteArrayInputStream(input.getBytes()));
		cliView.runView();
		assertThat(outContent.toString())
			.contains("Invalid digit");
	}
	
	@Test
	public void testForceDigitChoiceShouldPrintErrorWhenInvalidIntegerIsTyped() {
		String input = "8\n5\n";
		System.setIn(new ByteArrayInputStream(input.getBytes()));
		cliView.runView();
		assertThat(outContent.toString())
			.contains("Input should be between 1 and 5");
	}
	
	@Test
	public void testForceDoubleChoiceShouldPrintErrorWhenInputIsANegativeDouble() {
		String input = "3\n1\ntest\n-7\n1\n1\n5";
		System.setIn(new ByteArrayInputStream(input.getBytes()));
		cliView.runView();
		assertThat(outContent.toString())
			.contains("Input should be a positive double");		
	}
	
	@Test
	public void testForceDoubleChoiceShouldPrintErrorWhenInputCanNotBeParsedAsDouble() {
		String input = "3\n1\ntest\nNotADouble\n1\n1\n5";
		System.setIn(new ByteArrayInputStream(input.getBytes()));
		cliView.runView();
		assertThat(outContent.toString())
			.contains("Input should be a positive double");	
	}
	
	@Test public void testChooseRepetitionMethodAssignsCorrectValue() {
		String input = "3\n1\ntest\n1\n3\n5\n";
		System.setIn(new ByteArrayInputStream(input.getBytes()));
		cliView.runView();
		verify(controller).addSubscription(new Subscription("1", "test", 1.0, "Annual"));
	}
	
	@Test
	public void testSpendindWhenThereAreSubsAdded() {
		cliView.getList().add(SUBSCRIPTION_FIXTURE);
		cliView.getList().add(SUBSCRIPTION_FIXTURE2);
		String input = "2\n5";
		System.setIn(new ByteArrayInputStream(input.getBytes()));
		cliView.runView();
		assertThat(outContent.toString())
			.contains("Total monthly spending: 5.0\n");
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
		String input = "3\n1\nNetflix\n1.0\n2\n5";
		System.setIn(new ByteArrayInputStream(input.getBytes()));
		cliView.runView();
		verify(controller).addSubscription(SUBSCRIPTION_FIXTURE);
	}
	
	@Test
	public void testDeleteSubscriptionShouldDelegateToController() {
		cliView.getList().add(SUBSCRIPTION_FIXTURE);
		String input = "4\n1\n5";
		System.setIn(new ByteArrayInputStream(input.getBytes()));
		cliView.runView();
		verify(controller).deleteSubscription(SUBSCRIPTION_FIXTURE.getId());
	}
	
	@Test
	public void testshowSubscriptionAlreadyExistsError() {
		cliView.showSubscriptionAlreadyExistsError(SUBSCRIPTION_FIXTURE);
		assertThat(outContent.toString())
			.isEqualTo("Error: Already existing subscription with id 1\n");
	}
	
	@Test
	public void testshowNonExistingSubscritptionError() {
		cliView.showNonExistingSubscritptionError("1");
		assertThat(outContent.toString())
			.isEqualTo("Error: No existing subscription with id 1\n");	
	}
	
	@Test
	public void testMonthlySubscriptionAdded() {
		cliView.subscriptionAdded(SUBSCRIPTION_FIXTURE);
		assertThat(outContent.toString())
			.isEqualTo("Subscription [id= 1, name= Netflix, price= 1.0, repetition= Monthly] added\n");	
	}
	
	@Test
	public void testSubscriptionRemoved() {
		cliView.subscriptionRemoved(SUBSCRIPTION_FIXTURE);
		assertThat(outContent.toString())
			.isEqualTo("Subscription [id= 1, name= Netflix, price= 1.0, repetition= Monthly] removed\n");		
	}
}
