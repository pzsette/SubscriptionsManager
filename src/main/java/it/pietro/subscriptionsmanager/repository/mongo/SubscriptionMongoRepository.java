package it.pietro.subscriptionsmanager.repository.mongo;

import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientSettings;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import it.pietro.subscriptionsmanager.model.Subscription;
import it.pietro.subscriptionsmanager.repository.SubscriptionRepository;

public class SubscriptionMongoRepository implements SubscriptionRepository {
	
	private final MongoCollection<Subscription> collection;
	
	
	public SubscriptionMongoRepository(MongoClient client, String dbName, String collectionName) {
		
		CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().automatic(true).build()));
		
		collection = client
				.getDatabase(dbName)
				.getCollection(collectionName, Subscription.class)
				.withCodecRegistry(pojoCodecRegistry);
				
	}

	@Override
	public List<Subscription> findAll() {
		return StreamSupport
			.stream(collection.find().spliterator(), false)
			.collect(Collectors.toList());		
	}

	@Override
	public Subscription findById(String id) {
		return collection.find(eq("_id", id)).first();
	}

	@Override
	public void save(Subscription sub) {
		collection.insertOne(sub);
	}

	@Override
	public void delete(String id) {
		collection.deleteOne(Filters.eq("_id", id));

	}

}
