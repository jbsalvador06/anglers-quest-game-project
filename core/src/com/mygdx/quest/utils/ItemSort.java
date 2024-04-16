package com.mygdx.quest.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.mygdx.quest.utils.Fish.Rarity;

public class ItemSort {
    
    public static void bucketSort(List<Fish> inventory, Comparator<Fish> comparator) {
    
        // Create buckets
        @SuppressWarnings("unchecked")
        List<Fish>[] buckets = new List[Rarity.values().length];
        for (int i = 0; i < buckets.length; i++) {
          buckets[i] = new ArrayList<>(); 
        }
    
        // Distribute items into buckets
        for (Fish item : inventory) {
          buckets[item.getRarity().ordinal()].add(item);
        }
    
        // Sort each bucket
        for (List<Fish> bucket : buckets) {
        //   Collections.sort(bucket, comparator); 
            bucket.sort(Comparator.comparing(Fish::getWeight));
        }
    
        // Gather sorted buckets
        int index = 0;
        for (List<Fish> bucket : buckets) {
          for (Fish item : bucket) {
            inventory.set(index, item);
            index++;
          }
        }
      }
    
      public static ArrayList<Fish> sort(ArrayList<Fish> inventory) {
        Comparator<Fish> comparator = Comparator.comparing(Fish::getLocation);
        bucketSort(inventory, comparator);
        return inventory;
      }

}
