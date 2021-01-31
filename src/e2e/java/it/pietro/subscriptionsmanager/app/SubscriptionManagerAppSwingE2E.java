package it.pietro.subscriptionsmanager.app;

import static org.junit.Assert.*;

import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import static org.assertj.swing.launcher.ApplicationLauncher.*;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

@RunWith(GUITestRunner.class) 
public class SubscriptionManagerAppSwingE2E extends AssertJSwingJUnitTestCase {
	
	@ClassRule
	public static final MongoDBContainer mongo =new MongoDBContainer("mongo:4.2.3").withExposedPorts(27017);
	
	private static final String DB_NAME = "test-db";
	private static final String DB_COLLECTION = "test-collection";
	
	private FrameFixture window;
	private MongoClient client;

	@Override
	protected void onSetUp() throws Exception {
		client = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(), mongo.getMappedPort(27017)));	
		client.getDatabase(DB_NAME).drop();
		
	}

}
