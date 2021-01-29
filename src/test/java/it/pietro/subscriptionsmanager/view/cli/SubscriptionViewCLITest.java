package it.pietro.subscriptionsmanager.view.cli;

import static org.assertj.core.api.Assertions.assertThat;
import static java.util.Arrays.asList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collections;

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
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;
	private final PrintStream originalErr = System.err;
	
	SubscriptionViewCLI cliView; 

	private static final Subscription SUBSCRIPTION_FIXTURE = new Subscription("1", "Netflix", 1.0, "Monthly");
	private static final Subscription SUBSCRIPTION_FIXTURE2 = new Subscription("2", "Test", 1.0, "Weekly");
	
	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		cliView = new SubscriptionViewCLI();
		cliView.setController(controller);
	    System.setOut(new PrintStream(outContent));
	    System.setErr(new PrintStream(errContent));
	}

	@Test
	public void testShowAllSubscriptions() {
		cliView.showAllSubscriptions(asList(SUBSCRIPTION_FIXTURE,SUBSCRIPTION_FIXTURE2));
		assertThat(outContent.toString())
			.isEqualTo("All subscriptions:\n"+SUBSCRIPTION_FIXTURE.toString()+"\n"+SUBSCRIPTION_FIXTURE2.toString()+"\n");
	}
	
	@Test
	public void testShowAllSubscriptionWhenThereAreNoSubsAdded() {
		cliView.showAllSubscriptions(Collections.emptyList());
		assertThat(outContent.toString())
			.isEqualTo("No subscriptions added\n");
	}
	
	@Test
	public void testSpendingWhenThereAreNoSubAdded() {
		cliView.showSpending();
		assertThat(outContent.toString())
			.isEqualTo("Total monthly spending: 0.0\n");
	}
	
	@Test
	public void testSpendindWhenThereAreSubsAdded() {
		cliView.getList().add(SUBSCRIPTION_FIXTURE);
		cliView.getList().add(SUBSCRIPTION_FIXTURE2);
		cliView.showSpending();
		assertThat(outContent.toString())
			.isEqualTo("Total monthly spending: 5.0\n");
	}
	
	@Test
	public void testAddSubscriptionShouldDelegateToController() {
		String input = "3\n1\nNetflix\n1.0\n2\n";
		System.setIn(new ByteArrayInputStream(input.getBytes()));
		cliView.runView();
		verify(controller).addSubscription(SUBSCRIPTION_FIXTURE);
	}
	
	@Test
	public void testDeleteSubscriptionShouldDelegateToController() {
		cliView.getList().add(SUBSCRIPTION_FIXTURE);
		String input = "4\n1";
		System.setIn(new ByteArrayInputStream(input.getBytes()));
		cliView.runView();
		verify(controller).deleteSubscription(SUBSCRIPTION_FIXTURE);
	}
}
