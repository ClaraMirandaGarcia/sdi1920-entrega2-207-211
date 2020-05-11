package com.uniovi.tests.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PO_ChatView extends PO_NavView {
	public static void createSendMessage(String mensaje, WebDriver driver ) {
		WebElement search = driver.findElement(By.id("newMessage"));
		search.click();
		search.clear();
		search.sendKeys(mensaje);

		WebElement sendButton = driver.findElement(By.id("addMessage"));
		sendButton.click();
		PO_View.checkElement(driver, "text", mensaje);
	}

}