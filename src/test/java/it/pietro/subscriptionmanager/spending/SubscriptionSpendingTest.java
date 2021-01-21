package it.pietro.subscriptionmanager.spending;
import static org.assertj.core.api.Assertions.assertThat;
import static java.util.Arrays.asList;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import it.pietro.subscriptionsmanager.model.Subscription;

public class SubscriptionSpendingTest {
	
	Subscription MONTHLY_SUB_FIXTURE = new Subscription("0", "Netflix", 1.0, "Monthly");
	Subscription WEEKLY_SUB_FIXTURE = new Subscription("1", "Magazine", 2.0, "Weekly");
	Subscription ANNUAL_SUB_FIXTURE = new Subscription("2", "Gym", 120.0, "Annual");
	
	private SubscriptionSpending spending = new SubscriptionSpending();

	@Test
	public void testComputeSpendindWhenThereAreNoSubscrptions() {
		List<Subscription> subs = Collections.emptyList();
		assertThat(spending.computeSpending(subs)).isEqualTo(0);
	}
	
	@Test
	public void testComputeSpendingWhenThereIsOneMonthlySubscription() { 
		assertThat(spending.computeSpending(asList(MONTHLY_SUB_FIXTURE)))
			.isEqualTo(MONTHLY_SUB_FIXTURE.getPrice());
	}
	
	@Test
	public void testComputeSpndingWhenThereIsOneWeeklySubscription() {
		assertThat(spending.computeSpending(asList(WEEKLY_SUB_FIXTURE)))
			.isEqualTo(WEEKLY_SUB_FIXTURE.getPrice()*4);
	}
	
	@Test
	public void testComputeSpndingWhenThereIsOneAnnualSubscription() {
		assertThat(spending.computeSpending(asList(ANNUAL_SUB_FIXTURE)))
			.isEqualTo(ANNUAL_SUB_FIXTURE.getPrice()/12);
	}
	
	@Test
	public void testComputeSpendingWhenThereAreDifferentaSubscripitons() {
		assertThat(spending.computeSpending(asList(WEEKLY_SUB_FIXTURE, MONTHLY_SUB_FIXTURE, ANNUAL_SUB_FIXTURE)))
			.isEqualTo(19.0);
	}

}
