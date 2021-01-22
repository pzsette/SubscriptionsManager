package it.pietro.subscriptionsmanager.view.swing;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
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

public class SubscriptionViewSwing extends JFrame {

	private JPanel contentPane;
	private JTextField nameTextField;
	private JTextField idTextField;
	private JTextField priceTextField;
	private String[] repetition = {"Weekly", "Monthly", "Annual"};

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
		gbl_contentPane.rowHeights = new int[]{0, 121, 30, 0, 30, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JLabel spendingTextLabel = new JLabel("Total monthly spending");
		GridBagConstraints gbc_spendingTextLabel = new GridBagConstraints();
		gbc_spendingTextLabel.gridwidth = 3;
		gbc_spendingTextLabel.anchor = GridBagConstraints.EAST;
		gbc_spendingTextLabel.insets = new Insets(0, 0, 5, 5);
		gbc_spendingTextLabel.gridx = 0;
		gbc_spendingTextLabel.gridy = 0;
		contentPane.add(spendingTextLabel, gbc_spendingTextLabel);
		
		JLabel amountTextLabel = new JLabel("0");
		GridBagConstraints gbc_amountTextLabel = new GridBagConstraints();
		gbc_amountTextLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_amountTextLabel.insets = new Insets(0, 0, 5, 0);
		gbc_amountTextLabel.gridx = 3;
		gbc_amountTextLabel.gridy = 0;
		contentPane.add(amountTextLabel, gbc_amountTextLabel);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridwidth = 4;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		contentPane.add(scrollPane, gbc_scrollPane);
		
		JList list = new JList();
		scrollPane.setViewportView(list);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setName("subscriptionList");
		
		JButton deleteBtn = new JButton("Delete selected");
		deleteBtn.setName("deleteBtn");
		GridBagConstraints gbc_deleteBtn = new GridBagConstraints();
		gbc_deleteBtn.insets = new Insets(0, 0, 5, 0);
		gbc_deleteBtn.gridwidth = 4;
		gbc_deleteBtn.gridx = 0;
		gbc_deleteBtn.gridy = 2;
		contentPane.add(deleteBtn, gbc_deleteBtn);
		
		JLabel idTextLabel = new JLabel("id");
		GridBagConstraints gbc_idTextLabel = new GridBagConstraints();
		gbc_idTextLabel.insets = new Insets(0, 0, 5, 5);
		gbc_idTextLabel.gridx = 0;
		gbc_idTextLabel.gridy = 3;
		contentPane.add(idTextLabel, gbc_idTextLabel);
		
		idTextField = new JTextField();
		GridBagConstraints gbc_idTextField = new GridBagConstraints();
		gbc_idTextField.insets = new Insets(0, 0, 5, 5);
		gbc_idTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_idTextField.gridx = 1;
		gbc_idTextField.gridy = 3;
		contentPane.add(idTextField, gbc_idTextField);
		idTextField.setColumns(10);
		
		JLabel nameTextLabel = new JLabel("name");
		nameTextLabel.setName("idTextField");
		GridBagConstraints gbc_nameTextLabel = new GridBagConstraints();
		gbc_nameTextLabel.insets = new Insets(0, 0, 5, 5);
		gbc_nameTextLabel.gridx = 2;
		gbc_nameTextLabel.gridy = 3;
		contentPane.add(nameTextLabel, gbc_nameTextLabel);
		
		nameTextField = new JTextField();
		GridBagConstraints gbc_nameTextField = new GridBagConstraints();
		gbc_nameTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_nameTextField.insets = new Insets(0, 0, 5, 0);
		gbc_nameTextField.gridx = 3;
		gbc_nameTextField.gridy = 3;
		contentPane.add(nameTextField, gbc_nameTextField);
		nameTextField.setColumns(10);
		
		priceTextField = new JTextField();
		priceTextField.setName("nameTextField");
		GridBagConstraints gbc_priceTextField = new GridBagConstraints();
		gbc_priceTextField.insets = new Insets(0, 0, 5, 5);
		gbc_priceTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_priceTextField.gridx = 1;
		gbc_priceTextField.gridy = 4;
		contentPane.add(priceTextField, gbc_priceTextField);
		priceTextField.setColumns(10);
		
		JLabel priceTextLabel = new JLabel("price");
		priceTextLabel.setName("priceTextField");
		GridBagConstraints gbc_priceTextLabel = new GridBagConstraints();
		gbc_priceTextLabel.fill = GridBagConstraints.VERTICAL;
		gbc_priceTextLabel.insets = new Insets(0, 0, 5, 5);
		gbc_priceTextLabel.gridx = 0;
		gbc_priceTextLabel.gridy = 4;
		contentPane.add(priceTextLabel, gbc_priceTextLabel);
		
		JLabel repetitionTextLabel = new JLabel("repetition");
		GridBagConstraints gbc_repetitionTextLabel = new GridBagConstraints();
		gbc_repetitionTextLabel.insets = new Insets(0, 0, 5, 5);
		gbc_repetitionTextLabel.gridx = 2;
		gbc_repetitionTextLabel.gridy = 4;
		contentPane.add(repetitionTextLabel, gbc_repetitionTextLabel);
		
		JComboBox repetitionDropDown = new JComboBox(repetition);
		GridBagConstraints gbc_repetitionDropDown = new GridBagConstraints();
		gbc_repetitionDropDown.fill = GridBagConstraints.HORIZONTAL;
		gbc_repetitionDropDown.insets = new Insets(0, 0, 5, 0);
		gbc_repetitionDropDown.gridx = 3;
		gbc_repetitionDropDown.gridy = 4;
		contentPane.add(repetitionDropDown, gbc_repetitionDropDown);
		
		JButton addBtn = new JButton("Add subscription");
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
		
		JLabel lblNewLabel = new JLabel("New label");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.fill = GridBagConstraints.VERTICAL;
		gbc_lblNewLabel.gridwidth = 4;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 6;
		contentPane.add(lblNewLabel, gbc_lblNewLabel);
		
		
	}

}
