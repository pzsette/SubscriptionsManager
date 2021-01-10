package it.pietro.subscriptionsmanager.repository;

import it.pietro.subscriptionsmanager.model.Subscription;
import java.util.List;

public interface SubscriptionRepository {
	
	public List<Subscription> findAll();

	public Subscription findById(String id);

	public void save(Subscription sub);
	
	public void delete(String id);

}
