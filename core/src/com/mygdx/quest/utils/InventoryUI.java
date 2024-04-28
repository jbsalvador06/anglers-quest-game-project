// package com.mygdx.quest.utils;

// import com.badlogic.gdx.scenes.scene2d.Group;
// import com.badlogic.gdx.scenes.scene2d.Stage;
// import com.badlogic.gdx.scenes.scene2d.ui.Label;
// import com.badlogic.gdx.scenes.scene2d.ui.Table;
// import com.mygdx.quest.entities.Player;

// public class InventoryUI extends Group {
    
//     private Table inventoryTable;

//     public InventoryUI() {
//         inventoryTable = new Table();
//         inventoryTable.top();
//         inventoryTable.left();

//         addActor(inventoryTable);
//     }

//     public void refresh(Player player) {
//         inventoryTable.clear();
//         for (Fish fish : player.getInventory()) {
//             // Label nameLabel = new Label(fish.getName(), skin);

//             // Image fishImage = new Image(fish.getIcon());

//             // inventoryTable.add(fishImage);
//             inventoryTable.add(nameLabel);
//             inventoryTable.row();
//         }
//     }

// }
