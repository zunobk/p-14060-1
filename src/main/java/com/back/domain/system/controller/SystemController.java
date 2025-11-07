package com.back.domain.system.controller;

import java.util.Scanner;

public class SystemController {
    private final Scanner sc;
    public SystemController(Scanner sc) { this.sc = sc; }

    public void exit() {
        // 종료시 추가 정리 필요 없으면 메시지 없이 바로 종료해도 됨
    }

    public void unknown(String cmd) {
        System.out.println("알 수 없는 명령: " + cmd);
    }
}
