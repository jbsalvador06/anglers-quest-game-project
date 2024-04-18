package com.mygdx.quest.utils;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;

public class Inventory {

    private ArrayList<Fish> inventory;

    public Inventory() {
        
    }

    public void setInventory(ArrayList<Fish> inventory) {
        this.inventory = inventory;
    }

    public void displayInventory(Skin skin, Table mainTable) {
        
        for (Fish fish : inventory) {
            Label fishNames = new Label(fish.getName(), skin);
            mainTable.add(fishNames).pad(5);
        }

        mainTable.add(new TextButton("Hello world", skin));

    }

}
