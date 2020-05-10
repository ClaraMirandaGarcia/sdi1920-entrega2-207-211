package com.uniovi.tests;

//Paquetes Java
import java.util.List;
//Paquetes JUnit 
import org.junit.*;
import org.junit.runners.MethodSorters;
import static org.junit.Assert.assertTrue;
//Paquetes Selenium 
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.*;
//Paquetes Utilidades de Testing Propias
import com.uniovi.tests.util.SeleniumUtils;
//Paquetes con los Page Object
import com.uniovi.tests.pageobjects.*;

//Ordenamos las pruebas por el nombre del método
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SocianNetworkTest {

	static String PathFirefox65 = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
	// static String Geckdriver024 =
	// "C:\\Users\\CMG\\Desktop\\Tercero\\2_Cuatrimestre\\SDI\\Laboratorio\\Material\\PL-SDI-Sesión5-material\\PL-SDI-Sesión5-material\\geckodriver024win64.exe";
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

	// PR10. Comprobar que el botón cerrar sesión no está visible si el usuario no está autenticado.
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
		assertTrue("PR11 sin hacer", false);
	}

	// PR12. Sin hacer /
	@Test
	public void PR12() {
		assertTrue("PR12 sin hacer", false);
	}

	// PR13. Sin hacer /
	@Test
	public void PR13() {
		assertTrue("PR13 sin hacer", false);
	}

	// PR14. Sin hacer /
	@Test
	public void PR14() {
		assertTrue("PR14 sin hacer", false);
	}

	// PR15. Sin hacer /
	@Test
	public void PR15() {
		assertTrue("PR15 sin hacer", false);
	}

	// PR16. Sin hacer /
	@Test
	public void PR16() {
		assertTrue("PR16 sin hacer", false);
	}

	// PR017. Sin hacer /
	@Test
	public void PR17() {
		assertTrue("PR17 sin hacer", false);
	}

	// PR18. Sin hacer /
	@Test
	public void PR18() {
		assertTrue("PR18 sin hacer", false);
	}

	// PR19. Sin hacer /
	@Test
	public void PR19() {
		assertTrue("PR19 sin hacer", false);
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

	// PR23. Sin hacer /
	@Test
	public void PR23() {
		assertTrue("PR23 sin hacer", false);
	}

	// PR24. Sin hacer /
	@Test
	public void PR24() {
		assertTrue("PR24 sin hacer", false);
	}

	// PR25. Sin hacer /
	@Test
	public void PR25() {
		assertTrue("PR25 sin hacer", false);
	}

	// PR26. Sin hacer /
	@Test
	public void PR26() {
		assertTrue("PR26 sin hacer", false);
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
