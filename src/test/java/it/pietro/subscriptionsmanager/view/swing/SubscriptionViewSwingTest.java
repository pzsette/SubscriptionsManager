package it.pietro.subscriptionsmanager.view.swing;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import it.pietro.subscriptionsmanager.controller.SubscriptionController;
import it.pietro.subscriptionsmanager.model.Subscription;

@RunWith(GUITestRunner.class)
public class SubscriptionViewSwingTest extends AssertJSwingJUnitTestCase {
	
	@Mock
	private SubscriptionController controller;
	
	private FrameFixture window;
	
	private SubscriptionViewSwing swingView;
	
	private static final Subscription SUBSCRIPTION_FIXTURE = new Subscription("1", "Netflix", 1.0, "Monthly");
	private static final Subscription SUBSCRIPTION_FIXTURE2 = new Subscription("2", "Test", 1.0, "Weekly");

	@Override
	protected void onSetUp() throws Exception {
		GuiActionRunner.execute(() -> {
			MockitoAnnotations.openMocks(this);
			swingView = new SubscriptionViewSwing();
			swingView.setController(controller);
			return swingView;
		});
		window = new FrameFixture(robot(), swingView);
		window.show();
	}
	
	@Test
	@GUITest
	public void testControlsInitialState() {
		window.label(JLabelMatcher.withName("spendingTextLabel"));
		window.label(JLabelMatcher.withName("amountTextLabel")).requireText("0");
		window.list("subscriptionList").requireItemCount(0).requireNoSelection();
		window.button(JButtonMatcher.withName("deleteBtn")).requireDisabled();
		window.label(JLabelMatcher.withName("idTextLabel"));
		window.textBox("idTextField").requireEmpty();
		window.label(JLabelMatcher.withName("nameTextLabel"));
		window.textBox("nameTextField").requireEmpty();
		window.label(JLabelMatcher.withName("priceTextLabel"));
		window.textBox("priceTextField");
		window.label(JLabelMatcher.withName("repetitionTextLabel"));
		window.comboBox("repetitionDropDown").requireSelection("Weekly");
		window.button(JButtonMatcher.withName("addBtn")).requireDisabled();
		window.label(JLabelMatcher.withName("errorLbl")).requireText(" ");
	}
	
	@Test
	@GUITest
	public void testWhenSubFieldsAreNotEmptyTheAddButtonShouldBeEnabled() {
		window.textBox("idTextField").enterText("1");
		window.textBox("nameTextField").enterText("Netflix");
		window.textBox("priceTextField").enterText("1");
		window.button(JButtonMatcher.withName("addBtn")).requireEnabled();
	}
	
	@Test
	@GUITest
	public void testWhenPriceFieldIsNotFilledWithDoubleTheAddButtonShouldBeDisabled() {
		window.textBox("idTextField").enterText("1");
		window.textBox("nameTextField").enterText("Netflix");
		window.textBox("priceTextField").enterText("ppp");
		window.button(JButtonMatcher.withName("addBtn")).requireDisabled();
	}
	
	@Test
	@GUITest
	public void testWhenNameIsNotFilledTheAddButtonShouldBeDisabled() {
	  window.textBox("idTextField").enterText(" ");
	  window.textBox("priceTextField").enterText("7.0");
	  window.textBox("nameTextField").enterText("Test");
	  window.button(JButtonMatcher.withName("addBtn")).requireDisabled();
	}

	@Test
	@GUITest
	public void testWheIdIsNotFilledTheAddButtonShouldBeDisabled() {
	  window.textBox("idTextField").enterText("1");
	  window.textBox("priceTextField").enterText(" ");
	  window.textBox("nameTextField").enterText("Test");
	  window.button(JButtonMatcher.withName("addBtn")).requireDisabled();
	}

	@Test
	@GUITest
	public void testWhenPriceIsNotFilledTheAddButtonShouldBeDisabled() {
	  window.textBox("idTextField").enterText("2");
	  window.textBox("priceTextField").enterText("7.0");
	  window.textBox("nameTextField").enterText(" ");
	  window.button(JButtonMatcher.withName("addBtn")).requireDisabled();
	}
	
	@Test
	@GUITest
	public void testWhenThereAreBlankFieldsAddButtonShouldBeDisabled() {
		window.textBox("idTextField").enterText("1");
		window.textBox("nameTextField").enterText(" ");
		window.textBox("priceTextField").enterText("1");
		window.button(JButtonMatcher.withName("addBtn")).requireDisabled();
	}
	
	@Test
	@GUITest
	public void testDeleteButtonShouldBeEnabledOnlyWhenASubscriptionIsSelected() {
		GuiActionRunner.execute(() ->
			swingView.getListSubscriptionModel().addElement(SUBSCRIPTION_FIXTURE));
		window.list("subscriptionList").selectItem(0);
		window.button(JButtonMatcher.withName("deleteBtn")).requireEnabled();
		window.list("subscriptionList").clearSelection();
		window.button(JButtonMatcher.withName("deleteBtn")).requireDisabled();
	}
	
	@Test
	@GUITest
	public void testShowAllSubscriptionsShouldAddSubsDescriptionToTheList() {
		GuiActionRunner.execute(() -> 
			swingView.showAllSubscriptions(asList(SUBSCRIPTION_FIXTURE,SUBSCRIPTION_FIXTURE2)));
		String[] listContents = window.list().contents();
		assertThat(listContents)
			.containsExactly(SUBSCRIPTION_FIXTURE.toString(),SUBSCRIPTION_FIXTURE2.toString());
	}
	
	@Test
	@GUITest
	public void testErrorForNoExistingSubscription() {
		GuiActionRunner.execute(
				() -> swingView.showNonExistingSubscritptionError("1")
		);
		window.label("errorLbl")
			.requireText("Error: No existing subscription with id 1");
	}
	
	@Test
	@GUITest
	public void testErrorForAlreadyExistingSubscription() {
		GuiActionRunner.execute(
				() -> swingView.showSubscriptionAlreadyExistsError(SUBSCRIPTION_FIXTURE)
		);
		window.label("errorLbl")
			.requireText("Error: Already existing subscription with id "+SUBSCRIPTION_FIXTURE.getId());
	}
	
	@Test
	@GUITest
	public void testSubscriptionAddedShouldAddTheSubToTheListAndResetErrorLabel() {
		GuiActionRunner.execute(
				() -> swingView.subscriptionAdded(SUBSCRIPTION_FIXTURE));
		String[] listContents = window.list().contents();
		assertThat(listContents).containsExactly(SUBSCRIPTION_FIXTURE.toString());
		window.label("errorLbl").requireText(" ");
	}
	
	@Test
	@GUITest
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
	@GUITest
	public void testAddSubscriptionShouldUpdateAmountTextLabel() {
		GuiActionRunner.execute(
				() -> swingView.subscriptionAdded(SUBSCRIPTION_FIXTURE));
		window.label(JLabelMatcher.withName("amountTextLabel")).requireText("1.0");
	}
	
	@Test
	@GUITest
	public void testRemoveSubscriptionShouldUpdateAmountTextLabel() {
		GuiActionRunner.execute(
				() -> {
					swingView.getListSubscriptionModel().addElement(SUBSCRIPTION_FIXTURE);
					swingView.getListSubscriptionModel().addElement(SUBSCRIPTION_FIXTURE2);
					swingView.subscriptionRemoved(SUBSCRIPTION_FIXTURE);
				});
		window.label(JLabelMatcher.withName("amountTextLabel")).requireText("4.0");
	}
	
	@Test
	@GUITest
	public void testAddButtonShouldDelegateToController() {
		window.textBox("idTextField").enterText("1");
		window.textBox("nameTextField").enterText("Netflix");
		window.textBox("priceTextField").enterText("1");
		window.comboBox("repetitionDropDown").selectItem("Monthly");
		window.button(JButtonMatcher.withName("addBtn")).click();
		verify(controller).addSubscription(SUBSCRIPTION_FIXTURE);
	}
	
	@Test
	@GUITest
	public void testDeleteButtonShouldDelegateController() {
		GuiActionRunner.execute(
				() -> swingView.getListSubscriptionModel().addElement(SUBSCRIPTION_FIXTURE));
		window.list("subscriptionList").selectItem(0);
		window.button(JButtonMatcher.withName("deleteBtn")).click();
		verify(controller).deleteSubscription(SUBSCRIPTION_FIXTURE.getId()); 
	}
}
