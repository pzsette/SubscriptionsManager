package it.pietro.subscriptionsmanager.view.cli;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import it.pietro.subscriptionsmanager.controller.SubscriptionController;
import it.pietro.subscriptionsmanager.model.Subscription;
import it.pietro.subscriptionsmanager.spending.SubscriptionSpending;
import it.pietro.subscriptionsmanager.view.SubscriptionView;

public class SubscriptionViewCLI implements SubscriptionView {
	
	private List<Subscription> listSubscriptions; 
	private SubscriptionController controller;
	
	private Scanner scanner;
	private PrintStream output;
	
	public List<Subscription> getList() {
		return listSubscriptions;
	}
	
	public SubscriptionViewCLI(PrintStream output) {
		this.output = output;
		listSubscriptions = new ArrayList<>();
	}

	@Override
	public void loadAllSubscriptions(List<Subscription> subs) {
		listSubscriptions = subs;
	}

	@Override
	public void showSubscriptionAlreadyExistsError(Subscription sub) {
		output.println("Error: Already existing subscription with id "+sub.getId());
	}

	@Override
	public void showNonExistingSubscritptionError(String id) {
		output.println("Error: No existing subscription with id "+id);
	}

	@Override
	public void subscriptionAdded(Subscription sub) {
		listSubscriptions.add(sub);
		output.println(sub.toString()+" added");
	}

	@Override
	public void subscriptionRemoved(Subscription sub) {
		listSubscriptions.remove(sub);
		output.println(sub.toString()+" removed");
	}
	
	private void showSubscriptions() {
		if (listSubscriptions.isEmpty()) {
			output.println("No subscriptions added");
		} else {
			output.println("All subscriptions:");
			for (Subscription sub : listSubscriptions) {
				output.println(sub.toString());
			}
		}
	}
	
	private void showSpending() {
		output.println("Total monthly spending: "+(SubscriptionSpending.computeSpending(listSubscriptions)));
	}
	
	private void showOptions() {
		output.println("1) Show all subscriptions");
		output.println("2) Show total spending");
		output.println("3) Add subscription");
		output.println("4) Delete subscription");
		output.println("5) Exit");
	}
	
	private void addSubscription() {
		output.println("Insert id:");
		String id = scanner.nextLine();
		output.println("Insert name:");
		String name = scanner.nextLine();
		output.println("Insert price:");
		Double price = forceDoubleChoice();
		String repetition = chooseRepetition();
		controller.addSubscription(new Subscription(id, name, price, repetition));
	}
	
	private String chooseRepetition() {
		output.println("Choose repetition:");
		output.println("1) Weekly");
		output.println("2) Monthly");
		output.println("3) Annual");
		int choice = forceDigitChoice(1, 3);
		String repetition = null;
		if (choice == 1) {

			repetition = "Weekly";
		} else if (choice == 2) {
			repetition = "Monthly";
		} else {	
			repetition = "Annual";
		}
		return repetition;
	}
	
	private int forceDigitChoice(int low, int high) {
		int choice = 0;
		boolean validInput = false;
		while(!validInput) {
			try {
				choice = Integer.parseInt(scanner.nextLine());
				if (choice >= low && choice <= high) {
					validInput = true;
				} else {
					output.println("Input should be between "+low+" and "+high);
				}
			} catch (NumberFormatException e) {
				output.println("Invalid digit");
			}
		}
		return choice;
	}
	
	private double forceDoubleChoice() {
		Double choice = 0.0;
		boolean validInput = false;
		while(!validInput) {
			String input = scanner.nextLine();
			if (isPositiveDouble(input)) {
				choice = Double.parseDouble(input);
				validInput = true;
			} else {
				output.println("Input should be a positive double");
			}
		}
		return choice;
	}
	
	private void deleteSubscription() {
		output.println("Insert id of subscription to delete:");
		String id = scanner.nextLine();
		controller.deleteSubscription(id);
	}
	
	public void runView() {
		scanner = new Scanner(System.in);
		output.println("SUBSCRIPTIONS MANAGER");
		boolean exit = false;
		while (!exit) {
			showOptions();
			int choice = forceDigitChoice(1, 5);
			if (choice == 1) {
				showSubscriptions();
			} else if (choice == 2) {
				showSpending();
			} else if (choice == 3) {
				addSubscription();
			} else if (choice == 4) {
				deleteSubscription();
			} else {
				exit = true;
			}
		}
		output.println("Goodbye!");
	}
	
	public void setController(SubscriptionController controller) {
		this.controller = controller;
	}
	
	private boolean isPositiveDouble(String value) {
		try {
			return (Double.parseDouble(value) > 0);
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
}
