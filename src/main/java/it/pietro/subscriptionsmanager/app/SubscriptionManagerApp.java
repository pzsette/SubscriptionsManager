package it.pietro.subscriptionsmanager.app;

import java.awt.EventQueue;
import java.util.concurrent.Callable;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

import org.slf4j.LoggerFactory;

import com.mongodb.ServerAddress;
import com.mongodb.MongoClient;

import it.pietro.subscriptionsmanager.controller.SubscriptionController;
import it.pietro.subscriptionsmanager.repository.mongo.SubscriptionMongoRepository;
import it.pietro.subscriptionsmanager.view.cli.SubscriptionViewCLI;
import it.pietro.subscriptionsmanager.view.swing.SubscriptionViewSwing;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(mixinStandardHelpOptions = true)
public class SubscriptionManagerApp implements Callable<Void>  {
	
	private static LoggerContext loggerCtx = (LoggerContext) LoggerFactory.getILoggerFactory();
	private static final Logger logger = loggerCtx.getLogger(SubscriptionManagerApp.class);
	
	private enum uiOptions {
		gui,
		cli
	}
	
	@Option(names = { "--mongo-host" }, description = "MongoDB host address")
	private String mongoHost = "localhost";

	@Option(names = { "--mongo-port" }, description = "MongoDB host port")
	private int mongoPort = 27017;

	@Option(names = { "--db-name" }, description = "Database name")
	private String databaseName = "subscriptionsmanager";

	@Option(names = { "--db-collection" }, description = "Collection name")
	private String collectionName = "subscriptions";
	
	@Option(names = { "--ui" }, description = "User interfaces options: ${COMPLETION-CANDIDATES}")
	uiOptions ui = uiOptions.gui;	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		new CommandLine(new SubscriptionManagerApp()).execute(args);
	}

	@Override
	public Void call() throws Exception {
		EventQueue.invokeLater(() -> {
			try {
				MongoClient client = new MongoClient(new ServerAddress(mongoHost, mongoPort));
				SubscriptionMongoRepository repository = new SubscriptionMongoRepository(client, databaseName, collectionName);
				
				if (ui.equals(uiOptions.gui)) {
					SubscriptionViewSwing swingView = new SubscriptionViewSwing();
					SubscriptionController controller = new SubscriptionController(repository, swingView);
					swingView.setController(controller);
					swingView.setVisible(true);
					controller.allSubscriptions();
				} else if (ui.equals(uiOptions.cli)) {
					disableMongoJavaLogs();
					SubscriptionViewCLI cliView = new SubscriptionViewCLI(System.out);
					SubscriptionController controller = new SubscriptionController(repository, cliView);
					cliView.setController(controller);
					controller.allSubscriptions();
					cliView.runView();
				}
			} catch (Exception e) {
				logger.debug("Caught Exception: %s", e.getMessage());
			}
		});
		return null;
	}
	
	private static void disableMongoJavaLogs() {
		Logger loggerMongoJava = loggerCtx.getLogger("org.mongodb.driver");
		loggerMongoJava.setLevel(Level.ERROR);
	}

}
