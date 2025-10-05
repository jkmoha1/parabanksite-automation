package com.parabank.listeners;

import com.parabank.managers.DriverManager;
import com.parabank.utils.ConfigReader;
import org.openqa.selenium.*;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestListener implements ITestListener {

    private String screenshotsDir;   // e.g. "screenshots"
    private String logsDir;          // e.g. "logs"
    private String reportsDir;       // e.g. "test-output"

    private int total = 0;
    private int passed = 0;
    private int failed = 0;
    private int skipped = 0;

    @Override
    public void onStart(ITestContext context) {
        screenshotsDir = nz(ConfigReader.get("screenshot.path"), "screenshots");
        logsDir        = nz(ConfigReader.get("logs.path"),        "logs");
        reportsDir     = nz(ConfigReader.get("reports.path"),     "test-output");

        cleanDir(screenshotsDir); // fresh screenshots every run
        ensureDir(logsDir);
        ensureDir(reportsDir);

        log("Suite started: " + context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        log("Suite finished: " + context.getName());

        // Mirror TestNG output into reportsDir (usually "test-output")
        try {
            Path src = Path.of(context.getOutputDirectory());
            Path dest = Path.of(reportsDir);
            mirrorDirectory(src, dest);
            log("Reports available in: " + dest.toAbsolutePath());
        } catch (Exception e) {
            log("Could not mirror TestNG reports: " + e.getMessage());
        }

        // Summary -> log file only
        double passAllPct = total == 0 ? 0.0 : (passed * 100.0 / total);
        int executed = passed + failed; // skipped not executed
        double passExecPct = executed == 0 ? 0.0 : (passed * 100.0 / executed);

        String summary = String.format(
                "Summary -> Total: %d | Passed: %d | Failed: %d | Skipped: %d | Pass%%(all): %.2f%% | Pass%%(executed): %.2f%%",
                total, passed, failed, skipped, passAllPct, passExecPct
        );
        log(summary);
    }

    @Override
    public void onTestStart(ITestResult result) {
        total++;
        log("Starting Test: " + result.getName() + " [thread=" + Thread.currentThread().getId() + "]");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        passed++;
        log("Test Passed: " + result.getName());
        // no screenshot on pass
    }

    @Override
    public void onTestFailure(ITestResult result) {
        failed++;
        log("Test Failed: " + result.getName() + " | reason: " + safeMsg(result.getThrowable()));
        takeScreenshot(result.getName(), "FAIL"); // ONLY failure screenshots
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        skipped++;
        log("Test Skipped: " + result.getName());
        // no screenshot on skip
    }

    // ---------------- helpers ----------------

    private void takeScreenshot(String testName, String status) {
        WebDriver driver;
        try {
            driver = DriverManager.getDriver();
        } catch (IllegalStateException e) {
            log("Driver not initialized, skipping screenshot for: " + testName);
            return;
        }

        try {
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String thread = "T" + Thread.currentThread().getId();
            File destFile = new File(screenshotsDir + File.separator
                    + testName + "_" + status + "_" + thread + "_" + ts + ".png");

            ensureDir(destFile.getParentFile().getPath());
            Files.copy(srcFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            log("Screenshot saved: " + destFile.getAbsolutePath());
        } catch (WebDriverException wde) {
            log("Could not capture screenshot: " + wde.getClass().getSimpleName());
        } catch (IOException ioe) {
            log("IO error saving screenshot: " + ioe.getMessage());
        } catch (Throwable t) {
            log("Unexpected screenshot error: " + t.getMessage());
        }
    }

    private void mirrorDirectory(Path src, Path dest) throws IOException {
        if (src == null || !Files.exists(src)) return;
        ensureDir(dest.toString());
        Files.walk(src).forEach(from -> {
            try {
                Path to = dest.resolve(src.relativize(from).toString());
                if (Files.isDirectory(from)) {
                    Files.createDirectories(to);
                } else {
                    Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException ignored) {}
        });
    }

    private void cleanDir(String dir) {
        try {
            Path path = Path.of(dir);
            if (Files.exists(path)) {
                Files.walk(path)
                        .sorted((a, b) -> b.compareTo(a)) // files first
                        .forEach(p -> { try { Files.deleteIfExists(p); } catch (Exception ignored) {} });
            }
            Files.createDirectories(path);
        } catch (Exception ignored) {}
    }

    private void ensureDir(String dir) {
        try { Files.createDirectories(Path.of(dir)); } catch (Exception ignored) {}
    }

    private void log(String msg) {
        try {
            Path logFile = Path.of(logsDir, "execution.log");
            Files.createDirectories(logFile.getParent());
            String line = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    + "  " + msg + System.lineSeparator();
            Files.writeString(logFile, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ignored) {}
    }

    private String safeMsg(Throwable t) { return t == null ? "" : t.getMessage(); }
    private String nz(String v, String def) { return (v == null || v.isBlank()) ? def : v.trim(); }
}
