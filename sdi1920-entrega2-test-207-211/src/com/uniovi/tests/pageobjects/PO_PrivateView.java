package com.uniovi.tests.pageobjects;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.sun.corba.se.impl.orbutil.threadpool.TimeoutException;
import com.uniovi.tests.util.SeleniumUtils;

public class PO_PrivateView extends PO_NavView {
	static public void fillFormAddMark(WebDriver driver, int userOrder, String descriptionp, String scorep) {
		// Espero por que se cargue el formulario de asñadir nota (Concretamente el
		// botón class="btn")
		PO_View.checkElement(driver, "class", "btn");
		// Seleccionamos el alumnos userOrder
		new Select(driver.findElement(By.id("user"))).selectByIndex(userOrder);
		// Rellenemos el campo de descripción
		WebElement description = driver.findElement(By.name("description"));
		description.clear();
		description.sendKeys(descriptionp);
		WebElement score = driver.findElement(By.name("score"));
		score.click();
		score.clear();
		score.sendKeys(scorep);
		By boton = By.className("btn");
		driver.findElement(boton).click();
	}

	public static void clickMenuOption(final WebDriver driver, final String typeMenu, final String textMenu,
			final String typeOption, final String textOption) {
		PO_View.checkElement(driver, typeMenu, textMenu).get(0).click();
		PO_View.checkElement(driver, typeOption, textOption).get(0).click();
	}

	public static int countInPagination(WebDriver driver, String table) {
		int total = 0;
		boolean whileNext = true;
		int i = 2;
		
		do {

			List<WebElement> users = driver.findElements(By.xpath("//*[@id=\""+table+"\"]/tbody/tr"));
			total += users.size();
			
			List<WebElement> elementos = driver.findElements(By.xpath("//*[@id=\"pi-"+i+"\"]/a"));
			if(elementos.size() == 1) {
				i++;
				elementos.get(0).click();
			} else {
				whileNext = false;
			}

		} while (whileNext);
		
		return total;
	}

}