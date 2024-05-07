package com.mygdx.quest.utils;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.mygdx.quest.utils.Fish.Rarity;

public class FishParser {

    public static Map<String, Fish> parseFishJson(String fileName) {
        
        JSONParser parser = new JSONParser();
        Map<String, Fish> fishMap = new HashMap<>();

        try {
            Object obj = parser.parse(new FileReader(fileName));
            JSONArray fishData = (JSONArray) obj;
    
            for (Object fishObject : fishData) {
                JSONObject fishObj = (JSONObject) fishObject;
    
                String name = (String) fishObj.get("name");
                String description1 = (String) fishObj.get("description1");
                String description2 = (String) fishObj.get("description2");
                String description = (String) description1 + "\n" + description2;
                String location = (String) fishObj.get("location");
                Rarity rarity = Rarity.valueOf((String) fishObj.get("rarity"));
                Number num = (Number) fishObj.get("weight");
                float weight = num.floatValue();
                String imgUrl = (String) fishObj.get("imgUrl");

                Fish fish = new Fish(name, description, location, rarity, weight, imgUrl);
                fishMap.put(name, fish);
        }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return fishMap;
    }

    public static Fish getRandomFish(Map<String, Fish> fishMap) {
        Random rand = new Random();
        int randomIndex = rand.nextInt(fishMap.size());
        return (Fish) fishMap.values().toArray()[randomIndex];
    }

    public static Fish getRandomCommonFish(Map<String, Fish> fishMap) {
        Random rand = new Random();
        ArrayList<Fish> commonFish = new ArrayList<>();
        for (Fish fish : fishMap.values()) {
            if (fish.getRarity() == Rarity.COMMON) {
                commonFish.add(fish);
            }
        }
        int randomIndex = rand.nextInt(commonFish.size());
        return (Fish) commonFish.toArray()[randomIndex];
    }
}
