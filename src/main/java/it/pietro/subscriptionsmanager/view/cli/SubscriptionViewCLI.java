package it.pietro.subscriptionsmanager.view.cli;

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
	
	public List<Subscription> getList() {
		return list;
	}
	
	public SubscriptionViewCLI() {
		list = new ArrayList<>();
	}
	
	public static void main(String[] args) {
		SubscriptionViewCLI view = new SubscriptionViewCLI();
		view.runView();
	};

	@Override
	public void showAllSubscriptions(List<Subscription> subs) {
		if (subs.size() == 0) {
			System.out.println("No subscriptions added");
		} else {
			System.out.println("All subscriptions:");
			for (Subscription sub : subs) {
				System.out.println(sub.toString());
			}
		}
	}

	@Override
	public void showSubscriptionAlreadyExistsError(Subscription sub) {
		System.out.println("Error: Already existing subscription with id "+sub.getId());

	}

	@Override
	public void showNonExistingSubscritptionError(Subscription sub) {
		try(Scanner sc = new Scanner(System.in)){
			int a= sc.nextInt();
			if (a==1) {
				System.out.println("Hai scelto 1");
			} else {
				System.out.print("Non hai scelto 1");
			}
		}
	}

	@Override
	public void subscriptionAdded(Subscription sub) {
		list.add(sub);
		System.out.println("Subscription "+sub.toString()+" added");

	}

	@Override
	public void subscriptionRemoved(Subscription sub) {
		list.remove(sub);
		System.out.println("Subscription "+sub.toString()+" removed");
	}
	
	public void showSpending() {
		System.out.println("Total monthly spending: "+(SubscriptionSpending.computeSpending(list)));
	}
	
	public void setController(SubscriptionController controller) {
		this.controller = controller;
	}
	
	public void showOptions() {
		System.out.println("1) Show all subscriptions");
		System.out.println("2) Show total spending");
		System.out.println("3) Add subscription");
		System.out.println("4) Delete subscription");
	}
	
	public void addSubscription() {
		System.out.println("Insert id:");
		String id = scanner.nextLine();
		System.out.println("Insert name:");
		String name = scanner.nextLine();
		System.out.println("Insert price:");
		String price = scanner.nextLine();
		System.out.println("Choose repetition:");
		System.out.println("1) Weekly");
		System.out.println("2) Monthly");
		System.out.println("3) Annual");
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
			System.out.println("Invalid digit");
			return;
		}
		controller.addSubscription(new Subscription(id, name, Double.valueOf(price), repetition));
	}
	
	public void deleteSubscription() {
		System.out.println("Insert id of subscription to delete:");
		String id = scanner.nextLine();
		System.out.println(id);
		Subscription subToDelete = list
				.stream()
				.filter(x -> x.getId().equals(id))
				.findFirst()
				.get();
		controller.deleteSubscription(subToDelete);
	}
	
	public void runView() {
		scanner = new Scanner(System.in);
		System.out.println("SUBSCRIPTIONS MANAGER");
		showOptions();
		System.out.print("Choose operation...");
		int choose = Integer.parseInt(scanner.nextLine());
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
		default:
			System.out.println("Invalid digit");
		}
	}
}
