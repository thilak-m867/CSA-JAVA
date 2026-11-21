package TaskA2;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

record Product(String sku, String name, String category, int stock, double unitPrice) {}

public class WarehouseAnalytics {
    public static void main(String[] args) {
        List<Product> inventory = List.of(
                new Product("SKU-101", "Mechanical Keyboard", "Electronics", 8, 3200.0),
                new Product("SKU-102", "Wireless Mouse", "Electronics", 42, 1100.0),
                new Product("SKU-103", "Ergonomic Chair", "Furniture", 4, 14500.0),
                new Product("SKU-104", "Standing Desk", "Furniture", 12, 22000.0),
                new Product("SKU-105", "USB-C Hub", "Electronics", 5, 2400.0),
                new Product("SKU-106", "A4 Printer Paper", "Stationery", 120, 350.0)
        );

        // 1. Filter low-stock items (stock < 10) sorted by stock ascending
        List<Product> lowStock = inventory.stream()
                .filter(p -> p.stock() < 10)
                .sorted(Comparator.comparingInt(Product::stock))
                .toList();
        System.out.println("Low-Stock Items (< 10 units):");
        lowStock.forEach(p -> System.out.println("  " + p.sku() + " - " + p.name() + " (Stock: " + p.stock() + ")"));

        // 2. Total inventory valuation per category
        Map<String, Double> valuationByCategory = inventory.stream()
                .collect(Collectors.groupingBy(
                        Product::category,
                        Collectors.summingDouble(p -> p.stock() * p.unitPrice())
                ));
        System.out.println("\nInventory Valuation by Category:");
        valuationByCategory.forEach((cat, val) ->
                System.out.printf("  %-14s : Rs %.2f%n", cat, val));

        // 3. Most expensive product per category
        System.out.println("\nHighest-Priced Product per Category:");
        inventory.stream()
                .collect(Collectors.groupingBy(
                        Product::category,
                        Collectors.maxBy(Comparator.comparingDouble(Product::unitPrice))
                ))
                .forEach((cat, optProd) ->
                        optProd.ifPresent(p -> System.out.println("  " + cat + " -> " + p.name() + " (Rs " + p.unitPrice() + ")")));
    }
}