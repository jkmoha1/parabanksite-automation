package com.parabank.utils;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestUtil {
    public static String captureScreenshot(WebDriver driver, String name){
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String path = ConfigReader.get("screenshot.path") + name + "_" + ts + ".png";
        File src = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        try { FileUtils.copyFile(src, new File(path)); } catch (IOException e){ throw new RuntimeException(e); }
        return path;
    }

    public static String uniq(){ return String.valueOf(System.currentTimeMillis()); }
    public static String expand(String s){ return s==null? null : s.replace("{uniq}", uniq()); }
}
