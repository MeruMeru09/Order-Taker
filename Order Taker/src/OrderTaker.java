/*
   BSCS A121
   Frank Garcia
   Mel Macabenta
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderTaker extends JFrame {
    private JPanel menuPanel, orderPanel, controlPanel, categoryPanel, adPanel;
    private JList<String> orderList;
    private DefaultListModel<String> listModel;
    private JLabel totalLabel;
    private double totalPrice = 0.0;
    private ArrayList<Double> priceList = new ArrayList<>();
    private Map<String, String[]> menuItemsByCategory;
    private Map<String, double[]> pricesByCategory;
    private Map<String, Integer> orderQuantities;

    public OrderTaker() {
        setTitle("Order Taker");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize panels
        menuPanel = new JPanel();
        orderPanel = new JPanel();
        controlPanel = new JPanel();
        categoryPanel = new JPanel();
        adPanel = new JPanel();

        // Set up category buttons
        String[] categories = {"Main Course", "Sides", "Drinks", "Desserts"};
        categoryPanel.setLayout(new GridLayout(1, categories.length, 5, 5));
        for (String category : categories) {
            JButton categoryButton = new JButton(category);
            categoryButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    updateMenuPanel(category);
                }
            });
            categoryPanel.add(categoryButton);
        }

        // Set up menu items by category
        menuItemsByCategory = new HashMap<>();
        pricesByCategory = new HashMap<>();

        menuItemsByCategory.put("Main Course", new String[]{
                "Porkchop - ₱150.00", "Chopsuey - ₱120.00", "Adobo - ₱130.00",
                "Bulalo - ₱250.00", "Lomi - ₱90.00", "Chicken Mami - ₱90.00",
                "Lumpiang Shanghai - ₱80.00","A5 Wagyu Steak - ₱2000.00", "Fried Chicken - ₱130.00","Buttered Scallops - ₱250.00",
                "Bistek - ₱90.00","Buttered Shrimp -250.00"
        });
        pricesByCategory.put("Main Course", new double[]{150.00, 120.00, 130.00,
                                                        250.00, 90.00, 90.00,
                                                        80.00,2000.00,130.00,250.00,90.00,250.00});

        menuItemsByCategory.put("Sides", new String[]{
                "Fries - ₱50.00", "Rice - ₱10.00", "Steam Dumplings - ₱90.00","Corn and Carrots - ₱40.00","Mash Potatoes - ₱40.00",
                "Kimchi - 70.00", "Caesar Salad - 100.00","Corn Chips - ₱45.00","California Maki - ₱130.00","Tteokbokki -70.00",
                "Ratatouille - ₱130.00","Kani Salad - ₱130.00"
        });
        pricesByCategory.put("Sides", new double[]{50.00, 10.00, 90.00,40.00,40.00,70.00,100.00,45.00,130.00,70.00,130.00,130.00});

        menuItemsByCategory.put("Drinks", new String[]{
                "Coke - ₱25.00", "Coffee - ₱30.00","Mountain Dew - ₱25.00","Tea - ₱30.00","Sting - 30.00","Gulaman - ₱20.00",
                "Cold Chocolate - 35.00","Bottled Water - ₱20.00","San Miguel Light - ₱60.00","Soju - ₱ 70.00","Iced Tea - 30.00",
                "Grape Fanta - ₱40.00"
        });
        pricesByCategory.put("Drinks", new double[]{25.00, 30.00,25.00,30.00,30.00,20.00,35.00,20.00,60.00,70.00,30.00,40.00});

        menuItemsByCategory.put("Desserts", new String[]{
                "Ice Cream - ₱20.00","Leche Flan - ₱35.00","Halo-Halo - ₱70.00","Mochi - ₱65.00",
                "Mixed Fruits - ₱50.00","Red Velvet Cake - ₱80.00","Mango Shake -₱75.00","Mentos - ₱1.00",
                "Brownies - ₱35.00","Tiramisu - 60.00","Turon - ₱20.00","Ginataang Halo-Halo - ₱35.00"
        });
        pricesByCategory.put("Desserts", new double[]{20.00,35.00,70.00,65.00,50.00,80.00,75.00,1.00,35.00,60.00,20.00,35.00});

        // Set up order quantities map
        orderQuantities = new HashMap<>();

        // Set up menu panel
        menuPanel.setLayout(new GridLayout(6, 1, 5, 5));
        menuPanel.setBorder(BorderFactory.createTitledBorder("Menu"));
        updateMenuPanel("Main Course");

        // Set up order panel
        orderPanel.setLayout(new BorderLayout());
        orderPanel.setBorder(BorderFactory.createTitledBorder("Order"));
        listModel = new DefaultListModel<>();
        orderList = new JList<>(listModel);
        totalLabel = new JLabel("Total: Php0.00");

        orderPanel.add(new JScrollPane(orderList), BorderLayout.CENTER);
        orderPanel.add(totalLabel, BorderLayout.SOUTH);

        // Set up control panel
        controlPanel.setLayout(new GridLayout(1, 2, 5, 5));
        JButton removeButton = new JButton("Remove Last Item");
        JButton finishButton = new JButton("Finish Order");

        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!listModel.isEmpty()) {
                    String lastItem = listModel.lastElement();
                    String itemName = lastItem.substring(0, lastItem.lastIndexOf(" -"));
                    double itemPrice = priceList.remove(priceList.size() - 1);

                    int currentQuantity = orderQuantities.get(itemName);
                    if (currentQuantity == 1) {
                        orderQuantities.remove(itemName);
                        listModel.removeElement(lastItem);
                    } else {
                        orderQuantities.put(itemName, currentQuantity - 1);
                        updateOrderList();
                    }

                    totalPrice -= itemPrice;
                    updateTotalPrice();
                }
            }
        });

        finishButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveOrderToFile();
                clearOrder();
            }
        });

        controlPanel.add(removeButton);
        controlPanel.add(finishButton);

        // Set up advertisement panel
        adPanel.setBorder(BorderFactory.createTitledBorder("Advertisement"));
        JLabel adLabel = new JLabel();
        adLabel.setIcon(new ImageIcon("IMAGES/WUWAADS.jpg"));
        adPanel.add(adLabel);

        // Add panels to frame
        add(categoryPanel, BorderLayout.NORTH);
        add(menuPanel, BorderLayout.WEST);
        add(orderPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        add(adPanel, BorderLayout.EAST);
    }

    private void updateMenuPanel(String category) {
        menuPanel.removeAll();
        String[] menuItems = menuItemsByCategory.get(category);
        double[] prices = pricesByCategory.get(category);

        Dimension buttonSize = new Dimension(200, 40);

        for (int i = 0; i < menuItems.length; i++) {
            JButton button = new JButton(menuItems[i]);
            int index = i;
            button.setPreferredSize(buttonSize);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String itemName = menuItems[index].substring(0, menuItems[index].lastIndexOf(" -"));
                    double itemPrice = prices[index];

                    orderQuantities.put(itemName, orderQuantities.getOrDefault(itemName, 0) + 1);
                    priceList.add(itemPrice);
                    totalPrice += itemPrice;

                    updateOrderList();
                    updateTotalPrice();
                }
            });
            menuPanel.add(button);
        }

        menuPanel.revalidate();
        menuPanel.repaint();
    }

    private void updateOrderList() {
        listModel.clear();
        for (Map.Entry<String, Integer> entry : orderQuantities.entrySet()) {
            listModel.addElement(entry.getKey() + " - Qty: " + entry.getValue());
        }
    }

    private void updateTotalPrice() {
        totalLabel.setText(String.format("Total: ₱%.2f", totalPrice));
    }

    private void saveOrderToFile() {
        try (FileWriter writer = new FileWriter("order.txt")) {
            for (int i = 0; i < listModel.size(); i++) {
                writer.write(listModel.getElementAt(i) + "\n");
            }
            writer.write(String.format("Total: ₱%.2f", totalPrice));
            JOptionPane.showMessageDialog(this, "Thank you!!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving order", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearOrder() {
        listModel.clear();
        priceList.clear();
        orderQuantities.clear();
        totalPrice = 0.0;
        updateTotalPrice();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new OrderTaker().setVisible(true);
            }
        });
    }
}
