package it.pietro.subscriptionsmanager.controller;

import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.junit.Test;
import org.junit.Before;

import it.pietro.subscriptionsmanager.model.Subscription;
import it.pietro.subscriptionsmanager.repository.SubscriptionRepository;
import it.pietro.subscriptionsmanager.view.SubscriptionView;

public class SubscriptionControllerTest {
	
	@Mock
	private SubscriptionRepository repository;
	
	@Mock
	private SubscriptionView view;
	
	@InjectMocks
	private SubscriptionController controller;
	
	private static final Subscription SUBSCRIPTION_FIXTURE = new Subscription("1", "Netflix", 1.0, "Month");
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
	}
 
	@Test
	public void testAllSubscriptionsRetrieval() {
		List<Subscription> subscriptions = Collections.singletonList(SUBSCRIPTION_FIXTURE);
		when(repository.findAll()).thenReturn(subscriptions);
		controller.allSubscriptions();
		verify(view).showAllSubscriptions(subscriptions);	
	}
	
	@Test
	public void testAddSubscriptionWhenSubscriptionDoesNotAlreadyExist() {
		controller.addSubscription(SUBSCRIPTION_FIXTURE);
		InOrder inorder = inOrder(repository, view);
		inorder.verify(repository).save(SUBSCRIPTION_FIXTURE);
		inorder.verify(view).subscriptionAdded(SUBSCRIPTION_FIXTURE);
	}
	
	@Test
	public void testAddSubscriptionWhenSubscriptionDoesAlreayExist() {
		Subscription newSub = new Subscription("1", "Netflix", 1.0, "Month");
		when(repository.findById("1"))
			.thenReturn(SUBSCRIPTION_FIXTURE);
		controller.addSubscription(newSub);
		verify(view)
			.showSubscriptionAlreadyExistsError(SUBSCRIPTION_FIXTURE);
		verifyNoMoreInteractions(ignoreStubs(repository));
	}
	
	@Test
	public void testDeleteSubscriptionWhenSubscritpionExists() {
		when(repository.findById(SUBSCRIPTION_FIXTURE.getId()))
			.thenReturn(SUBSCRIPTION_FIXTURE);
		controller.deleteSubscription(SUBSCRIPTION_FIXTURE);
		InOrder inorder = inOrder(repository, view);
		inorder.verify(repository).delete(SUBSCRIPTION_FIXTURE.getId());
		inorder.verify(view).subscriptionRemoved(SUBSCRIPTION_FIXTURE);
	}
	
	@Test
	public void testDeleteSubscriptionWhenSubscriptionDoesNotExist() {
		when(repository.findById(SUBSCRIPTION_FIXTURE.getId()))
			.thenReturn(null);
		controller.deleteSubscription(SUBSCRIPTION_FIXTURE);
		verify(view)
			.showNonExistingSubscritptionError(SUBSCRIPTION_FIXTURE);
		verifyNoMoreInteractions(ignoreStubs(repository));
	}
}
