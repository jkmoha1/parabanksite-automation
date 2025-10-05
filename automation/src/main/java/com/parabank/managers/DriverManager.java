package com.parabank.managers;

import com.parabank.utils.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

public class DriverManager {

    private static final ThreadLocal<WebDriver> TL = new ThreadLocal<>();

    // start driver with given browser
    public static void initDriver(String browserFromTestNgParam) {
        if (TL.get() != null) {
            System.out.println("[DriverManager] Driver already initialized");
            return;
        }

        // pick browser from system, testng or config
        String b = firstNonBlank(
                System.getProperty("browser"),
                browserFromTestNgParam,
                ConfigReader.get("browser"),
                "chrome"
        ).trim().toLowerCase();

        boolean headless = parseBool(ConfigReader.get("headless"), false);

        String pinnedChrome = blankToNull(ConfigReader.get("wdm.chrome.version"));
        String pinnedGecko  = blankToNull(ConfigReader.get("wdm.gecko.version"));
        String chromeBinary = blankToNull(ConfigReader.get("chrome.binary"));
        String ffBinary     = blankToNull(ConfigReader.get("firefox.binary"));

        System.out.printf("[DriverManager] Requested browser=%s, headless=%s%n", b, headless);

        WebDriver driver;
        try {
            switch (b) {
                case "firefox" -> {
                    WebDriverManager wdm = WebDriverManager.firefoxdriver()
                            .avoidBrowserDetection()
                            .clearResolutionCache()
                            .avoidExport();
                    if (pinnedGecko != null) {
                        System.out.println("[DriverManager] Using pinned GeckoDriver " + pinnedGecko);
                        wdm.driverVersion(pinnedGecko);
                    }
                    wdm.setup();
                    FirefoxOptions fo = new FirefoxOptions();
                    if (ffBinary != null) {
                        System.out.println("[DriverManager] Using Firefox binary: " + ffBinary);
                        fo.setBinary(ffBinary);
                        System.setProperty("webdriver.firefox.bin", ffBinary);
                    }
                    if (headless) fo.addArguments("-headless");
                    fo.addPreference("network.proxy.type", 0);
                    fo.addPreference("dom.file.createInChild", true);
                    driver = new FirefoxDriver(fo);
                }
                case "chrome" -> {
                    WebDriverManager wdm = WebDriverManager.chromedriver()
                            .clearResolutionCache()
                            .avoidExport();
                    if (pinnedChrome != null) {
                        System.out.println("[DriverManager] Using pinned ChromeDriver " + pinnedChrome);
                        wdm.driverVersion(pinnedChrome);
                    } else {
                        try { wdm.setup(); }
                        catch (Exception ex) {
                            System.out.println("[DriverManager] WDM detection failed for Chrome, retrying with avoidBrowserDetection()");
                            WebDriverManager.chromedriver().avoidBrowserDetection().setup();
                        }
                    }
                    if (wdm.getDownloadedDriverPath() == null) {
                        wdm.setup();
                    }

                    ChromeOptions co = new ChromeOptions();
                    co.addArguments(
                            "--disable-notifications",
                            "--start-maximized",
                            "--remote-allow-origins=*"
                    );
                    if (chromeBinary != null) {
                        System.out.println("[DriverManager] Using Chrome binary: " + chromeBinary);
                        co.setBinary(chromeBinary);
                        System.setProperty("webdriver.chrome.bin", chromeBinary);
                    }
                    if (headless) {
                        co.addArguments("--headless=new");
                    }
                    driver = new ChromeDriver(co);
                }
                default -> throw new IllegalArgumentException("Browser not supported: " + b);
            }

            // set window and timeouts
            try { driver.manage().window().maximize(); } catch (Exception ignored) {}
            int iw = ConfigReader.getInt("implicit.wait", 10);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(iw));

            TL.set(driver);
            System.out.println("[DriverManager] Driver initialized OK (" + b + ")");
        } catch (Exception e) {
            System.err.println("[DriverManager] Failed to initialize driver");
            e.printStackTrace();
            throw e;
        }
    }

    // get current driver
    public static WebDriver getDriver() {
        WebDriver d = TL.get();
        if (d == null) {
            throw new IllegalStateException("Driver not initialized. Call initDriver() first. " +
                    "Check earlier logs from [BaseTest]/[DriverManager] for the root cause.");
        }
        return d;
    }

    // quit driver and remove from threadlocal
    public static void quitDriver() {
        WebDriver d = TL.get();
        if (d != null) {
            try { d.quit(); } finally { TL.remove(); }
        }
    }

    // helpers
    private static boolean parseBool(String v, boolean def) {
        try { return (v != null) ? Boolean.parseBoolean(v.trim()) : def; }
        catch (Exception e) { return def; }
    }
    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
    private static String firstNonBlank(String... vals) {
        if (vals == null) return null;
        for (String v : vals) {
            if (v != null && !v.isBlank()) return v;
        }
        return null;
    }
}
