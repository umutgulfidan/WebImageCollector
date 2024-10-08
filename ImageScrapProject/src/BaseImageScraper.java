import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public abstract class BaseImageScraper implements ImageScraper {
    protected WebDriver driver; // Selenium WebDriver instance
    protected String downloadPath; // Download path
    protected int imageCount; // Total images to download
    protected int downloadedImages = 0; // Successfully downloaded images
    protected int errorCounter = 0; // Error counter
    protected int requestCounter = 0; // Request counter
    protected int waitTime; // Wait time for page loading

    // Constructor with parameters
    public BaseImageScraper(String downloadPath, int imageCount, int waitTime, BrowserType browserType) {
        this.downloadPath = downloadPath;
        this.imageCount = imageCount;
        this.waitTime = waitTime;

        // Set up the browser
        if (browserType == BrowserType.CHROME) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("start-maximized"); // Open browser in fullscreen
            this.driver = new ChromeDriver(options);
        } else if (browserType == BrowserType.FIREFOX) {
            FirefoxOptions options = new FirefoxOptions();
            options.addArguments("-start-maximized"); // Open browser in fullscreen
            this.driver = new FirefoxDriver(options);
        }
    }

    // Helper method to wait for a specified time
    protected void wait(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Close the WebDriver
    @Override
    public void quit() {
        if (driver != null) {
            driver.quit(); // Close the browser
        }
    }

    // Download an image from the specified URL
    protected void downloadImage(String imageUrl, String destinationPath) {
        try {
            URI uri = URI.create(imageUrl); // Convert URL to URI
            URL url = uri.toURL(); // Convert URI to URL
            try (InputStream in = url.openStream()) {
                // Save the image to the specified path
                Files.copy(in, Path.of(destinationPath), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Image downloaded: " + destinationPath);
            }
        } catch (IOException e) {
            // Handle errors
            if (e.getMessage().contains("403")) {
                System.out.println("Access denied: " + imageUrl); // Access denied error
            } else {
                System.out.println("Error occurred while downloading image: " + e.getMessage()); // General error
            }
            errorCounter++; // Increment error counter
            throw new RuntimeException("Error occurred while downloading the image: " + e.getMessage()); // Throw exception with message
        }
    }
}