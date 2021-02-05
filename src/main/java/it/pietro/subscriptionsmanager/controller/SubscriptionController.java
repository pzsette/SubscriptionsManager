package it.pietro.subscriptionsmanager.controller;

import it.pietro.subscriptionsmanager.model.Subscription;
import it.pietro.subscriptionsmanager.repository.SubscriptionRepository;
import it.pietro.subscriptionsmanager.view.SubscriptionView;

public class SubscriptionController {
	
	private final SubscriptionRepository repository;
	private final SubscriptionView view;
	
	public SubscriptionController(SubscriptionRepository repository, SubscriptionView view) {
		this.repository = repository;
		this.view = view;
	}
	
	public void allSubscriptions() {
		view.loadAllSubscriptions(repository.findAll());
	}
	
	public void addSubscription(Subscription sub) {
		Subscription subInDB = repository.findById(sub.getId());
		if (subInDB != null)  {
			view.showSubscriptionAlreadyExistsError(subInDB);
			return;
		}
		repository.save(sub);
		view.subscriptionAdded(sub);
	}
	
	public void deleteSubscription(String id) {
		Subscription subInDB = repository.findById(id);
		if (subInDB == null) {
			view.showNonExistingSubscritptionError(id);
			return;
		}
		repository.delete(id);
		view.subscriptionRemoved(subInDB);
	}

}
