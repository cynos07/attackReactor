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
    private static final String PREFIX = "http://192.168.0.1";
    private static final String LOGIN_MENU = PREFIX + "/sess-bin/login_session.cgi";
    private static final String MAIN_MENU = PREFIX + "/sess-bin/login.cgi";
    private static final String SETTING_MENU = PREFIX + "/sess-bin/timepro.cgi?tmenu=main_frame&smenu=main_frame";
    private String router_name = "A1004NS";
    private String router_version = "9-98-4";
    private String prefix = router_name+router_version;
    private boolean isAttack;

    public void initialize() {
        driver = new HtmlUnitDriver(true);
        logger = Logger.getInstance();
        config = SecureConfig.getInstance();
        router_name = config.getString("router.name");
        router_version = config.getString("router.version");
        isAttack = true;

        prefix = router_name + router_version;
    }

    @Override
    public void run() {
        initialize();
        driver.get(SETTING_MENU);
        if(isLoginMenu())
        {
            element = driver.findElement(By.xpath(config.getString(prefix+".login_page.username")));
            element.sendKeys(config.getString("administrator.id"));

            element = driver.findElement(By.xpath(config.getString(prefix+".login_page.password")));
            element.sendKeys(config.getString("administrator.password"));
            element = driver.findElement(By.xpath(config.getString(prefix+".login_page.submit_button")));
            element.click();
        }
        if(isMainMenu())
        {
            element = driver.findElement(By.xpath(config.getString(prefix+".main_page.setting_button")));
            element.click();
        }
        if(isSettingMenu())
        {
            driver.switchTo().frame("main_body");
            driver.switchTo().frame("navi_menu_basic");
            element = driver.findElement(By.xpath(config.getString(prefix+".setting_page.settingInternetConnection")));
            element.click();
            driver.switchTo().defaultContent();
            driver.switchTo().frame("main_body");
            driver.switchTo().frame("main");
            element = driver.findElement(By.xpath(config.getString(prefix+".setting_page.macAddressCheckBox")));
            if(element.getAttribute("checked") == null){
                System.out.println("is display??: " +driver.findElement(By.xpath("//*[@id=\"appbtn\"]")).isDisplayed());
                element.click();
                System.out.println("after checkbox clicked - is display??:" +driver.findElement(By.xpath("//*[@id=\"appbtn\"]")).isDisplayed());
            }

            element = driver.findElement(By.xpath(config.getString(prefix+".setting_page.macAddress6")));
            String beforeMacAddress6 = element.getAttribute("value");

            element = driver.findElement(By.xpath(config.getString(prefix+".setting_page.macAddress1")));
            element.sendKeys("88");
            element = driver.findElement(By.xpath(config.getString(prefix+".setting_page.macAddress2")));
            element.sendKeys("36");
            element = driver.findElement(By.xpath(config.getString(prefix+".setting_page.macAddress3")));
            element.sendKeys("6C");
            element = driver.findElement(By.xpath(config.getString(prefix+".setting_page.macAddress4")));
            element.sendKeys("06");
            element = driver.findElement(By.xpath(config.getString(prefix+".setting_page.macAddress5")));
            element.sendKeys("18");
            element = driver.findElement(By.xpath(config.getString(prefix+".setting_page.macAddress6")));
            element.clear();
            element.sendKeys(String.valueOf(getRandomAddress(beforeMacAddress6)));
            element = driver.findElement(By.xpath("//*[@id=\"appbtn\"]"));
            element.click();
            driver.close();
        }
        while(isAttack) // if it's attack, while
        {
            if(AttackListener.getInstance().isAttack("naver.com")&&AttackListener.getInstance().isAttack("google.com"))
            {
                logger.log("INFO", "아이피를 변경중입니다.");
            }
            else{
                logger.log("INFO", "아이피가 변경되었습니다.");
                isAttack=false;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private int getRandomAddress(String currentAddress) {
        Random generator = new Random();
        int newAddress = generator.nextInt(70)+10;
        if(currentAddress.equals(String.valueOf(newAddress)))
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