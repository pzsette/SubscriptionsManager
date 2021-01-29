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
	
	private List<Subscription> list; 
	private SubscriptionController controller;
	private Scanner scanner;
	
	private PrintStream output;
	
	public List<Subscription> getList() {
		return list;
	}
	
	public SubscriptionViewCLI(PrintStream output) {
		this.output = output;
		list = new ArrayList<>();
	}
	
	public static void main(String[] args) {
		SubscriptionViewCLI view = new SubscriptionViewCLI(System.out);
		view.runView();
	};

	@Override
	public void showAllSubscriptions(List<Subscription> subs) {
		if (subs.size() == 0) {
			output.println("No subscriptions added");
		} else {
			output.println("All subscriptions:");
			for (Subscription sub : subs) {
				output.println(sub.toString());
			}
		}
	}

	@Override
	public void showSubscriptionAlreadyExistsError(Subscription sub) {
		output.println("Error: Already existing subscription with id "+sub.getId());
	}

	@Override
	public void showNonExistingSubscritptionError(Subscription sub) {
		output.println("Error: No existing subscription with id "+sub.getId());
	}

	@Override
	public void subscriptionAdded(Subscription sub) {
		list.add(sub);
		output.println(sub.toString()+" added");
	}

	@Override
	public void subscriptionRemoved(Subscription sub) {
		list.remove(sub);
		output.println(sub.toString()+" removed");
	}
	
	public void showSpending() {
		output.println("Total monthly spending: "+(SubscriptionSpending.computeSpending(list)));
	}
	
	public void setController(SubscriptionController controller) {
		this.controller = controller;
	}
	
	public void showOptions() {
		output.println("1) Show all subscriptions");
		output.println("2) Show total spending");
		output.println("3) Add subscription");
		output.println("4) Delete subscription");
		output.println("5) Exit");
	}
	
	public void addSubscription() {
		output.println("Insert id:");
		String id = scanner.nextLine();
		output.println("Insert name:");
		String name = scanner.nextLine();
		output.println("Insert price:");
		String price = scanner.nextLine();
		output.println("Choose repetition:");
		output.println("1) Weekly");
		output.println("2) Monthly");
		output.println("3) Annual");
		int repetitionChoose = Integer.parseInt(scanner.nextLine());
		String repetition;
		switch (repetitionChoose) {
		case 1:
			repetition = "Weekly";
			break;
		case 2:
			repetition = "Monthly";
			break;
		case 3:	
			repetition = "Annual";
			break;
		default:
			output.println("Invalid digit");
			return;
		}
		controller.addSubscription(new Subscription(id, name, Double.valueOf(price), repetition));
	}
	
	public void deleteSubscription() {
		output.println("Insert id of subscription to delete:");
		String id = scanner.nextLine();
		output.println(id);
		Subscription subToDelete = list
				.stream()
				.filter(x -> x.getId().equals(id))
				.findFirst()
				.get();
		controller.deleteSubscription(subToDelete);
	}
	
	public void runView() {
		scanner = new Scanner(System.in);
		output.println("SUBSCRIPTIONS MANAGER");
		boolean exit = false;
		while (!exit) {
			showOptions();
			int choose = 0;
			boolean validInput = false;
			while(!validInput) {
				output.println("Choose operation...");
				try {
				 choose = Integer.parseInt(scanner.nextLine());
				 if (choose > 0 && choose < 6) {
					 validInput = true;
				 } else {
					 output.println("Input should be between 1 and 5"); 
				 }
				} catch (NumberFormatException e) {
					output.println("Invalid digit");
				}
			}
			switch(choose) {
			case 1:
				showAllSubscriptions(list);
				break;
			case 2:
				showSpending();
				break;
			case 3:	
				addSubscription();
				break;
			case 4:
				deleteSubscription();
				break;
			case 5:
				exit = true;
				break;
			default:
				break;
			}
		}
		output.print("Goodbye!");
	}
}
