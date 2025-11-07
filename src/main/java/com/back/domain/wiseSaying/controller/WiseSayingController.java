package com.back.domain.wiseSaying.controller;

import com.back.domain.wiseSaying.entity.WiseSaying;
import com.back.domain.wiseSaying.service.WiseSayingService;

import java.util.List;
import java.util.Scanner;

public class WiseSayingController {
    private final Scanner sc;
    private final WiseSayingService service = new WiseSayingService();

    public WiseSayingController(Scanner sc) { this.sc = sc; }

    public void list() {
        List<WiseSaying> list = service.findAll();
        System.out.println("번호 / 작가 / 명언");
        System.out.println("----------------------");
        for (int i = list.size() - 1; i >= 0; i--) {
            WiseSaying w = list.get(i);
            System.out.println(w.getId() + " / " + w.getAuthor() + " / " + w.getText());
        }
    }

    public void create() {
        System.out.print("명언 : ");
        String text = sc.nextLine();
        System.out.print("작가 : ");
        String author = sc.nextLine();
        int id = service.create(text, author);
        System.out.println(id + "번 명언이 등록되었습니다.");
    }

    public void delete(String cmd) {
        Integer id = parseId(cmd, "삭제");
        if (id == null) return;

        if (service.findById(id) == null) {
            System.out.println(id + "번 명언은 존재하지 않습니다.");
            return;
        }

        service.delete(id);
        System.out.println(id + "번 명언이 삭제되었습니다.");
    }

    public void update(String cmd) {
        Integer id = parseId(cmd, "수정");
        if (id == null) return;
        WiseSaying found = service.findById(id);
        if (found == null) {
            System.out.println(id + "번 명언은 존재하지 않습니다.");
            return;
        }
        System.out.println("명언(기존) : " + found.getText());
        System.out.print("명언 : ");
        String newText = sc.nextLine();
        System.out.println("작가(기존) : " + found.getAuthor());
        System.out.print("작가 : ");
        String newAuthor = sc.nextLine();

        service.update(id, newText, newAuthor);
        System.out.println(id + "번 명언이 수정되었습니다.");
    }

    public void buildDataJson() {
        boolean ok = service.buildDataJson();
        if (ok) System.out.println("data.json 파일의 내용이 갱신되었습니다.");
        else    System.out.println("data.json 생성 오류가 발생했습니다.");
    }

    private Integer parseId(String cmd, String keyword) {
        try {
            String[] parts = cmd.split("\\?");
            String[] kv = parts[1].split("=");
            if (!"id".equals(kv[0])) throw new IllegalArgumentException();
            return Integer.parseInt(kv[1].trim());
        } catch (Exception e) {
            System.out.println("올바른 형식이 아닙니다. 예) " + keyword + "?id=1");
            return null;
        }
    }
}
