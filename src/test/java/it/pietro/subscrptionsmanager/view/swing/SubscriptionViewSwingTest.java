package it.pietro.subscrptionsmanager.view.swing;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

import it.pietro.subscriptionsmanager.view.swing.SubscriptionViewSwing;

@RunWith(GUITestRunner.class)
public class SubscriptionViewSwingTest extends AssertJSwingJUnitTestCase {
	
	private FrameFixture windows;
	
	private SubscriptionViewSwing swingView;

	@Override
	protected void onSetUp() throws Exception {
		GuiActionRunner.execute(() -> {
			swingView = new SubscriptionViewSwing();
			return swingView;
		});
		windows = new FrameFixture(robot(), swingView);
		windows.show();
	}
	
	@Test
	public void test() {
		//test
	}

}
