package it.pietro.subscriptionsmanager.view.swing;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import java.awt.Dimension;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import it.pietro.subscriptionsmanager.model.Subscription;
import it.pietro.subscriptionsmanager.view.SubscriptionView;
import it.pietro.subscriptionsmanager.spending.SubscriptionSpending;
import it.pietro.subscriptionsmanager.controller.SubscriptionController;

public class SubscriptionViewSwing extends JFrame implements SubscriptionView {
	
	private static final long serialVersionUID = 1L;
	
	private transient SubscriptionController controller;
	
	private JList<Subscription> listSubscriptions;
	private CustomListModel<Subscription> listSubscriptionsModel;

	private final JLabel amountTextLabel;
	private final JPanel contentPane;
	private final JTextField nameTextField;
	private final JTextField idTextField;
	private final JTextField priceTextField;
	private final JComboBox<String> repetitionDropDown;
	private final JButton addBtn;
	private final JLabel errorLbl;
	
	private final String[] repetition = {"Weekly", "Monthly", "Annual"};
	
	public CustomListModel<Subscription> getListSubscriptionModel() {
		return listSubscriptionsModel;
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SubscriptionViewSwing frame = new SubscriptionViewSwing();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public SubscriptionViewSwing() {
		
		listSubscriptionsModel = new CustomListModel<>();
		
		setMinimumSize(new Dimension(500, 330));
		setTitle("Subscriptions manager");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setMinimumSize(new Dimension(0, 0));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{61, 137, 69, 140, 0};
		gbl_contentPane.rowHeights = new int[]{0, 121, 30, 0, 30, 0, 15, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		//SPENDING TEXT LABEL
		JLabel spendingTextLabel = new JLabel("Total monthly spending:");
		spendingTextLabel.setName("spendingTextLabel");
		spendingTextLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_spendingTextLabel = new GridBagConstraints();
		gbc_spendingTextLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_spendingTextLabel.gridwidth = 3;
		gbc_spendingTextLabel.insets = new Insets(0, 0, 5, 5);
		gbc_spendingTextLabel.gridx = 0;
		gbc_spendingTextLabel.gridy = 0;
		contentPane.add(spendingTextLabel, gbc_spendingTextLabel);
		
		//AMOUNT TEXT LABEL
		amountTextLabel = new JLabel("0");
		amountTextLabel.setName("amountTextLabel");
		GridBagConstraints gbc_amountTextLabel = new GridBagConstraints();
		gbc_amountTextLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_amountTextLabel.insets = new Insets(0, 0, 5, 0);
		gbc_amountTextLabel.gridx = 3;
		gbc_amountTextLabel.gridy = 0;
		contentPane.add(amountTextLabel, gbc_amountTextLabel);
		
		//SCROLL PANE
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridwidth = 4;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		contentPane.add(scrollPane, gbc_scrollPane);
		
		//JLIST SUBSCRIPTIONS
		listSubscriptions = new JList<Subscription>(listSubscriptionsModel);
		scrollPane.setViewportView(listSubscriptions);
		listSubscriptions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listSubscriptions.setName("subscriptionList");
		
		//DELETE BUTTON
		JButton deleteBtn = new JButton("Delete selected");
		deleteBtn.setEnabled(false);
		deleteBtn.setName("deleteBtn");
		GridBagConstraints gbc_deleteBtn = new GridBagConstraints();
		gbc_deleteBtn.insets = new Insets(0, 0, 5, 0);
		gbc_deleteBtn.gridwidth = 4;
		gbc_deleteBtn.gridx = 0;
		gbc_deleteBtn.gridy = 2;
		contentPane.add(deleteBtn, gbc_deleteBtn);
		
		//ID TEXT LABEL
		JLabel idTextLabel = new JLabel("id");
		idTextLabel.setName("idTextLabel");
		idTextLabel.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_idTextLabel = new GridBagConstraints();
		gbc_idTextLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_idTextLabel.insets = new Insets(0, 0, 5, 5);
		gbc_idTextLabel.gridx = 0;
		gbc_idTextLabel.gridy = 3;
		contentPane.add(idTextLabel, gbc_idTextLabel);
		
		//ID TEXT FIELD
		idTextField = new JTextField();
		idTextField.setName("idTextField");
		GridBagConstraints gbc_idTextField = new GridBagConstraints();
		gbc_idTextField.insets = new Insets(0, 0, 5, 5);
		gbc_idTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_idTextField.gridx = 1;
		gbc_idTextField.gridy = 3;
		contentPane.add(idTextField, gbc_idTextField);
		idTextField.setColumns(10);
		
		//NAME TEXT LABEL
		JLabel nameTextLabel = new JLabel("name");
		nameTextLabel.setName("nameTextLabel");
		GridBagConstraints gbc_nameTextLabel = new GridBagConstraints();
		gbc_nameTextLabel.insets = new Insets(0, 0, 5, 5);
		gbc_nameTextLabel.gridx = 2;
		gbc_nameTextLabel.gridy = 3;
		contentPane.add(nameTextLabel, gbc_nameTextLabel);
		
		//NAME TEXT FIELD
		nameTextField = new JTextField();
		nameTextField.setName("nameTextField");
		GridBagConstraints gbc_nameTextField = new GridBagConstraints();
		gbc_nameTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_nameTextField.insets = new Insets(0, 0, 5, 0);
		gbc_nameTextField.gridx = 3;
		gbc_nameTextField.gridy = 3;
		contentPane.add(nameTextField, gbc_nameTextField);
		nameTextField.setColumns(10);
		
		//PRICE TEXT LABEL
		JLabel priceTextLabel = new JLabel("price");
		priceTextLabel.setHorizontalAlignment(SwingConstants.CENTER);
		priceTextLabel.setName("priceTextLabel");
		GridBagConstraints gbc_priceTextLabel = new GridBagConstraints();
		gbc_priceTextLabel.fill = GridBagConstraints.BOTH;
		gbc_priceTextLabel.insets = new Insets(0, 0, 5, 5);
		gbc_priceTextLabel.gridx = 0;
		gbc_priceTextLabel.gridy = 4;
		contentPane.add(priceTextLabel, gbc_priceTextLabel);
		
		//PRICE TEXT FIELD
		priceTextField = new JTextField();
		priceTextField.setName("priceTextField");
		GridBagConstraints gbc_priceTextField = new GridBagConstraints();
		gbc_priceTextField.insets = new Insets(0, 0, 5, 5);
		gbc_priceTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_priceTextField.gridx = 1;
		gbc_priceTextField.gridy = 4;
		contentPane.add(priceTextField, gbc_priceTextField);
		priceTextField.setColumns(10);
		
		//REPETITION TEXT LABEL
		JLabel repetitionTextLabel = new JLabel("repetition");
		repetitionTextLabel.setName("repetitionTextLabel");
		GridBagConstraints gbc_repetitionTextLabel = new GridBagConstraints();
		gbc_repetitionTextLabel.insets = new Insets(0, 0, 5, 5);
		gbc_repetitionTextLabel.gridx = 2;
		gbc_repetitionTextLabel.gridy = 4;
		contentPane.add(repetitionTextLabel, gbc_repetitionTextLabel);
		
		//REPETITION COMBO BOX
		repetitionDropDown = new JComboBox<String>(repetition);
		repetitionDropDown.setName("repetitionDropDown");
		GridBagConstraints gbc_repetitionDropDown = new GridBagConstraints();
		gbc_repetitionDropDown.fill = GridBagConstraints.HORIZONTAL;
		gbc_repetitionDropDown.insets = new Insets(0, 0, 5, 0);
		gbc_repetitionDropDown.gridx = 3;
		gbc_repetitionDropDown.gridy = 4;
		contentPane.add(repetitionDropDown, gbc_repetitionDropDown);
		
		//ADD BUTTON
		addBtn = new JButton("Add subscription");
		addBtn.setEnabled(false);
		addBtn.setName("addBtn");
		addBtn.setAutoscrolls(true);
		GridBagConstraints gbc_addBtn = new GridBagConstraints();
		gbc_addBtn.insets = new Insets(0, 0, 5, 0);
		gbc_addBtn.gridwidth = 4;
		gbc_addBtn.gridx = 0;
		gbc_addBtn.gridy = 5;
		contentPane.add(addBtn, gbc_addBtn);
		gbc_repetitionTextLabel.insets = new Insets(0, 0, 5, 5);
		gbc_repetitionTextLabel.gridx = 3;
		gbc_repetitionTextLabel.gridy = 4;
		
		//ERROR LABEL
		errorLbl = new JLabel(" ");
		errorLbl.setForeground(Color.RED);
		errorLbl.setName("errorLbl");
		errorLbl.setDoubleBuffered(true);
		errorLbl.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_errorLbl = new GridBagConstraints();
		gbc_errorLbl.fill = GridBagConstraints.BOTH;
		gbc_errorLbl.gridwidth = 4;
		gbc_errorLbl.gridx = 0;
		gbc_errorLbl.gridy = 6;
		contentPane.add(errorLbl, gbc_errorLbl);
		
		KeyAdapter btnAddEnabler = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				addBtn.setEnabled(
						!priceTextField.getText().trim().isEmpty() &&
						isDouble(priceTextField.getText().trim()) &&
						!nameTextField.getText().trim().isEmpty() &&
						!idTextField.getText().trim().isEmpty());
			}
		};
		
		idTextField.addKeyListener(btnAddEnabler);
		nameTextField.addKeyListener(btnAddEnabler);
		priceTextField.addKeyListener(btnAddEnabler);
		
		addBtn.addActionListener(e -> {
			controller.addSubscription(
					new Subscription(
							idTextField.getText(),
							nameTextField.getText(),
							Double.parseDouble(priceTextField.getText()),
							repetitionDropDown.getSelectedItem().toString())
					);
		});
		
		deleteBtn.addActionListener(e -> {
			controller.deleteSubscription(listSubscriptions.getSelectedValue());
		});
		
		listSubscriptions.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				deleteBtn.setEnabled(listSubscriptions.getSelectedIndex() != -1);
			}
		});
	}

	@Override
	public void showAllSubscriptions(List<Subscription> subs) {
		subs.stream().forEach(listSubscriptionsModel::addElement);	
	}

	@Override
	public void subscriptionAdded(Subscription sub) {
		listSubscriptionsModel.addElement(sub);
		updateAmountLabel();
		errorLbl.setText(" ");
	}

	@Override
	public void subscriptionRemoved(Subscription sub) {
		listSubscriptionsModel.removeElement(sub);
		updateAmountLabel();
		errorLbl.setText(" ");
	}
	
	@Override
	public void showSubscriptionAlreadyExistsError(Subscription sub) {
		errorLbl.setText("Error: Already existing subscription with id "+sub.getId());	
	}
	
	@Override
	public void showNonExistingSubscritptionError(Subscription sub) {
		errorLbl.setText("Error: No existing subscription with id "+sub.getId());
	}
	
	private void updateAmountLabel() {
		amountTextLabel.setText(Double.toString(SubscriptionSpending.computeSpending(listSubscriptionsModel.getList())));
	}
	
	public void setController(SubscriptionController controller) {
		this.controller = controller;
	}
	
	private boolean isDouble(String value) {
		try {
			Double.parseDouble(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
