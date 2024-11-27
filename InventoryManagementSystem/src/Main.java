import java.util.*;
import java.util.stream.Collectors;

class InventoryItem {
    private final String id;
    private final String name;
    private final String category;
    private int quantity;

    public InventoryItem(String id, String name, String category, int quantity) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Category: " + category + ", Quantity: " + quantity;
    }
}

class InventoryManagementSystem {
    private final Map<String, InventoryItem> inventory = new HashMap<>();
    private final Map<String, TreeSet<InventoryItem>> categoryMap = new HashMap<>();
    private final int restockThreshold;

    public InventoryManagementSystem(int restockThreshold) {
        this.restockThreshold = restockThreshold;
    }

    public void addOrUpdateItem(String id, String name, String category, int quantity) {
        InventoryItem item = inventory.get(id);
        if (item != null) {
            removeItemFromCategory(item);
            item.setQuantity(quantity);
        } else {
            item = new InventoryItem(id, name, category, quantity);
            inventory.put(id, item);
        }
        addItemToCategory(item);
        checkRestockNotification(item);
    }

    public void removeItem(String id) {
        InventoryItem item = inventory.remove(id);
        if (item != null) {
            removeItemFromCategory(item);
        }
    }

    public List<InventoryItem> getItemsByCategory(String category) {
        TreeSet<InventoryItem> items = categoryMap.get(category);
        return items == null ? new ArrayList<>() : new ArrayList<>(items);
    }

    public void mergeInventory(InventoryManagementSystem other) {
        for (InventoryItem item : other.inventory.values()) {
            InventoryItem existingItem = inventory.get(item.getId());
            if (existingItem != null) {
                if (item.getQuantity() > existingItem.getQuantity()) {
                    addOrUpdateItem(item.getId(), item.getName(), item.getCategory(), item.getQuantity());
                }
            } else {
                addOrUpdateItem(item.getId(), item.getName(), item.getCategory(), item.getQuantity());
            }
        }
    }

    public List<InventoryItem> getTopKItems(int k) {
        return inventory.values().stream()
                .sorted(Comparator.comparingInt(InventoryItem::getQuantity).reversed())
                .limit(k)
                .collect(Collectors.toList());
    }

    private void addItemToCategory(InventoryItem item) {
        categoryMap.computeIfAbsent(item.getCategory(), k -> new TreeSet<>(
                Comparator.comparingInt(InventoryItem::getQuantity).reversed()
                        .thenComparing(InventoryItem::getId)
        )).add(item);
    }

    private void removeItemFromCategory(InventoryItem item) {
        TreeSet<InventoryItem> items = categoryMap.get(item.getCategory());
        if (items != null) {
            items.remove(item);
            if (items.isEmpty()) {
                categoryMap.remove(item.getCategory());
            }
        }
    }

    private void checkRestockNotification(InventoryItem item) {
        if (item.getQuantity() < restockThreshold) {
            System.out.println("Restock needed for item: " + item);
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        InventoryManagementSystem ims = new InventoryManagementSystem(10);

        while (true) {
            System.out.println("\nInventory Management System");
            System.out.println("1. Add/Update Item");
            System.out.println("2. Remove Item");
            System.out.println("3. View Items by Category");
            System.out.println("4. Merge Inventory");
            System.out.println("5. Get Top K Items by Quantity");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter ID: ");
                    String id = scanner.nextLine();
                    System.out.print("Enter Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter Category: ");
                    String category = scanner.nextLine();
                    System.out.print("Enter Quantity: ");
                    int quantity = scanner.nextInt();
                    ims.addOrUpdateItem(id, name, category, quantity);
                }
                case 2 -> {
                    System.out.print("Enter ID of the item to remove: ");
                    String id = scanner.nextLine();
                    ims.removeItem(id);
                }
                case 3 -> {
                    System.out.print("Enter Category: ");
                    String category = scanner.nextLine();
                    List<InventoryItem> items = ims.getItemsByCategory(category);
                    System.out.println("Items in category " + category + ":");
                    items.forEach(System.out::println);
                }
                case 4 -> {
                    System.out.println("Merging another inventory...");
                    InventoryManagementSystem other = new InventoryManagementSystem(10);
                    System.out.print("How many items to merge? ");
                    int count = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    for (int i = 0; i < count; i++) {
                        System.out.print("Enter ID: ");
                        String id = scanner.nextLine();
                        System.out.print("Enter Name: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter Category: ");
                        String category = scanner.nextLine();
                        System.out.print("Enter Quantity: ");
                        int quantity = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                        other.addOrUpdateItem(id, name, category, quantity);
                    }
                    ims.mergeInventory(other);
                }
                case 5 -> {
                    System.out.print("Enter the value of K: ");
                    int k = scanner.nextInt();
                    List<InventoryItem> topItems = ims.getTopKItems(k);
                    System.out.println("Top " + k + " items by quantity:");
                    topItems.forEach(System.out::println);
                }
                case 6 -> {
                    System.out.println("Exiting the system. Goodbye!");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
