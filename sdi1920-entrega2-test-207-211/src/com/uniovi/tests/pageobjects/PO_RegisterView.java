package com.uniovi.tests.pageobjects;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.uniovi.utils.SeleniumUtils;

public class PO_RegisterView extends PO_NavView {

	static public void fillForm(WebDriver driver, String emailp, String namep, String lastnamep, String passwordp,
			String passwordconfp) {
		WebElement email = driver.findElement(By.name("email"));
		email.click();
		email.clear();
		email.sendKeys(emailp);
		WebElement name = driver.findElement(By.name("nombre"));
		name.click();
		name.clear();
		name.sendKeys(namep);
		WebElement lastname = driver.findElement(By.name("apellidos"));
		lastname.click();
		lastname.clear();
		lastname.sendKeys(lastnamep);
		WebElement password = driver.findElement(By.name("password"));
		password.click();
		password.clear();
		password.sendKeys(passwordp);
		WebElement passwordConfirm = driver.findElement(By.name("passwordConfirmation"));
		passwordConfirm.click();
		passwordConfirm.clear();
		passwordConfirm.sendKeys(passwordconfp);
		// Pulsar el boton de Alta.
		By boton = By.className("btn");
		driver.findElement(boton).click();
	}

	public static void register(WebDriver driver, String emailp, String namep, String lastnamep, String passwordp,
			String passwordconfp) {
		PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
		PO_RegisterView.fillForm(driver, emailp, namep, lastnamep, passwordp, passwordconfp);
		PO_View.checkElement(driver, "text", "Nuevo usuario registrado");
	}

	private static boolean checkUser(WebDriver driver, String texto) {
		PO_HomeView.loginForm(driver, "class", "btn btn-primary", "login", "prueba1@prueba1.com", "123");
		int pagenumber = 2;
		do {
			try {
				SeleniumUtils.EsperaCargaPagina(driver, "text", texto, PO_View.getTimeout());
				break;
			} catch (TimeoutException e) {
			}
			try {
				List<WebElement> elementos = PO_View.checkElement(driver, "id", "pi-"+pagenumber);
				assertTrue(elementos.size() == 1);
				elementos.get(0).findElement(By.className("page-link")).click();
				pagenumber++;
			} catch (TimeoutException e) {
				PO_PrivateView.clickOption(driver, "logout", "id", "loginButton");
				return false;
			}
		} while (true);
		PO_PrivateView.clickOption(driver, "logout", "id", "loginButton");
		return true;
	}

	public static void checkUserExist(WebDriver driver, String texto) {
		assertTrue(checkUser(driver, texto));
	}

	public static void checkUserDoesNotExist(WebDriver driver, String texto) {
		assertFalse(checkUser(driver, texto));
	}
	
	 public static void checkInvalidRegister(WebDriver driver, String email, String name, String lastname, String pass,
	            String passconfirm, String textToCheck) {
	        PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
	        PO_RegisterView.fillForm(driver, email, name, lastname, pass, passconfirm);
	        PO_RegisterView.checkUserDoesNotExist(driver, textToCheck);
	    }

	public static Object checkSearchForm(WebDriver driver, String string) {
		//log in
		PO_HomeView.loginForm(driver, "class", "btn btn-primary", "login", "prueba1@prueba1.com", "123");
		PO_View.checkElement(driver, "id", "tableUsers");
		
		//search
		WebElement search = driver.findElement(By.name("busqueda"));
		search.click();
		search.clear();
		search.sendKeys(string);
		
		By boton = By.id("submitUserSearch");
		driver.findElement(boton).click();
		
		//count
		return PO_PrivateView.countInPagination(driver);
	}

}
