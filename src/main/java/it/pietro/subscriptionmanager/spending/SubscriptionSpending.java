package it.pietro.subscriptionmanager.spending;

import java.util.List;
import it.pietro.subscriptionsmanager.model.Subscription;

public class SubscriptionSpending {
	
	public SubscriptionSpending() {};
	
	public double computeSpending(List<Subscription> subs) {
		Double price = 0.0;
		for(Subscription sub : subs) {
			if (sub.getRepetition() == "Weekly") {
				price += sub.getPrice()*4;
			} else if( sub.getRepetition() == "Monthly") {
				price += sub.getPrice();
			} else {
				price += sub.getPrice()/12;
			}
		}
		return price;
 	}

}