package it.pietro.subscriptionsmanager.repository.mongo;

import java.util.List;

import com.mongodb.MongoClient;
import org.bson.Document;
import com.mongodb.client.MongoCollection;

import it.pietro.subscriptionsmanager.model.Subscription;
import it.pietro.subscriptionsmanager.repository.SubscriptionRepository;

public class SubscriptionMongoRepository implements SubscriptionRepository {
	
	protected final MongoCollection<Document> collection;
	
	public SubscriptionMongoRepository(MongoClient client, String dbName, String collectionName) {
		collection = client
				.getDatabase(dbName)
				.getCollection(collectionName);
	}
	

	@Override
	public List<Subscription> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Subscription findById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(Subscription sub) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub

	}

}
