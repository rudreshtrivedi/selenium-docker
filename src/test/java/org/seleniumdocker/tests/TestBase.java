package org.seleniumdocker.tests;

import org.openqa.selenium.remote.RemoteWebDriver;
import org.seleniumdocker.base.BasePageFactory;
import org.seleniumdocker.base.BrowserEnum;
import org.seleniumdocker.base.DockerContainer;
import org.seleniumdocker.configuration.ConfigManager;
import org.seleniumdocker.pages.LoginPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

public class TestBase {
    private final static Logger logger = LoggerFactory.getLogger(TestBase.class);
    private RemoteWebDriver driver;
    protected LoginPage loginPage;

    DockerContainer container = new DockerContainer();

    @BeforeClass(alwaysRun = true)
    public void beforeClassTestBase() throws Exception {
        if (ConfigManager.config().containerized()) {
            container = new DockerContainer();
            driver = container.SeleniumDockerContainer();
        } else {
            driver = BrowserEnum.valueOf(ConfigManager.config().browser().toUpperCase()).getDriver();
        }

        loginPage = BasePageFactory.createInstance(driver, LoginPage.class);
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethodBase() {
        if (ConfigManager.config().containerized()) {
            container.startVideoRecordingContainer();
        }
    }

    @AfterMethod(alwaysRun = true)
    public void afterTestContainer(ITestResult result) throws InterruptedException {
        if (ConfigManager.config().containerized()) {
            String status;
            if (result.getStatus() == ITestResult.SUCCESS) {
                status = "PASSED";
            } else if (result.getStatus() == ITestResult.FAILURE) {
                status = "FAILED";
                Thread.sleep(5000); // getting recording for 5 second additionally
            } else {
                status = "SKIPPED";
            }

            container.stopVideoContainerCmd();
            container.copyRecordingToHost(result.getMethod().getMethodName(), status);
            container.stopVideoContainer();
        }
    }

    @AfterClass(alwaysRun = true)
    public void afterClass(ITestContext result) {
        if (ConfigManager.config().containerized()) {
            container.stopSeleniumContainer();
        } else {
            driver.quit();
        }
    }
}
