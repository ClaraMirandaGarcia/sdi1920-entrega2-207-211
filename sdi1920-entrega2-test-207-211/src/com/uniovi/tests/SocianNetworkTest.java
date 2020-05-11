package com.uniovi.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

//Paquetes JUnit 
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
//Paquetes Selenium 
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

//Paquetes con los Page Object
import com.uniovi.tests.pageobjects.PO_HomeView;
import com.uniovi.tests.pageobjects.PO_LoginView;
import com.uniovi.tests.pageobjects.PO_PrivateView;
import com.uniovi.tests.pageobjects.PO_Properties;
import com.uniovi.tests.pageobjects.PO_RegisterView;
import com.uniovi.tests.pageobjects.PO_View;
//Paquetes Utilidades de Testing Propias
import com.uniovi.tests.util.SeleniumUtils;

//Ordenamos las pruebas por el nombre del método
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SocianNetworkTest {

	static String PathFirefox65 = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
	static String Geckdriver024 = "C:\\Users\\CMG\\Desktop\\Tercero\\2_Cuatrimestre\\SDI\\Laboratorio\\Material\\PL-SDI-Sesión5-material\\PL-SDI-Sesión5-material\\geckodriver024win64.exe";
	// static String Geckdriver024 =
	// "C:\\Users\\AGM-PC\\Documents\\GitHub\\SDI\\PL-SDI-Sesión5-material\\geckodriver024win64.exe";
	static WebDriver driver = getDriver(PathFirefox65, Geckdriver024);
	static String URL = "https://localhost:8081";

	public static WebDriver getDriver(String PathFirefox, String Geckdriver) {
		System.setProperty("webdriver.firefox.bin", PathFirefox);
		System.setProperty("webdriver.gecko.driver", Geckdriver);
		WebDriver driver = new FirefoxDriver();
		return driver;
	}

	@Before
	public void setUp() {
		driver.navigate().to(URL);
	}

	@After
	public void tearDown() {
		driver.manage().deleteAllCookies();
	}

	@BeforeClass
	static public void begin() {
		// COnfiguramos las pruebas.
		// Fijamos el timeout en cada opción de carga de una vista. 2 segundos.
		PO_View.setTimeout(3);

	}

	@AfterClass
	static public void end() {
		// Cerramos el navegador al finalizar las pruebas
		driver.quit();
	}

	// PR01. Registro de Usuario con datos válidos
	@Test
	public void PR01() {
		PO_RegisterView.checkUserDoesNotExist(driver, "newEmail@whatever.xd");
		PO_RegisterView.register(driver, "newEmail@whatever.xd", "bestname", "suchlastname", "100%secure",
				"100%secure");
		PO_RegisterView.checkUserExist(driver, "newEmail@whatever.xd");
	}

	// PR02. Registro de Usuario con datos inválidos (email vacío, nombre vacío,
	// apellidos vacíos)
	@Test
	public void PR02() {
		// email vacio
		PO_RegisterView.checkInvalidRegister(driver, "", "bestname2", "suchlastname", "100%secure", "100%secure",
				"bestname2");
		// nombre vacio
		PO_RegisterView.checkInvalidRegister(driver, "newEmail2@whatever.xd", "", "suchlastname", "100%secure",
				"100%secure", "newEmail2@whatever.xd");
		// apellidos vacios
		PO_RegisterView.checkInvalidRegister(driver, "newEmail3@whatever.xd", "bestname3", "", "100%secure",
				"100%secure", "bestname3");
	}

	// PR03. Registro de Usuario con datos inválidos (repetición de contraseña
	// inválida).

	@Test
	public void PR03() {
		PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
		PO_RegisterView.fillForm(driver, "newEmail03@whatever.xd", "bestname03", "suchlastname03", "0", "0");
		PO_RegisterView.checkKey(driver, "Error.signup.password.length", PO_Properties.getSPANISH());
		PO_RegisterView.checkUserDoesNotExist(driver, "newEmail03@whatever.xd");
	}

	// Registro de Usuario con datos inválidos (email existente).
	@Test
	public void PR04() {
		PO_RegisterView.checkUserExist(driver, "newEmail@whatever.xd");
		PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
		// email vacio
		PO_RegisterView.fillForm(driver, "newEmail@whatever.xd", "emailcreated", "in", "pr01", "pr01");
		PO_RegisterView.checkKey(driver, "Error.signup.email.duplicate", PO_Properties.getSPANISH());
	}

	// PR05. Inicio de sesión con datos válidos (usuario estándar)
	@Test
	public void PR05() {
		PO_HomeView.loginForm(driver, "class", "btn btn-primary", "login", "prueba1@prueba1.com", "123");
	}

	// PR06. Inicio de sesión con datos inválidos (usuario estándar, campo email y
	// contraseña vacíos).
	@Test
	public void PR06() {
		PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
		// Rellenamos el formulario
		PO_LoginView.fillForm(driver, "", "");

		SeleniumUtils.textoNoPresentePagina(driver, "Your username and password is invalid.");
		// El usuario no se loguea.
		SeleniumUtils.textoNoPresentePagina(driver, "Usuarios");
	}

	// PR07. Inicio de sesión con datos inválidos (usuario estándar, email
	// existente, pero contraseña incorrecta).
	@Test
	public void PR07() {
		PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
		// Rellenamos el formulario
		PO_LoginView.fillForm(driver, "prueba1@prueba1.com", "wrongpass");
		// Comprobamos que se notifica que la contraseña es incorrecta
		PO_View.checkElement(driver, "text", "Email o password incorrecto");
	}

	// PR08. Inicio de sesión con datos inválidos (usuario estándar, email no
	// existente y contraseña no vacía).
	@Test
	public void PR08() {
		PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
		// Rellenamos el formulario
		PO_LoginView.fillForm(driver, "pruebaWrong@prueba1.com", "random");
		// Comprobamos que se notifica que la contraseña es incorrecta
		PO_View.checkElement(driver, "text", "Email o password incorrecto");
	}

	// PR09. Hacer click en la opción de salir de sesión y comprobar que se redirige
	// a la página de inicio de sesión (Login).
	@Test
	public void PR09() {
		// Vamos al formulario de logueo.
		PO_HomeView.loginForm(driver, "class", "btn btn-primary", "login", "prueba1@prueba1.com", "123");

		// Ahora nos desconectamos y comprobamos que se redirige a la página de inicio
		// de sesión.
		PO_HomeView.clickOption(driver, "logout", "class", "btn btn-primary");

		PO_View.checkElement(driver, "text", "Identifícate");
	}

	// PR10. Comprobar que el botón cerrar sesión no está visible si el usuario no
	// está autenticado.
	@Test
	public void PR10() {

		SeleniumUtils.textoNoPresentePagina(driver, "Desconectar");

		PO_HomeView.loginForm(driver, "class", "btn btn-primary", "login", "prueba1@prueba1.com", "123");
		PO_View.checkElement(driver, "text", "Desconectar");

		PO_HomeView.clickOption(driver, "logout", "class", "btn btn-primary");

		PO_View.checkElement(driver, "text", "Identifícate");
		SeleniumUtils.textoNoPresentePagina(driver, "Desconectar");

	}

	// PR11. Sin hacer /
	@Test
	public void PR11() {
		PO_HomeView.loginForm(driver, "class", "btn btn-primary", "login", "prueba1@prueba1.com", "123");
		PO_View.checkElement(driver, "id", "tableUsers");

		assertEquals(4, PO_PrivateView.countInPagination(driver, "tableUsers"));

		// logout
		PO_HomeView.clickOption(driver, "logout", "class", "btn btn-primary");

	}

	// [Prueba12] Hacer una búsqueda con el campo vacío y comprobar que se muestra
	// la página que corresponde con el listado usuarios existentes en el sistema.

	@Test
	public void PR12() {
		assertEquals(4, PO_RegisterView.checkSearchForm(driver, ""));
		// logout
		PO_HomeView.clickOption(driver, "logout", "class", "btn btn-primary");
	}

	// [Prueba13] Hacer una búsqueda escribiendo en el campo un texto que no exista
	// y comprobar que se muestra la página que corresponde, con la lista de
	// usuarios vacía
	@Test
	public void PR13() {
		assertEquals(0, PO_RegisterView.checkSearchForm(driver, "ñ"));
	}

	// [Prueba14] Hacer una búsqueda con un texto específico y comprobar que se
	// muestra la página que corresponde, con la lista de usuarios en los que el
	// texto especificados sea parte de su nombre, apellidos o de su email.
	@Test
	public void PR14() {
		assertEquals(1, PO_RegisterView.checkSearchForm(driver, "prueba1@prueba1.com"));
		// logout
		PO_HomeView.clickOption(driver, "logout", "class", "btn btn-primary");
	}

	// [Prueba15] Desde el listado de usuarios de la aplicación, enviar una
	// invitación de amistad a un usuario. Comprobar que la solicitud de amistad
	// aparece en el listado de invitaciones (punto siguiente).
	@Test
	public void PR15() {

		// log as prueba1
		PO_HomeView.loginForm(driver, "class", "btn btn-primary", "login", "prueba1@prueba1.com", "123");
		PO_View.checkElement(driver, "id", "tableUsers");

		// send invitation to prueba2
		PO_View.checkElement(driver, "id", "tableUsers");

		List<WebElement> elementos = PO_HomeView.checkElement(driver, "free",
				"//td[contains(text(), 'prueba2')]/following-sibling::*/a[contains(@href, 'invitation/send/')]");
		elementos.get(0).click();

		// wait for message success
		PO_View.checkElement(driver, "text", "Invitacion enviada");
		// log out
		PO_HomeView.clickOption(driver, "logout", "class", "btn btn-primary");

		// log in with other user
		// invitations
		// look
		// assertTrue("PR15 sin hacer", false);
	}

	// [Prueba16] Desde el listado de usuarios de la aplicación, enviar una
	// invitación de amistad a un usuario al que ya le habíamos enviado la
	// invitación previamente. No debería dejarnos enviar la invitación, se podría
	// ocultar el botón de enviar invitación o notificar que ya había sido enviada
	// previamente.
	@Test
	public void PR16() {
		// login as prueba1
		PO_HomeView.loginForm(driver, "class", "btn btn-primary", "login", "prueba1@prueba1.com", "123");
		PO_View.checkElement(driver, "id", "tableUsers");

		// send invitation to prueba2
		PO_View.checkElement(driver, "id", "tableUsers");

		List<WebElement> elementos = PO_HomeView.checkElement(driver, "free",
				"//td[contains(text(), 'prueba2')]/following-sibling::*/a[contains(@href, 'invitation/send/')]");
		elementos.get(0).click();

		// wait for message error
		PO_View.checkElement(driver, "text", "Ya existe una invitacion");
		// log out
		PO_HomeView.clickOption(driver, "logout", "class", "btn btn-primary");
	}

	// [Prueba17] Mostrar el listado de invitaciones de amistad recibidas. Comprobar
	// con un listado que contenga varias invitaciones recibidas.

	@Test
	public void PR17() {
		// prueba2 ya ha recibido una petición -> enviamos otra

		// log as prueba3
		PO_HomeView.loginForm(driver, "class", "btn btn-primary", "login", "prueba3@prueba3.com", "prueba3");
		PO_View.checkElement(driver, "id", "tableUsers");

		// send invitation to prueba2
		PO_View.checkElement(driver, "id", "tableUsers");

		List<WebElement> elementos = PO_HomeView.checkElement(driver, "free",
				"//td[contains(text(), 'prueba2')]/following-sibling::*/a[contains(@href, 'invitation/send/')]");
		elementos.get(0).click();

		// wait for message success
		PO_View.checkElement(driver, "text", "Invitacion enviada");
		// log out
		PO_HomeView.clickOption(driver, "logout", "class", "btn btn-primary");

		// Con prueba2 comprobamos que tengamos dos invitaciones.
		PO_HomeView.loginForm(driver, "class", "btn btn-primary", "login", "prueba2@prueba2.com", "prueba2");

		PO_View.checkElement(driver, "id", "tableUsers");
		PO_HomeView.clickOption(driver, "invitations");
		PO_View.checkElement(driver, "text", "Invitaciones de amistad");

		// check 2 invitations

		assertEquals(2, PO_PrivateView.countInPagination(driver, "tableInvitations"));

		// log out
		PO_HomeView.clickOption(driver, "logout", "class", "btn btn-primary");

	}

	// [Prueba18] Sobre el listado de invitaciones recibidas. Hacer click en el
	// botón/enlace de una de ellas y comprobar que dicha solicitud desaparece del
	// listado de invitaciones.

	@Test
	public void PR18() {
		// Accedemos con prueba2 -> tiene dos invitaciones

		PO_HomeView.loginForm(driver, "class", "btn btn-primary", "login", "prueba2@prueba2.com", "prueba2");
		PO_View.checkElement(driver, "id", "tableUsers");
		PO_HomeView.clickOption(driver, "invitations");
		PO_View.checkElement(driver, "text", "Invitaciones de amistad");

		// 2 invitaciones
		assertEquals(2, PO_PrivateView.countInPagination(driver, "tableInvitations"));

		// aceptamos la de prueba3 -> Aceptar

		List<WebElement> elementos = PO_HomeView.checkElement(driver, "free",
				"//td[contains(text(), 'prueba3')]/following-sibling::*/a[contains(@href, 'invitation/accept/')]");
		elementos.get(0).click();

		// comprobamos que ha salido el mensaje Se ha añadido un amigo
		PO_View.checkElement(driver, "text", "Se ha añadido un amigo");

		// VOLVER A INVITATIONS
		PO_HomeView.clickOption(driver, "invitations");
		PO_View.checkElement(driver, "text", "Invitaciones de amistad");

		// 1 invitaciones
		assertEquals(1, PO_PrivateView.countInPagination(driver, "tableInvitations"));

		// log out
		PO_HomeView.clickOption(driver, "logout", "class", "btn btn-primary");

	}

	// [Prueba19] Mostrar el listado de amigos de un usuario. Comprobar que el
	// listado contiene los amigos que deben ser.
	@Test
	public void PR19() {

		// usuario prueba2 tiene 1 amigo

		PO_HomeView.loginForm(driver, "class", "btn btn-primary", "login", "prueba2@prueba2.com", "prueba2");
		PO_View.checkElement(driver, "id", "tableUsers");
		PO_HomeView.clickOption(driver, "friendships");
		PO_View.checkElement(driver, "text", "Amigos");

		// contar 1 elemento en tableFriendships
		assertEquals(1, PO_PrivateView.countInPagination(driver, "tableFriendships"));
		// log out
		PO_HomeView.clickOption(driver, "logout", "class", "btn btn-primary");

		// DELETE FROM DATABASE friendships && invitations

	}

	// P20. Sin hacer /
	@Test
	public void PR20() {
		assertTrue("PR20 sin hacer", false);
	}

	// PR21. Sin hacer /
	@Test
	public void PR21() {
		assertTrue("PR21 sin hacer", false);
	}

	// PR22. Sin hacer /
	@Test
	public void PR22() {
		assertTrue("PR22 sin hacer", false);
	}

	// [Prueba23] Inicio de sesión con datos válidos.
	@Test
	public void PR23() {
		PO_HomeView.clickOption(driver, "cliente.html");
		PO_View.checkElement(driver, "id", "widget-login");
		PO_LoginView.fillFormApi(driver, "prueba2@prueba2.com", "prueba2");
		PO_View.checkElement(driver, "text", "Tus amigos");
	}

	// [Prueba24] Inicio de sesión con datos inválidos (usuario no existente en la
	// aplicación).
	@Test
	public void PR24() {
		PO_HomeView.clickOption(driver, "cliente.html");
		PO_View.checkElement(driver, "id", "widget-login");
		PO_LoginView.fillFormApi(driver, "emailInvalido", "passwordInvalido");
		PO_View.checkElement(driver, "text", "No se ha encontrado a ese usuario");
	}

	// [Prueba25] Acceder a la lista de amigos de un usuario, que al menos tenga
	// tres amigos
	@Test
	public void PR25() {
		PO_HomeView.clickOption(driver, "cliente.html");
		PO_View.checkElement(driver, "id", "widget-login");
		PO_LoginView.fillFormApi(driver, "tengo3amigos@gmail.com", "tengo3amigos");
		PO_View.checkElement(driver, "text", "Tus amigos");

		// Check 3 friends
		PO_View.checkElement(driver, "id", "tableFriendship");
		SeleniumUtils.esperarSegundos(driver, 5);

		assertEquals(3, PO_PrivateView.countInPagination(driver, "tableFriendship"));

	}

//	[Prueba26] Acceder a la lista de amigos de un usuario, 
//	y realizar un filtrado para encontrar a un amigo concreto, 
//	el nombre a buscar debe coincidir con el de un amigo
	
	@Test
	public void PR26() {
		PO_HomeView.clickOption(driver, "cliente.html");
		PO_View.checkElement(driver, "id", "widget-login");
		PO_LoginView.fillFormApi(driver, "tengo3amigos@gmail.com", "tengo3amigos");
		PO_View.checkElement(driver, "text", "Tus amigos");
		SeleniumUtils.esperarSegundos(driver, 5);
		
		//search for prueba2
		//name-filter
		//refreshButton
		WebElement search = driver.findElement(By.id("name-filter"));
		search.click();
		search.clear();
		search.sendKeys("prueba2");
		
		
		SeleniumUtils.esperarSegundos(driver, 5);
		assertEquals(1, PO_PrivateView.countInPagination(driver, "tableFriendship"));
	}

	// PR27. Sin hacer /
	@Test
	public void PR27() {
		assertTrue("PR27 sin hacer", false);
	}

	// PR029. Sin hacer /
	@Test
	public void PR29() {
		assertTrue("PR29 sin hacer", false);
	}

	// PR030. Sin hacer /
	@Test
	public void PR30() {
		assertTrue("PR30 sin hacer", false);
	}

	// PR031. Sin hacer /
	@Test
	public void PR31() {
		assertTrue("PR31 sin hacer", false);
	}

}
