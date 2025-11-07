package com.back.domain.wiseSaying.repository;

import com.back.domain.wiseSaying.entity.WiseSaying;

import java.io.IOException;
import java.util.ArrayList;
import java.io.*;
import java.util.List;
import java.util.Scanner;

public class WiseSayingRepository {

    private static final String DIR = "db/wiseSaying";
    private static final String LAST_ID_FILE = DIR + "/lastId.txt";

    private final List<WiseSaying> cache = new ArrayList<>();
    private int lastId;

    public WiseSayingRepository() {
        initStorage();
        lastId = readLastId();
        loadAllQuotes();
    }

    // 조회
    public List<WiseSaying> findAll() {
        return new ArrayList<>(cache);
    }

    public WiseSaying findById(int id) {
        for (WiseSaying w : cache) {
            if (w.getId() == id) return w;
        }
        return null;
    }

    public int save(String text, String author) {
        int id = ++lastId;
        WiseSaying w = new WiseSaying(id, text, author);
        cache.add(w);
        saveQuote(w);
        writeLastId(lastId);
        return id;
    }

    public void update(int id, String newText, String newAuthor) {
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getId() == id) {
                WiseSaying app = cache.get(i);
                app.setText(newText);
                app.setAuthor(newAuthor);
                saveQuote(app); // 덮어쓰기
                return;
            }
        }
    }

    public void delete(int id) {
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getId() == id) {
                cache.remove(i);
                deleteQuoteFile(id);
                return;
            }
        }
    }

    public boolean buildDataJson() {
        String path = DIR + "/data.json";
        try (FileWriter fw = new FileWriter(path)) {
            fw.write("[\n");
            for (int i = 0; i < cache.size(); i++) {
                WiseSaying app = cache.get(i);
                fw.write("  {\n");
                fw.write("    \"id\": " + app.getId() + ",\n");
                fw.write("    \"content\": " + jsonString(app.getText()) + ",\n");
                fw.write("    \"author\": " + jsonString(app.getAuthor()) + "\n");
                fw.write("  }");
                if (i < cache.size() - 1) fw.write(",");
                fw.write("\n");
            }
            fw.write("]");
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void initStorage() {
        File folder = new File(DIR);
        if (!folder.exists()) folder.mkdirs();
        File lastId = new File(LAST_ID_FILE);
        if (!lastId.exists()) {
            try (FileWriter fw = new FileWriter(lastId)) { fw.write("0"); }
            catch (IOException e) { System.out.println("초기화 오류: " + e.getMessage()); }
        }
    }

    private int readLastId() {
        try (Scanner fileScanner = new Scanner(new File(LAST_ID_FILE))) {
            return fileScanner.hasNextInt() ? fileScanner.nextInt() : 0;
        } catch (IOException e) {
            return 0;
        }
    }

    private void writeLastId(int id) {
        try (FileWriter fw = new FileWriter(LAST_ID_FILE)) {
            fw.write(String.valueOf(id));
        } catch (IOException e) {
            System.out.println("lastId 저장 오류: " + e.getMessage());
        }
    }

    private void saveQuote(WiseSaying app) {
        String path = DIR + "/" + app.getId() + ".json";
        try (FileWriter fw = new FileWriter(path)) {
            fw.write("{\n");
            fw.write("  \"id\": " + app.getId() + ",\n");
            fw.write("  \"text\": " + jsonString(app.getText()) + ",\n");
            fw.write("  \"author\": " + jsonString(app.getAuthor()) + "\n");
            fw.write("}");
        } catch (IOException e) {
            System.out.println("명언 저장 오류(id=" + app.getId() + "): " + e.getMessage());
        }
    }

    private void deleteQuoteFile(int id) {
        File f = new File(DIR + "/" + id + ".json");
        if (f.exists() && !f.delete()) {
            System.out.println("명언 파일 삭제 실패(id=" + id + ")");
        }
    }

    private void loadAllQuotes() {
        File folder = new File(DIR);
        if (!folder.exists()) return;

        File[] files = folder.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.getName().endsWith(".json")) { // json 파일만
                String fileName = file.getName();
                String idStr = fileName.substring(0, fileName.lastIndexOf(".json"));
                try {
                    int id = Integer.parseInt(idStr);
                    WiseSaying app = loadQuote(id);
                    if (app != null) cache.add(app);
                } catch (NumberFormatException ignore) {
                    // lastId.txt 등은 무시
                }
            }
        }
    }

    private WiseSaying loadQuote(int id) {
        String path = DIR + "/" + id + ".json";
        File file = new File(path);
        if (!file.exists()) return null;

        try (Scanner fileScanner = new Scanner(file)) {
            StringBuilder sb = new StringBuilder();
            while (fileScanner.hasNextLine()) sb.append(fileScanner.nextLine());
            String json = sb.toString();

            int quoteId = parseJsonInt(json, "id");
            String text = parseJsonString(json, "text");
            String author = parseJsonString(json, "author");

            return new WiseSaying(quoteId, text, author);
        } catch (IOException e) {
            System.out.println("명언 로드 오류(id=" + id + "): " + e.getMessage());
            return null;
        }
    }

    private int parseJsonInt(String json, String key) {
        String search = "\"" + key + "\":";
        int start = json.indexOf(search) + search.length();
        int end = json.indexOf(",", start);
        if (end == -1) end = json.indexOf("}", start);
        String value = json.substring(start, end).trim();
        return Integer.parseInt(value);
    }

    private String parseJsonString(String json, String key) {
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start == -1) return "";
        start = json.indexOf("\"", start + search.length()) + 1;
        int end = start;
        while (end < json.length()) {
            if (json.charAt(end) == '\"' && json.charAt(end - 1) != '\\') break;
            end++;
        }
        String value = json.substring(start, end);
        return value.replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }

    private String jsonString(String s) {
        if (s == null) return "null";
        String esc = s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
        return "\"" + esc + "\"";
    }
}
