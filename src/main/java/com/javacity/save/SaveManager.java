package com.javacity.save;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SaveManager {
    private static final String SAVE_DIR = "saves/";

    public SaveManager() {
        File dir = new File(SAVE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public void save(SaveData data, String filename) {
        File file = new File(SAVE_DIR + filename + ".json");
        try (FileWriter writer = new FileWriter(file)) {
            String json = buildJson(data);
            writer.write(json);
            System.out.println("Game saved to " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to save game: " + e.getMessage());
        }
    }

    public SaveData load(String filename) {
        File file = new File(SAVE_DIR + filename + ".json");
        if (!file.exists()) {
            System.out.println("Save file not found.");
            return null;
        }

        try (FileReader reader = new FileReader(file)) {
            StringBuilder sb = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                sb.append((char) c);
            }
            return parseJson(sb.toString());
        } catch (IOException e) {
            System.err.println("Failed to load game: " + e.getMessage());
        }
        return null;
    }

    private String buildJson(SaveData data) {
        return "{\n" +
            "  \"playerPosition\": [" + data.playerPosition[0] + ", " + data.playerPosition[1] + ", " + data.playerPosition[2] + "],\n" +
            "  \"playerHealth\": " + data.playerHealth + ",\n" +
            "  \"money\": " + data.money + ",\n" +
            "  \"wantedLevel\": " + data.wantedLevel + ",\n" +
            "  \"currentMission\": \"" + data.currentMission + "\",\n" +
            "  \"missionState\": \"" + data.missionState + "\",\n" +
            "  \"timeOfDay\": " + data.timeOfDay + "\n" +
            "}";
    }

    private SaveData parseJson(String json) {
        SaveData data = new SaveData();
        try {
            data.playerHealth = extractFloat(json, "playerHealth");
            data.money = (int) extractFloat(json, "money");
            data.wantedLevel = (int) extractFloat(json, "wantedLevel");
            data.currentMission = extractString(json, "currentMission");
            data.missionState = extractString(json, "missionState");
            data.timeOfDay = extractFloat(json, "timeOfDay");
            
            String posStr = json.substring(json.indexOf("\"playerPosition\": [") + 19);
            posStr = posStr.substring(0, posStr.indexOf("]"));
            String[] parts = posStr.split(",");
            if(parts.length == 3) {
                data.playerPosition[0] = Float.parseFloat(parts[0].trim());
                data.playerPosition[1] = Float.parseFloat(parts[1].trim());
                data.playerPosition[2] = Float.parseFloat(parts[2].trim());
            }
        } catch(Exception e) {
            System.err.println("Error parsing save data JSON");
        }
        return data;
    }

    private float extractFloat(String json, String key) {
        String search = "\"" + key + "\":";
        int idx = json.indexOf(search);
        if (idx == -1) return 0f;
        idx += search.length();
        int end = json.indexOf(",", idx);
        if (end == -1) end = json.indexOf("\n", idx);
        if (end == -1) end = json.length();
        String val = json.substring(idx, end).trim();
        return Float.parseFloat(val);
    }
    
    private String extractString(String json, String key) {
        String search = "\"" + key + "\":";
        int idx = json.indexOf(search);
        if (idx == -1) return "";
        idx += search.length();
        int startQuote = json.indexOf("\"", idx);
        if(startQuote == -1) return "";
        int endQuote = json.indexOf("\"", startQuote + 1);
        if(endQuote == -1) return "";
        return json.substring(startQuote + 1, endQuote);
    }
}
