package it.pietro.subscrptionsmanager.view.swing;

import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

import it.pietro.subscriptionsmanager.model.Subscription;
import it.pietro.subscriptionsmanager.view.swing.SubscriptionViewSwing;

@RunWith(GUITestRunner.class)
public class SubscriptionViewSwingTest extends AssertJSwingJUnitTestCase {
	
	private FrameFixture window;
	
	private SubscriptionViewSwing swingView;

	@Override
	protected void onSetUp() throws Exception {
		GuiActionRunner.execute(() -> {
			swingView = new SubscriptionViewSwing();
			return swingView;
		});
		window = new FrameFixture(robot(), swingView);
		window.show();
	}
	
	@Test
	public void testControlsInitialState() {
		window.label(JLabelMatcher.withName("spendingTextLabel"));
		window.label(JLabelMatcher.withName("amountTextLabel"));
		window.list("subscriptionList");
		window.button(JButtonMatcher.withName("deleteBtn")).requireDisabled();
		window.label(JLabelMatcher.withName("idTextLabel"));
		window.textBox("idTextField");
		window.label(JLabelMatcher.withName("nameTextLabel"));
		window.textBox("nameTextField");
		window.label(JLabelMatcher.withName("priceTextLabel"));
		window.textBox("priceTextField");
		window.label(JLabelMatcher.withName("repetitionTextLabel"));
		window.comboBox("repetitionDropDown");
		window.button(JButtonMatcher.withName("addBtn")).requireDisabled();
		window.label(JLabelMatcher.withName("errorLbl"));
	}
	
	@Test
	public void testWhenhenSubFieldsAreNotEmptyTheAddButtonShouldBeEnabled() {
		window.textBox("idTextField").enterText("1");
		window.textBox("nameTextField").enterText("Netflix");
		window.textBox("priceTextField").enterText("1");
		window.button(JButtonMatcher.withName("addBtn")).requireEnabled();
	}
	
	@Test
	public void testWhenThereAreBlankFieldsAddButtonShouldBeDisabled() {
		window.textBox("idTextField").enterText("1");
		window.textBox("nameTextField").enterText(" ");
		window.textBox("priceTextField").enterText("1");
		window.button(JButtonMatcher.withName("addBtn")).requireDisabled();
	}
	
	@Test
	public void testDeleteButtonShouldBeEnabledOnlyWhenASubscriptionIsSelected() {
		GuiActionRunner.execute(() ->
			swingView.getListSubscriptionModel().addElement(new Subscription("1", "Netflix", 1.0, "Monthly")));
		window.list("subscriptionList").selectItem(0);
		window.button(JButtonMatcher.withName("deleteBtn")).requireEnabled();
		window.list("subscriptionList").clearSelection();
		window.button(JButtonMatcher.withName("deleteBtn")).requireDisabled();
		
		
	}
}
