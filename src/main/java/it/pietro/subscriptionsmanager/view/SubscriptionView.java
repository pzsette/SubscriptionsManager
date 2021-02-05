package it.pietro.subscriptionsmanager.view;

import java.util.List;
import it.pietro.subscriptionsmanager.model.Subscription;

public interface SubscriptionView {
	
	void loadAllSubscriptions(List<Subscription> subs);
	
	void showSubscriptionAlreadyExistsError(Subscription sub);
	
	void showNonExistingSubscritptionError(String id);
	
	void subscriptionAdded(Subscription sub);
	
	void subscriptionRemoved(Subscription sub);
}
