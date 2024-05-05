package com.mygdx.quest.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

public class Shop {
    
    private List<Fish> playerInventory;

    private Map<String, Integer> upgrades;

    private int playerCoins;

    private boolean isShopWindowOpen = false;
    private boolean isSellWindowOpen = false;

    public Shop(List<Fish> playerInventory) {
        this.playerInventory = playerInventory;
        playerCoins = 0;

        upgrades = new HashMap<>();
        upgrades.put("Bait", 50);
        upgrades.put("Hook", 250);
        upgrades.put("Bobber", 350);
        upgrades.put("High Test Fishing Line", 500);
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
        System.out.println("Coins: " + playerCoins);
        for (String name : upgrades.keySet()) {
            System.out.println("- " + name + " (" + upgrades.get(name) + ")" );
        }

        // Purchase upgrades
        System.out.print("Enter upgrade to buy: ");
        String upgradeName = System.console().readLine();

        if (upgrades.containsKey(upgradeName)) {
            int cost = upgrades.get(upgradeName);

            if (playerCoins > cost) {
                playerCoins -= cost;
                System.out.println("Purchased " + upgradeName + " for " + cost);

                // TODO: Apply upgrade to player
            } else {
                System.out.println("Not enough coins!");
            }
        } else {
            System.out.println("Invalid upgrade!");
        }
    }

    public Window sellWindow(Skin skin) {
        Window window = new Window("Sell Fish", skin);
        TextButton closeButton = new TextButton("X", skin);
        closeButton.right();

        window.add(closeButton);
        window.setResizable(true);

        window.pack();
        window.setPosition(0, 0);

        return window;
    }

}
