package it.pietro.subscrptionsmanager.view.swing;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

import it.pietro.subscriptionsmanager.view.swing.SubscriptionViewSwing;
import it.pietro.subscriptionsmanager.model.Subscription;

@RunWith(GUITestRunner.class)
public class SubscriptionViewSwingTest extends AssertJSwingJUnitTestCase {
	
	private FrameFixture window;
	
	private SubscriptionViewSwing swingView;
	
	private static final Subscription SUBSCRIPTION_FIXTURE = new Subscription("1", "Netflix", 1.0, "Monthly");
	private static final Subscription SUBSCRIPTION_FIXTURE2 = new Subscription("2", "Test", 1.0, "Weekly");

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
			swingView.getListSubscriptionModel().addElement(SUBSCRIPTION_FIXTURE));
		window.list("subscriptionList").selectItem(0);
		window.button(JButtonMatcher.withName("deleteBtn")).requireEnabled();
		window.list("subscriptionList").clearSelection();
		window.button(JButtonMatcher.withName("deleteBtn")).requireDisabled();
	}
	
	@Test
	public void testShowAllSubscriptionsShouldAddSubsDescriptionToTheList() {
		GuiActionRunner.execute(() -> 
			swingView.showAllSubscriptions(asList(SUBSCRIPTION_FIXTURE,SUBSCRIPTION_FIXTURE2)));
		String[] listContents = window.list().contents();
		assertThat(listContents)
			.containsExactly(SUBSCRIPTION_FIXTURE.toString(),SUBSCRIPTION_FIXTURE2.toString());
	}
	
	@Test
	public void testShowErrorShouldShowTheMessageInTheErrorLabel() {
		GuiActionRunner.execute(
				() -> swingView.showError("error message", SUBSCRIPTION_FIXTURE)
		);
		window.label("errorLbl")
			.requireText("error message: "+SUBSCRIPTION_FIXTURE);
	}
	
	@Test
	public void testSubscriptionAddedShouldAddTheSubToTheListAndResetErrorLabel() {
		GuiActionRunner.execute(
				() -> swingView.subscriptionAdded(SUBSCRIPTION_FIXTURE));
		String[] listContents = window.list().contents();
		assertThat(listContents).containsExactly(SUBSCRIPTION_FIXTURE.toString());
		window.label("errorLbl").requireText(" ");
	}
	
	@Test
	public void testSubscriptionRemovedShouldAddTheSubToTheListAndResetErrorLabel() {
		GuiActionRunner.execute(
				() -> {
					swingView.getListSubscriptionModel().addElement(SUBSCRIPTION_FIXTURE);
					swingView.getListSubscriptionModel().addElement(SUBSCRIPTION_FIXTURE2);
					swingView.subscriptionRemoved(SUBSCRIPTION_FIXTURE);
				});
		String[] listContents = window.list().contents();
		assertThat(listContents).containsExactly(SUBSCRIPTION_FIXTURE2.toString());
		window.label("errorLbl").requireText(" ");
	}
	
	@Test
	public void testAddSubscriptionShouldUpdateAmountTextLabel() {
		GuiActionRunner.execute(
				() -> swingView.subscriptionAdded(SUBSCRIPTION_FIXTURE));
		window.label(JLabelMatcher.withName("amountTextLabel")).requireText("1.0");
	}
}
