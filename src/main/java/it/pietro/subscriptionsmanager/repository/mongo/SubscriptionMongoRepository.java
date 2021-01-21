package it.pietro.subscriptionsmanager.repository.mongo;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.mongodb.MongoClient;
import org.bson.Document;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import it.pietro.subscriptionsmanager.model.Subscription;
import it.pietro.subscriptionsmanager.repository.SubscriptionRepository;

public class SubscriptionMongoRepository implements SubscriptionRepository {
	
	private final MongoCollection<Document> collection;
	
	public SubscriptionMongoRepository(MongoClient client, String dbName, String collectionName) {
		collection = client
				.getDatabase(dbName)
				.getCollection(collectionName);
	}

	@Override
	public List<Subscription> findAll() {
		return StreamSupport
			.stream(collection.find().spliterator(), false)
			.map(this::fromDocumentToStudent)
			.collect(Collectors.toList());		
	}

	@Override
	public Subscription findById(String id) {
		Document d = collection.find(Filters.eq("id", id)).first();
		if (d!= null) {
			return fromDocumentToStudent(d);
		}
		return null;
	}

	@Override
	public void save(Subscription sub) {
		collection.insertOne(
			new Document()
				.append("id", sub.getId())
				.append("name", sub.getName())
				.append("price", sub.getPrice())
				.append("repetition", sub.getRepetition()));
	}
	
	private Subscription fromDocumentToStudent(Document d) {
		return new Subscription(""+d.get("id"), ""+d.get("name"), (Double) d.get("price"), ""+d.get("repetition"));
	}

	@Override
	public void delete(String id) {
		collection.deleteOne(Filters.eq("id", id));

	}

}
