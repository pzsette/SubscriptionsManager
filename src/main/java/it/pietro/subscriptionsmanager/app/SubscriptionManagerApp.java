package it.pietro.subscriptionsmanager.app;

import java.awt.EventQueue;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import it.pietro.subscriptionsmanager.controller.SubscriptionController;
import it.pietro.subscriptionsmanager.repository.mongo.SubscriptionMongoRepository;
import it.pietro.subscriptionsmanager.view.swing.SubscriptionViewSwing;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;


public class SubscriptionManagerApp implements Callable<Void>  {
	
	@Option(names = { "--mongo-host" }, description = "MongoDB host address")
	private String mongoHost = "localhost";

	@Option(names = { "--mongo-port" }, description = "MongoDB host port")
	private int mongoPort = 27017;

	@Option(names = { "--db-name" }, description = "Database name")
	private String databaseName = "subscriptionsmanager";

	@Option(names = { "--db-collection" }, description = "Collection name")
	private String collectionName = "subscriptions";
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		

	}

	@Override
	public Void call() throws Exception {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MongoClient client = MongoClients(new ServerAddress(mongoHost, mongoPort));
					SubscriptionMongoRepository repository = new SubscriptionMongoRepository(client, databaseName, collectionName);
					SubscriptionViewSwing frame = new SubscriptionViewSwing();
					SubscriptionController controller = new SubscriptionController(repository, frame);
					frame.setVisible(true);
				} catch (Exception e) {
					Logger.getLogger(getClass().getName())
						.log(Level.SEVERE, "Exception", e);
				}
			}
		});
		return null;
	}

}
