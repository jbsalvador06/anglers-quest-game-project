package com.mygdx.quest.utils;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class Inventory {

    private ArrayList<Fish> inventory;

    public Inventory() {
        
    }

    public void setInventory(ArrayList<Fish> inventory) {
        this.inventory = inventory;
    }

    public void displayInventory(Skin skin, Table mainTable) {
        
        int count = 0;

        for (Fish fish : inventory) {
            Label fishNames = new Label(fish.getName(), skin);
            fishNames.setFontScale(0.5f);
            mainTable.add(fishNames).pad(5);
            count++;

            if (count % 3 == 0) {
                mainTable.row();
            }
        }
        
    }

}
