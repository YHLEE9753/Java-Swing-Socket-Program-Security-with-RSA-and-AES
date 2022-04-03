package com.security.test;

import java.io.File;

public class PathTest {
    public static void main(String[] args) {
        File file = new File("");
        String rootPath = String.valueOf(file.getAbsoluteFile());
        rootPath += "\\src\\main\\java\\com\\security\\filestore";
        System.out.println("현재 프로젝트의 경로 : "+rootPath );
    }
}
