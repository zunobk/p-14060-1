package com.back;

import com.back.domain.system.controller.SystemController;
import com.back.domain.wiseSaying.controller.WiseSayingController;

import java.util.Scanner;

public class App {
    private final Scanner sc = new Scanner(System.in);
    private final SystemController systemController = new SystemController(sc);
    private final WiseSayingController wiseSayingController = new WiseSayingController(sc);
    public void run() {
        System.out.println("== 명언 앱 ==");
        while (true) {
            System.out.print("명령) ");
            String cmd = sc.nextLine().trim();

            if (cmd.equals("종료")) {
                systemController.exit();
                break;
            } else if (cmd.equals("목록")) {
                wiseSayingController.list();
            } else if (cmd.equals("등록")) {
                wiseSayingController.create();
            } else if (cmd.startsWith("삭제")) {
                wiseSayingController.delete(cmd);
            } else if (cmd.startsWith("수정")) {
                wiseSayingController.update(cmd);
            } else if (cmd.equals("빌드")) {
                wiseSayingController.buildDataJson();
            } else {
                systemController.unknown(cmd);
            }
        }
    }
}
