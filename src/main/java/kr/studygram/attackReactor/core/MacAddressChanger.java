package kr.studygram.attackReactor.core;

import kr.studygram.attackReactor.utils.Logger;
import kr.studygram.attackReactor.utils.SecureConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.util.Random;

/**
 * Created by cynos07 on 2017-05-15.
 */
public enum MacAddressChanger implements Runnable {
    INSTANCE;
    private WebDriver driver;
    private Logger logger;
    private SecureConfig config;
    private WebElement element;
    private static final String PREFIX = "http://yirang3.iptime.org:1004";
    private static final String LOGIN_MENU = PREFIX + "/sess-bin/login_session.cgi";
    private static final String MAIN_MENU = PREFIX + "/sess-bin/login.cgi";
    private static final String SETTING_MENU = PREFIX + "/sess-bin/timepro.cgi?tmenu=main_frame&smenu=main_frame";
    private String router_name = "A1004NS";
    private String router_version = "9-98-4";
    private String prefix = router_name+router_version;

    public void initialize() {
        driver = new HtmlUnitDriver(true);
        logger = Logger.getInstance();
        config = SecureConfig.getInstance();
        router_name = config.getString("router.name");
        router_version = config.getString("router.version");

        prefix = router_name + router_version;
    }

    @Override
    public void run() {
        initialize();
        driver.get(SETTING_MENU);
        if (isLoginMenu()) {
            element = driver.findElement(By.xpath(config.getString(prefix+".login_page.username")));
            element.sendKeys(config.getString("administrator.id"));

            element = driver.findElement(By.xpath(config.getString(prefix+".login_page.password")));
            element.sendKeys(config.getString("administrator.password"));
            element = driver.findElement(By.xpath(config.getString(prefix+".login_page.submit_button")));
            element.click();
            if (isMainMenu()) {
                element = driver.findElement(By.xpath(config.getString(prefix+".main_page.setting_button")));
                element.click();
                if (isSettingMenu()) {
                    driver.switchTo().frame("main_body");
                    driver.switchTo().frame("navi_menu_basic");
                    element = driver.findElement(By.xpath(config.getString(prefix+".setting_page.settingInternetConnection")));
                    element.click();
                    driver.switchTo().defaultContent();
                    driver.switchTo().frame("main_body");
                    driver.switchTo().frame("main");
                    element = driver.findElement(By.xpath(config.getString(prefix+".setting_page.macAddress")));
                    element.sendKeys(String.valueOf(getRandomAddress(element.getAttribute("value"))));
                    System.out.println(element.getAttribute("value"));
                }
            } else {
                logger.log("ERROR", "공유기의 메인 메뉴가 아닙니다.");
                System.out.println(driver.getCurrentUrl());
            }
        } else {;
            logger.log("ERROR", "공유기의 로그인 페이지가 아닙니다.");
        }
    }

    private int getRandomAddress(String currentAddress) {
        Random generator = new Random();
        int newAddress = generator.nextInt(70)+10;
        if(newAddress == Integer.parseInt(currentAddress))
        {
            newAddress-=1;
        }
        return newAddress;
    }

    private boolean isLoginMenu() {
        if (driver.getCurrentUrl().contains(LOGIN_MENU)) {
            return true;
        }
        return false;
    }

    private boolean isMainMenu() {
        if (driver.getCurrentUrl().contains(MAIN_MENU)) {
            return true;
        }
        return false;
    }

    private boolean isSettingMenu() {
        if (driver.getCurrentUrl().contains(SETTING_MENU)) {
            return true;
        }
        return false;
    }

    public static MacAddressChanger getInstance() {
        return INSTANCE;
    }
};