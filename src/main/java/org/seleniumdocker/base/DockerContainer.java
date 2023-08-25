package org.seleniumdocker.base;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.rnorth.ducttape.timeouts.Timeouts;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static org.seleniumdocker.configuration.ConfigManager.config;

public class DockerContainer {
    private final static Logger logger = LoggerFactory.getLogger(DockerContainer.class);

    private GenericContainer<?> browserContainer;
    private GenericContainer<?> videoContainer;
    private String random;
    private String browserDockerImageName;
    public static Network network = Network.SHARED;
    private RemoteWebDriver driver;

    private Capabilities capabilities;

    public RemoteWebDriver SeleniumDockerContainer() {

        random = RandomStringUtils.randomAlphabetic(5);

        if (config().browser().equals("chrome")) {
            browserDockerImageName = "selenium/standalone-chrome:latest";
            capabilities = new ChromeOptions().addArguments("--no-sandbox").addArguments("--disable-dev-shm-usage");
        } else if (config().browser().equals("firefox")) {
            browserDockerImageName = "selenium/standalone-firefox:latest";
            capabilities = new FirefoxOptions();
        } else {
            throw new InvalidArgumentException("browser type not supported");
        }

        final WaitStrategy logWaitStrategy = new LogMessageWaitStrategy()
                .withRegEx(
                        ".*(RemoteWebDriver instances should connect to|Selenium Server is up and running|Started Selenium Standalone).*\n"
                )
                .withStartupTimeout(Duration.of(30, ChronoUnit.SECONDS));

        WaitAllStrategy waitAllStrategy = new WaitAllStrategy();
        waitAllStrategy.withStrategy(logWaitStrategy);
        waitAllStrategy.withStrategy(new HostPortWaitStrategy());
        waitAllStrategy.withStartupTimeout(Duration.of(30, ChronoUnit.SECONDS));

        int port = getAvailablePort();
        int port2 = getAvailablePort();

        browserContainer = new GenericContainer<>(DockerImageName.parse(browserDockerImageName))
                .withNetwork(network)
                .withCreateContainerCmdModifier(cmd -> cmd.withName("selenium-" + random))
                .withStartupTimeout(Duration.ofMinutes(2))
                .withEnv("SCREEN_WIDTH", "1920")
                .withEnv("SCREEN_HEIGHT", "1080")
                .withEnv("shm-size", "2g")
                .waitingFor(waitAllStrategy);

        browserContainer.getPortBindings().add(port + ":4444");
        browserContainer.getPortBindings().add(port2 + ":5900");

        try {
            browserContainer.start();
        } catch (Exception e) {
            throw new ContainerLaunchException(e.toString(), e.getCause());
        }

        driver = getWebDriver();
        driver.manage().window().setSize(new Dimension(1920, 1080));
        driver.setFileDetector(new LocalFileDetector());

        return driver;
    }

    public void stopSeleniumContainer() {
        browserContainer.stop();
    }

    public void startVideoRecordingContainer() {
        final WaitStrategy logWaitStrategy = new LogMessageWaitStrategy()
                .withRegEx(".*INFO success: video-recording entered RUNNING state,.*")
                .withStartupTimeout(Duration.of(15, ChronoUnit.SECONDS));

        WaitAllStrategy waitAllStrategy = new WaitAllStrategy();
        waitAllStrategy.withStrategy(logWaitStrategy);
        waitAllStrategy.withStrategy(new HostPortWaitStrategy());
        waitAllStrategy.withStartupTimeout(Duration.of(15, ChronoUnit.SECONDS));

        videoContainer = new GenericContainer<>(DockerImageName.parse("selenium/video:latest"))
                .withNetwork(network)
                .withCreateContainerCmdModifier(cmd -> cmd.withName("video-" + random))
                .withFileSystemBind(System.getProperty("user.dir") + "/recording", "/videos")
                .withEnv("SE_SCREEN_WIDTH", "1920")
                .withEnv("SE_SCREEN_HEIGHT", "1080")
                .withEnv("DISPLAY_CONTAINER_NAME", "selenium-" + random)
                .waitingFor(waitAllStrategy);

        try {
            videoContainer.start();
        } catch (Exception e) {
            throw new ContainerLaunchException(e.toString(), e.getCause());
        }
    }

    public void stopVideoContainerCmd() {
        videoContainer.getDockerClient().stopContainerCmd(videoContainer.getContainerId()).exec();
    }

    public void copyRecordingToHost(String methodName, String Status) {
        File file = new File(System.getProperty("user.dir") + "/recording/video.mp4");
        File file2 = new File(System.getProperty("user.dir") + "/recording/"
                + methodName
                + "_"
                + Status
                + "_"
                + System.currentTimeMillis() + ".mp4");
        if (!file.renameTo(file2)) {
            logger.error("Unable to rename file");
        }
    }

    public void stopVideoContainer() {
        videoContainer.stop();
    }

    private int getAvailablePort() {
        int port = 0;
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            port = serverSocket.getLocalPort();
            Assert.assertNotNull(serverSocket);
            Assert.assertTrue(serverSocket.getLocalPort() > 0);
        } catch (IOException e) {
            Assert.fail("Port is not available");
        }
        return port;
    }

    private synchronized RemoteWebDriver getWebDriver() {
        if (driver == null) {
            if (capabilities == null) {
                System.out.println("No capabilities provided - this will cause an exception in future versions. Falling back to ChromeOptions");
                capabilities = new ChromeOptions();
            }

            var ip = DockerClientFactory.instance().dockerHostIpAddress();
            System.out.println("http://" + browserContainer.getHost() + ":" + browserContainer.getMappedPort(4444) + "/wd/hub");
            driver =
                    Unreliables.retryUntilSuccess(
                            30,
                            TimeUnit.SECONDS,
                            () -> {
                                return Timeouts.getWithTimeout(
                                        10,
                                        TimeUnit.SECONDS,
                                        () -> new RemoteWebDriver(new URL("http://" + browserContainer.getHost() + ":" + browserContainer.getMappedPort(4444) + "/wd/hub"), capabilities)
                                );
                            }
                    );
        }
        return driver;
    }
}
