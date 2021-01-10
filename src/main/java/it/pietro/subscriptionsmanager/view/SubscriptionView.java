package it.pietro.subscriptionsmanager.view;

import java.util.List;
import it.pietro.subscriptionsmanager.model.Subscription;

public interface SubscriptionView {
	
	void showAllSubscriptions(List<Subscription> subs);
	
	void showError(String Message, Subscription sub);
	
	void subscriptionAdded(Subscription sub);
	
	void subscriptionRemoved(Subscription sub);
	
}
