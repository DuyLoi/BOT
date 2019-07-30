package com.tpb.bot.citad.job;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.tpb.bot.citad.config.Config;
import com.tpb.bot.citad.ui.BotUI;
import com.tpb.bot.citad.util.FileUtil;
import com.tpb.bot.citad.util.LogFile;
import com.tpb.bot.citad.util.Util;

public class CitadBotJob extends TimerTask {
	
	private static final Logger logger = Logger.getLogger(CitadBotJob.class);
	private static String printLogOut = "";
	private static WebDriver driver = null;
	private static int iTotalNumberOfColumns = 0;
	private static String amount;
	private static String txtstatus;
	private static String accountingEntry;

	public CitadBotJob() {
		super();	
	}

	public static void closeBrowser(WebDriver driver, int timeSecond){
		try {
			Thread.sleep(timeSecond);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		driver.quit();
	}

	public static void displayAndWriteLog(String log) {
		System.out.println(log);
//		PropertyConfigurator.configure("src/log4j.properties");
		PropertyConfigurator.configure("log4j.properties");
        logger.info(log);
        String DATE_FORMATTER= "dd-MM-yyyy HH:mm:ss .SSS";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        printLogOut = (LocalDateTime.now().format(formatter) +": \t"+ log + "\n");
        FileUtil.loadMonitor(printLogOut);
 }
	
	public static void displayAndWriteLogError(Exception e) {
        logger.error(e.getMessage(),e);
        String DATE_FORMATTER= "dd-MM-yyyy HH:mm:ss .SSS";
        String errorLog[] =  e.getMessage().split("\\\n");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        printLogOut = (LocalDateTime.now().format(formatter) +": \t"+ errorLog[0] + "\n");
        FileUtil.loadMonitor(printLogOut);
 }
	
	public static WebElement getElementId(WebDriver driver, String id){
		WebDriverWait wait = new WebDriverWait(driver, 100);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
		WebElement element = driver.findElement(By.id(id));
		if (element==null) {
			element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
			wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id(id)));
			element = new WebDriverWait(driver, 10).ignoring(StaleElementReferenceException.class).until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
			wait.until(ExpectedConditions.stalenessOf(element));
		}		
		return element;
	}
	
	public static WebElement getElementXpath(WebDriver driver, String xpath){
		WebDriverWait wait = new WebDriverWait(driver, 100);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
		WebElement element = driver.findElement(By.xpath(xpath));
		if (element==null) {
			element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(xpath)));
			element = new WebDriverWait(driver, 10).ignoring(StaleElementReferenceException.class).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
			wait.until(ExpectedConditions.stalenessOf(element));
		}		
		return element;
	}
	
	public static boolean isCheckAmountStatus(String _amount, String _status){
		String status = BotUI.statusExchange;
		int _iAmount = BotUI.amountExchange;
		String txtAmount = _amount;
		String []txtAmount1 = txtAmount.split("\\.");
		String txtAmount2 = "";
		for(String s2: txtAmount1){
			txtAmount2 +=s2;
		}
		int intAmount = Integer.parseInt(txtAmount2);
		if(intAmount<=_iAmount && _status.equals(status)){
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public static void doLogin() {
		try {

			JSONObject flowLogin = Util.getFlowById("LOGIN");
			String url = (String) flowLogin.get("mainUrl");
			JSONObject stepLogin = Util.getStep(flowLogin, "1");
			JSONArray arr = Util.getStepElements(stepLogin);
			
			File file = new File(Config.getParam("bot.ciatad.IEdriver"));
			System.setProperty("webdriver.ie.driver", file.getAbsolutePath());
//			
			DesiredCapabilities caps = DesiredCapabilities.internetExplorer();
			caps.setCapability(InternetExplorerDriver.INITIAL_BROWSER_URL, url);
			caps.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
//
			driver = new InternetExplorerDriver(caps);
			displayAndWriteLog("Mở trình duyệt");
			driver.manage().window().maximize();
			driver.manage().timeouts().pageLoadTimeout(50, TimeUnit.SECONDS);
			driver.manage().timeouts().implicitlyWait(50, TimeUnit.SECONDS);
			driver.manage().timeouts().setScriptTimeout(50, TimeUnit.SECONDS);

			driver.get(url);
			new WebDriverWait(driver, 30);
			displayAndWriteLog("Mở trang:"+url);
			
			JSONObject jsonObject = null;
			String id = null;
			String value = null;
			String findBy = null;
			String action = null;
			String waitElement = "";
			WebElement webElement = null;

			for (int i = 1; i <= arr.size(); i++) {
				jsonObject = com.tpb.bot.citad.util.Util.getStepElementByOrder(stepLogin,String.valueOf(i));
				id = (String) jsonObject.get("id");
				value = (String) jsonObject.get("value");
				findBy = (String) jsonObject.get("findBy");
				action = (String) jsonObject.get("action");
				waitElement = (String) jsonObject.get("waitElement");
				
				if ("ID".equals(findBy)) {
					webElement = driver.findElement(By.id(id));
					
				}
				if ("SENDTEXT".equals(action)) {
					webElement.sendKeys(value);
					new WebDriverWait(driver, 50);
					if(i==1) displayAndWriteLog("Nhập tên đăng nhập");
					if(i==2) displayAndWriteLog("Nhập tên mật khẩu");
				}
				if ("CLICK".equals(action)) {
					webElement.click();
					WebDriverWait wait1 = new WebDriverWait(driver, 30);
					wait1.until(ExpectedConditions.visibilityOfElementLocated(By.id(waitElement)));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			displayAndWriteLogError(e);
			driver.quit();
		}
	}

	public static void doProcess(String flowId, int stepNo){
		try {
			JSONObject flow = Util.getFlowById(flowId);
			int noOfSteps = Integer.parseInt(Util.getValue(flow, "noOfSteps"));
			JSONObject step = null;
			if(stepNo <= 0){
				for (int i = 1; i <= noOfSteps; i++) {
					step = Util.getStep(flow, String.valueOf(i));
					process4Step(step);			
				}
			}else{
				step = Util.getStep(flow, String.valueOf(stepNo));
				process4Step(step);			
			}
		} catch (Exception e) {
			e.printStackTrace();
			displayAndWriteLogError(e);
		}
	}
	
	public static void process4Step(JSONObject step){
		if (step == null)
			return;

		JSONArray arr = Util.getStepElements(step);

		Actions doAction = new Actions(driver);
		WebDriverWait wait = new WebDriverWait(driver, 100);
		
		String id = null;
		String findBy = null;
		String action = null;
		String waitElement = "";
		WebElement webElement = null;

		for (int i = 1; i <= arr.size(); i++) {
			JSONObject jsonObject = Util.getStepElementByOrder(step,String.valueOf(i));
			id = (String) jsonObject.get("id");		
			findBy = (String) jsonObject.get("findBy");
			action = (String) jsonObject.get("action");
			waitElement = (String) jsonObject.get("waitElement");

			switch (findBy) {
			case "ID":
				webElement = getElementId(driver, id);
				break;
			case "XPATH":
				webElement = getElementXpath(driver, id);
				break;
			default:
				break;
			}
			switch (action) {
			case "CLICK":
				webElement.click();
				if ( ! waitElement.equals("")) {
					wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(waitElement)));
				}
				break;
			case "HOVER":
				displayAndWriteLog(webElement.getText());
				doAction.moveToElement(webElement).pause(1000).perform();
				if ( ! waitElement.equals("")) {
					wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(waitElement)));
				}
				break;
			case "GETVALUE":
				if (i==1){
					amount = webElement.getAttribute("value");
					displayAndWriteLog("Số tiền chuyển:"+amount);
				}else if(i==2){
					txtstatus = webElement.getAttribute("value");
					displayAndWriteLog("Tình trạng giao dịch:"+txtstatus);
				}else if(i==3){
					accountingEntry = webElement.getAttribute("value");
					displayAndWriteLog("Số bút toán:"+accountingEntry);
				}
				if ( ! waitElement.equals("")) {
					wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(waitElement)));
				}			
				break;
			case "GETTEXT":
				displayAndWriteLog("Số lượng giao dịch chờ xử lý:"+webElement.getText());
				iTotalNumberOfColumns = Integer.parseInt(webElement.getText());
				if ( ! waitElement.equals("")) {
					wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(waitElement)));
				}
				break;
			case "CLICK_GETTEXT":
				displayAndWriteLog(webElement.getText());
				webElement.click();
				if ( ! waitElement.equals("")) {
					wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(waitElement)));
				}
				break;
			default:
				break;
			}			
		}
	}
	
	@Override
	public void run() {
		String note="";
		try {
			doLogin();
			doProcess("EXCHANGE", 0);
			doProcess("SELECT", 0);

			for (int i = 1; i <= iTotalNumberOfColumns; i++) {
				displayAndWriteLog("Giao dịch số "+i);
				doProcess("DETAIL", 0);
				if (isCheckAmountStatus(amount, txtstatus)) {
					displayAndWriteLog("Thỏa mãn điều kiện");
					doProcess("PERFORM", 1);
					displayAndWriteLog("Ghi");
					note="Ghi";
				}else{
					displayAndWriteLog("Không thỏa mãn điều kiện");
				}
				displayAndWriteLog("Tiếp theo");
				note="Tiếp theo";
				doProcess("PERFORM", 2);
				try {
					Thread.sleep(3000);
					LogFile.writeLogFile(accountingEntry, "Đã thực hiện", note);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
					displayAndWriteLogError(e1);
				}

			}
			closeBrowser(driver, 5000);
		} catch (Exception e) {
			e.printStackTrace();
			displayAndWriteLogError(e);
			closeBrowser(driver, 10000);
		}
	}
}
