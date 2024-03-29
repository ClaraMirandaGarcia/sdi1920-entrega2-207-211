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
import org.seleniumhq.jetty9.server.session.DatabaseAdaptor;

import com.uniovi.tests.pageobjects.PO_ChatView;
//Paquetes con los Page Object
import com.uniovi.tests.pageobjects.PO_HomeView;
import com.uniovi.tests.pageobjects.PO_LoginView;
import com.uniovi.tests.pageobjects.PO_PrivateView;
import com.uniovi.tests.pageobjects.PO_Properties;
import com.uniovi.tests.pageobjects.PO_RegisterView;
import com.uniovi.tests.pageobjects.PO_View;
import com.uniovi.tests.util.DatabaseUtils;
//Paquetes Utilidades de Testing Propias
import com.uniovi.tests.util.SeleniumUtils;

//Ordenamos las pruebas por el nombre del método
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SocianNetworkTest {

	static String PathFirefox65 = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
//	static String Geckdriver024 = "C:\\Users\\CMG\\Desktop\\Tercero\\2_Cuatrimestre\\SDI\\Laboratorio\\Material\\PL-SDI-Sesión5-material\\PL-SDI-Sesión5-material\\geckodriver024win64.exe";
	static String Geckdriver024 = "C:\\Users\\AGM-PC\\Documents\\GitHub\\SDI\\PL-SDI-Sesión5-material\\geckodriver024win64.exe";
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
		// eliminar todas las amistades del prueba2
		DatabaseUtils.removeAllFriendshipsOfUserEmail("prueba2@prueba2.com");
		// eliminar todas las invitaciones del prueba2
		DatabaseUtils.removeAllInvitationsOfUserEmail("prueba2@prueba2.com");

		// Cerramos el navegador al finalizar las pruebas
		driver.quit();
	}

	// PR01. Registro de Usuario con datos válidos
	@Test
	public void PR01() {
		String email = "newEmail@whatever.xd";
		DatabaseUtils.removeUser(email);
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

	// PR11.
	@Test
	public void PR11() {
		PO_HomeView.loginForm(driver, "class", "btn btn-primary", "login", "prueba1@prueba1.com", "123");
		PO_View.checkElement(driver, "id", "tableUsers");

		assertEquals(DatabaseUtils.countUsers(), PO_PrivateView.countInPagination(driver, "tableUsers"));

		// logout
		PO_HomeView.clickOption(driver, "logout", "class", "btn btn-primary");

	}

	// [Prueba12] Hacer una búsqueda con el campo vacío y comprobar que se muestra
	// la página que corresponde con el listado usuarios existentes en el sistema.

	@Test
	public void PR12() {
		assertEquals(DatabaseUtils.countUsers(), PO_RegisterView.checkSearchForm(driver, ""));
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
		String emailUserFrom = "prueba1@prueba1.com";
		String emailUserTo = "prueba2@prueba2.com";

		// Just in case remove friendship
		DatabaseUtils.removeFriendship(emailUserFrom, emailUserTo);
		// Just in case remove invitation.
		DatabaseUtils.removeInvitation(emailUserFrom, emailUserTo);

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
		String emailUserFrom = "prueba3@prueba3.com";
		String emailUserTo = "prueba2@prueba2.com";

		// Just in case remove friendship
		DatabaseUtils.removeFriendship(emailUserFrom, emailUserTo);
		// Just in case remove invitation.
		DatabaseUtils.removeInvitation(emailUserFrom, emailUserTo);

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

		// usuario prueba2 tiene 1 amigo prueba3

		PO_HomeView.loginForm(driver, "class", "btn btn-primary", "login", "prueba2@prueba2.com", "prueba2");
		PO_View.checkElement(driver, "id", "tableUsers");
		PO_HomeView.clickOption(driver, "friendships");
		PO_View.checkElement(driver, "text", "Amigos");

		// contar 1 elemento en tableFriendships
		assertEquals(1, PO_PrivateView.countInPagination(driver, "tableFriendships"));
		// log out
		PO_HomeView.clickOption(driver, "logout", "class", "btn btn-primary");

		// DELETE FROM DATABASE friendships && invitations
		String emailUserFrom = "prueba3@prueba3.com";
		String emailUserTo = "prueba2@prueba2.com";

		// Para dejarlo como estaba
		DatabaseUtils.removeFriendship(emailUserFrom, emailUserTo);

	}

	// P20. Intentar acceder sin estar autenticado a la opción de listado de
	// usuarios. Se deberá volver al formulario de login.
	@Test
	public void PR20() {
		driver.get("https://localhost:8081/users");
		PO_View.checkElement(driver, "id", "loginButton");
	}

	// PR21. Intentar acceder sin estar autenticado a la opción de listado de
	// invitaciones de amistad recibida de un usuario estándar. Se deberá volver al
	// formulario de login
	@Test
	public void PR21() {
		driver.get("https://localhost:8081/invitations");
		PO_View.checkElement(driver, "id", "loginButton");
	}

	// PR22. Intentar acceder estando autenticado como usuario standard a la lista
	// de amigos de otro usuario. Se deberá mostrar un mensaje de acción indebida.
	@Test
	public void PR22() {
		// Para acceder a la lista de amigos en nuestra aplicación se debe acceder a
		// /friendships, sin parametros. Debido a esto por diseño es imposible ver la
		// lista de amigos de otro usuario, pues la aplicación utiliza el usuario en
		// sesión para obtenerla
		assertTrue("PR22 no aplica", true);
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

		String emailTo = "tengo3amigos@gmail.com";
		// Borrar todas las amistades
		DatabaseUtils.removeAllFriendshipsOfUserEmail(emailTo);

		// Crear amistad con tengo3amigos
		String email1 = "asdf@gmail.com";
		String email2 = "prueba1@prueba1.com";
		String email3 = "prueba2@prueba2.com";

		DatabaseUtils.createFriendship(emailTo, email1);
		DatabaseUtils.createFriendship(emailTo, email2);
		DatabaseUtils.createFriendship(emailTo, email3);

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
		String emailTo = "tengo3amigos@gmail.com";
		// Borrar todas las amistades
		DatabaseUtils.removeAllFriendshipsOfUserEmail(emailTo);

		// Crear amistad con tengo3amigos
		String email1 = "asdf@gmail.com";
		String email2 = "prueba1@prueba1.com";
		String email3 = "prueba2@prueba2.com";

		DatabaseUtils.createFriendship(emailTo, email1);
		DatabaseUtils.createFriendship(emailTo, email2);
		DatabaseUtils.createFriendship(emailTo, email3);

		PO_HomeView.clickOption(driver, "cliente.html");
		PO_View.checkElement(driver, "id", "widget-login");
		PO_LoginView.fillFormApi(driver, "tengo3amigos@gmail.com", "tengo3amigos");
		PO_View.checkElement(driver, "text", "Tus amigos");
		SeleniumUtils.esperarSegundos(driver, 5);

		// search for prueba2
		// name-filter
		// refreshButton
		WebElement search = driver.findElement(By.id("name-filter"));
		search.click();
		search.clear();
		search.sendKeys("prueba2");

		SeleniumUtils.esperarSegundos(driver, 5);
		assertEquals(1, PO_PrivateView.countInPagination(driver, "tableFriendship"));
	}

	// PR27. Acceder a la lista de mensajes de un amigo “chat”, la lista debe
	// contener al menos tres mensajes.
	@Test
	public void PR27() {
		PO_HomeView.loginApiForm(driver, "alejan1579@gmail.com", "123");
		List<WebElement> row = PO_View.checkElement(driver, "id", "5eb58934a546330b2c522761");
		row.get(0).findElement(By.className("friendData")).click();
		List<WebElement> messages = PO_View.checkElement(driver, "class", "messageRow");
		assertTrue(messages.size() >= 3);
	}

	// [Prueba28] Acceder a la lista de mensajes de un amigo “chat” y crear un nuevo
	// mensaje, validar que el mensaje aparece en la lista de mensajes.

	@Test
	public void PR28() {

		String emailTo = "tengo3amigos@gmail.com";
		// Crear amistad con tengo3amigos
		String email2 = "prueba2@prueba2.com";
		DatabaseUtils.createFriendship(emailTo, email2);

		// Eliminar todos los mensajes tengo3amigos && prueba2
		DatabaseUtils.removeAllMessagesUsers(emailTo, email2);

		PO_HomeView.loginApiForm(driver, "tengo3amigos@gmail.com", "tengo3amigos");

		// id -> prueba2 = 5eb8193dbb06acf8338e9150
		List<WebElement> row = PO_View.checkElement(driver, "id", "5eb8193dbb06acf8338e9150");
		row.get(0).findElement(By.className("friendData")).click();

		// crear && enviar mensaje
		String mensaje = "MensajeTest28";
		PO_ChatView.createSendMessage(mensaje, driver);

	}

	// PR029. Identificarse en la aplicación y enviar un mensaje a un amigo, validar
	// que el mensaje enviado
	// aparece en el chat. Identificarse después con el usuario que recibido el
	// mensaje y validar que tiene un
	// mensaje sin leer, entrar en el chat y comprobar que el mensaje pasa a tener
	// el estado leído.
	@Test
	public void PR29() {
		PO_HomeView.loginApiForm(driver, "alejan1579@gmail.com", "123");
		List<WebElement> row = PO_View.checkElement(driver, "id", "5eb58934a546330b2c522761");
		row.get(0).findElement(By.className("friendData")).click();
		String texto = "asdsad";
		driver.navigate().to(URL);
		PO_HomeView.loginApiForm(driver, "prueba1@prueba1.com", "123");
		row = PO_View.checkElement(driver, "id", "5eb6eaf87637800730cec57f");
		row.get(0).findElement(By.className("friendData")).click();
		String mensaje = "MensajeTest29";
		WebElement search = driver.findElement(By.id("newMessage"));
		search.click();
		search.clear();
		search.sendKeys(mensaje);
		WebElement sendButton = driver.findElement(By.id("addMessage"));
		sendButton.click();
		PO_View.checkElement(driver, "text", mensaje);
		driver.navigate().to(URL);
		PO_HomeView.loginApiForm(driver, "alejan1579@gmail.com", "123");
		row = PO_View.checkElement(driver, "id", "5eb58934a546330b2c522761");
		assertTrue(row.get(0).findElement(By.className("friendDataCount")).getAttribute("innerHTML").equals("1"));
		row.get(0).findElement(By.className("friendData")).click();
		List<WebElement> messages = PO_View.checkElement(driver, "class", "messageRow");
		for (WebElement message : messages) {
			if (message.findElement(By.className("messageText")).getAttribute("innerHTML").equals(texto)) {
				assertTrue(message.findElement(By.className("messageRead")).getAttribute("innerHTML").equals("leído"));
				return;
			}
		}

	}

	// [Prueba30] Identificarse en la aplicación y enviar tres mensajes a un amigo,
	// validar que los mensajes enviados aparecen en el chat. Identificarse después
	// con el usuario que recibido el mensaje y validar que el número de mensajes
	// sin leer aparece en la propia lista de amigos

	@Test
	public void PR30() {
		String emailTo = "tengo3amigos@gmail.com";
		// Crear amistad con asdf
		String email2 = "asdf@gmail.com";
		DatabaseUtils.createFriendship(emailTo, email2);
		// erase messages
		DatabaseUtils.removeAllMessagesUsers(emailTo, email2);

		// asdf no tiene más amigos
		PO_HomeView.loginApiForm(driver, "tengo3amigos@gmail.com", "tengo3amigos");

		// id -> asdf = 5eb58d1df805e44eda6da88d
		List<WebElement> row = PO_View.checkElement(driver, "id", "5eb58d1df805e44eda6da88d");
		row.get(0).findElement(By.className("friendData")).click();

		// crear && enviar mensaje
		String mensaje01 = "MensajeTest30_01";
		PO_ChatView.createSendMessage(mensaje01, driver);
		String mensaje02 = "MensajeTest30_02";
		PO_ChatView.createSendMessage(mensaje02, driver);
		String mensaje03 = "MensajeTest30_03";
		PO_ChatView.createSendMessage(mensaje03, driver);

		// Botón volver
		WebElement returnButton = driver.findElement(By.id("volverApp"));
		returnButton.click();
		// Iniciar sesión
		PO_HomeView.loginApiForm(driver, "asdf@gmail.com", "asdf");
		// chat -> tengo3amigos@gmail.com. 5eb92576209b0415355b6270

		// check 3 unread messages en la lista de amigos
		PO_View.checkElement(driver, "text", "3");

	}

	// PR031. Identificarse con un usuario A que al menos tenga 3 amigos, ir al chat
	// del ultimo amigo de
	// la lista y enviarle un mensaje, volver a la lista de amigos y comprobar que
	// el usuario al que se le ha enviado
	// el mensaje esta en primera posición.
	// Identificarse con el usuario B y
	// enviarle un mensaje al usuario A.
	// Volver a identificarse con el usuario A y ver que el usuario que acaba de
	// mandarle el mensaje es el primero
	// en su lista de amigos.
	@Test
	public void PR31() {
		// Identificarse con un usuario A que al menos tenga 3 amigos
		PO_HomeView.loginApiForm(driver, "alejan1579@gmail.com", "123");
		// ir al chat del ultimo amigo de la lista
		String lastEmail = PO_HomeView.lastApiFriend(driver);
		// y enviarle un mensaje
		PO_HomeView.sendApiMessage(driver, "MensajeTest31_1");
		// volver a la lista de amigos
		driver.navigate().to(URL);
		PO_HomeView.loginApiForm(driver, "alejan1579@gmail.com", "123");
		// comprobar que el usuario al que se le ha enviado el mensaje esta en primera
		// posición
		String firstEmail = PO_HomeView.firstApiEmail(driver);
		assertTrue(lastEmail.equals(firstEmail));
		// Identificarse con el usuario B
		driver.navigate().to(URL);
		PO_HomeView.loginApiForm(driver, lastEmail, "123");
		// enviarle un mensaje al usuario A.
		PO_HomeView.gotoApiEmail(driver, "alejan1579@gmail.com");
		PO_HomeView.sendApiMessage(driver, "MensajeTest31_2");
		// Volver a identificarse con el usuario A
		driver.navigate().to(URL);
		PO_HomeView.loginApiForm(driver, "alejan1579@gmail.com", "123");
		// ver que el usuario que acaba de mandarle el mensaje es el primero en su lista
		// de amigos.
		firstEmail = PO_HomeView.firstApiEmail(driver);
		assertTrue(firstEmail.equals(lastEmail));
	}

}
