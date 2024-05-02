package com.mygdx.quest.utils;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

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
                String description = (String) fishObj.get("description");
                String location = (String) fishObj.get("location");
                Rarity rarity = Rarity.valueOf((String) fishObj.get("rarity"));
                Number num = (Number) fishObj.get("weight");
                float weight = num.floatValue();
                // Number num2 = (Number) fishObj.get("price");
                // int price = num2.intValue();

                Fish fish = new Fish(name, description, location, rarity, weight);
                fishMap.put(name, fish);
        }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return fishMap;
    }
}
