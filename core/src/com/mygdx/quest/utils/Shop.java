package com.mygdx.quest.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shop {
    
    private List<Fish> playerInventory;

    private Map<String, Integer> upgrades;

    private int playerCoins;

    public Shop(List<Fish> playerInventory) {
        this.playerInventory = playerInventory;
        playerCoins = 0;

        upgrades = new HashMap<>();
        upgrades.put("Fishing Rod", 100);
        upgrades.put("Cooler", 250);
        upgrades.put("Boat", 500);
    }

    public void sellFish() {
        // Print player inventory
        System.out.println("Your fish:");
        System.out.println("Coins: " + playerCoins);
        for (Fish fish : playerInventory) {
            System.out.println("- " + fish.getName() + " (" + fish.getPrice() + ")");
        }

        // Sell fish
        System.out.print("Enter fish name to sell: ");
        String fishName = System.console().readLine();

        // Remove fish from inventory and add money
        for (Fish fish : playerInventory) {
            if (fish.getName().equals(fishName)) {
                playerCoins += fish.getPrice();
                playerInventory.remove(fish);
                System.out.println("Sold " + fishName + " for " + fish.getPrice());
                return;
            }
        }
        System.out.println("Fish not found!");
    }

    public void buyUpgrades() {
        // Print available upgrades
        System.out.println("Upgrades available:");
        for (String name : upgrades.keySet()) {
            System.out.println("- " + name + " (" + upgrades.get(name) + ")" );
        }

        // Purchase upgrades
        System.out.print("Enter upgrade to buy: ");
        String upgradeName = System.console().readLine();

        if (upgrades.containsKey(upgradeName)) {
            int cost = upgrades.get(upgradeName);
            playerCoins -= cost;
            System.out.println("Purchased " + upgradeName + " for " + cost);

            // TODO: Apply upgrade to player
        } else {
            System.out.println("Invalid upgrade!");
        }
    }

}
