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
		view.showAllSubscriptions(repository.findAll());
	}
	
	public void addSubscription(Subscription sub) {
		repository.save(sub);
	}

}
