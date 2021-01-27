package it.pietro.subscriptionsmanager.spending;

import java.util.List;
import it.pietro.subscriptionsmanager.model.Subscription;

public class SubscriptionSpending {
	
	private SubscriptionSpending() {};
	
	public static double computeSpending(List<Subscription> subs) {
		Double price = 0.0;
		for(Subscription sub : subs) {
			if (sub.getRepetition().equals("Weekly")) {
				price += sub.getPrice()*4;
			} else if( sub.getRepetition().equals("Monthly")) {
				price += sub.getPrice();
			} else {
				price += sub.getPrice()/12;
			}
		}
		return price;
 	}
}