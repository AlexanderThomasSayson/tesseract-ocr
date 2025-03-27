package com.srllc.tesseract_ocr.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OpenCVLoader {
    private static boolean loaded = false;

    public static void loadOpenCV() {
        if (!loaded) {
            System.load("C:\\opencv\\build\\x64\\vc16\\bin\\opencv_java490.dll");
            loaded = true;
            log.info("OpenCV Loaded Successfully!");
        }
    }
}

