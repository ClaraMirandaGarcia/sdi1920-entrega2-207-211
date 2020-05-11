package com.uniovi.tests.pageobjects;

import java.util.List;

import org.openqa.selenium.*;

import com.uniovi.tests.util.SeleniumUtils;

public class PO_HomeView extends PO_NavView {
	
	static public void checkWelcome(WebDriver driver, int language) {
		//Esperamos a que se cargue el saludo de bienvenida en Español
		SeleniumUtils.EsperaCargaPagina(driver, "text", p.getString("welcome.message", language), getTimeout());
	}
	
	static public void checkChangeIdiom(WebDriver driver, String textIdiom1, String textIdiom2, int locale1, int locale2 ) {
			//Esperamos a que se cargue el saludo de bienvenida en Español
			PO_HomeView.checkWelcome(driver, locale1);
			//Cambiamos a segundo idioma
			PO_HomeView.changeIdiom(driver,  textIdiom2);
			//COmprobamos que el texto de bienvenida haya cambiado a segundo idioma
			PO_HomeView.checkWelcome(driver, locale2);
			//Volvemos a Español.
			PO_HomeView.changeIdiom(driver, textIdiom1);
			//Esperamos a que se cargue el saludo de bienvenida en Español
			PO_HomeView.checkWelcome(driver, locale1);
	}
	
	static public void loginForm(WebDriver driver, String classStr,String buttonName, String loginText, String emailp, String passwordp) {
		// Vamos al formulario de logueo.
		PO_HomeView.clickOption(driver, loginText, classStr, buttonName);
		// Rellenamos el formulario
		PO_LoginView.fillForm(driver, emailp, passwordp);
		PO_View.checkElement(driver, "text", emailp);
	}

	public static void loginApiForm(WebDriver driver, String email, String user) {
		PO_HomeView.clickOption(driver, "cliente.html","id","boton-login");
		PO_LoginView.fillFormApi(driver, email, user);
		
	}

	public static String lastApiFriend(WebDriver driver) {
		List<WebElement> rows = PO_View.checkElement(driver, "class", "friendRow");
		WebElement lastFriend = rows.get(rows.size() - 1);
		List<WebElement> datas = lastFriend.findElements(By.className("friendData"));
		String email = datas.get(datas.size() - 1).getAttribute("innerHTML");
		lastFriend.findElement(By.className("friendData")).click();
		return email;
		
	}

	public static void sendApiMessage(WebDriver driver, String mensaje) {
		WebElement search = driver.findElement(By.id("newMessage"));
		search.click();
		search.clear();
		search.sendKeys(mensaje);
		WebElement sendButton = driver.findElement(By.id("addMessage"));
		sendButton.click();
		PO_View.checkElement(driver, "text", mensaje);	
		
	}

	public static String firstApiEmail(WebDriver driver) {
		List<WebElement> rows = PO_View.checkElement(driver, "class", "friendRow");
		WebElement firstFriend = rows.get(0);
		List<WebElement> datasfirst = firstFriend.findElements(By.className("friendData"));
		String emailfirst = datasfirst.get(datasfirst.size() - 1).getAttribute("innerHTML");
		return emailfirst;
	}

	public static void gotoApiEmail(WebDriver driver, String email) {
		List<WebElement> data = PO_View.checkElement(driver, "text", email);
		data.get(0).click();
		
	}


}
