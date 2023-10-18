package com.cst438;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;

/*
 * This example shows how to use selenium testing using the web driver 
 * with Chrome browser.
 * 
 *  - Buttons, input, and anchor elements are located using XPATH expression.
 *  - onClick( ) method is used with buttons and anchor tags.
 *  - Input fields are located and sendKeys( ) method is used to enter test data.
 *  - Spring Boot JPA is used to initialize, verify and reset the database before
 *      and after testing.
 *      
 *  In SpringBootTest environment, the test program may use Spring repositories to 
 *  setup the database for the test and to verify the result.
 */

@SpringBootTest
public class EndToEndTestSubmitGrades {

	public static final String CHROME_DRIVER_FILE_LOCATION = "C:/Users/games/Downloads/chromedriver-win64/chromedriver-win64/chromedriver.exe";

	public static final String URL = "http://localhost:3000";
	public static final int SLEEP_DURATION = 1000; // 1 second.
	public static final String TEST_ASSIGNMENT_NAME = "db design";
	public static final String NEW_GRADE = "99";


	@Test
	public void addCourseTest() throws Exception {



		// set the driver location and start driver
		//@formatter:off
		// browser	property name 				Java Driver Class
		// edge 	webdriver.edge.driver 		EdgeDriver
		// FireFox 	webdriver.firefox.driver 	FirefoxDriver
		// IE 		webdriver.ie.driver 		InternetExplorerDriver
		//@formatter:on
		
		/*
		 * initialize the WebDriver and get the home page. 
		 */

		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		driver.get(URL);
		Thread.sleep(SLEEP_DURATION);
		
		WebElement w;
		

		try {
			/*
			* locate the <td> element for assignment title 'db design'
			* 
			*/
			
			List<WebElement> elements  = driver.findElements(By.xpath("//td"));
			boolean found = false;
			for (WebElement we : elements) {
				if (we.getText().equals(TEST_ASSIGNMENT_NAME)) {
					found=true;
					we.findElement(By.xpath("..//a")).click();
					break;
				}
			}
			assertThat(found).withFailMessage("The test assignment was not found.").isTrue();

			/*
			 *  Locate and click Grade button to indicate to grade this assignment.
			 */
			
			Thread.sleep(SLEEP_DURATION);

			/*
			 *  enter grades for all students, then click save.
			 */
			ArrayList<String> originalGrades = new ArrayList<>();
			elements  = driver.findElements(By.xpath("//input"));
			for (WebElement element : elements) {
				originalGrades.add(element.getAttribute("value"));
				element.clear();
				element.sendKeys(NEW_GRADE);
				Thread.sleep(SLEEP_DURATION);
			}
			
			for (String s : originalGrades) {
				System.out.println("'"+s+"'");
			}

			/*
			 *  Locate submit button and click
			 */
			driver.findElement(By.id("sgrade")).click();
			Thread.sleep(SLEEP_DURATION);
			
			w = driver.findElement(By.id("gmessage"));
			assertThat(w.getText()).withFailMessage("After saving grades, message should be \"Grades saved.\"").startsWith("Grades saved");
			
			driver.navigate().back();  // back button to last page
			Thread.sleep(SLEEP_DURATION);
			
			// find the assignment 'db design' again.
			elements  = driver.findElements(By.xpath("//td"));
			found = false;
			for (WebElement we : elements) {
				if (we.getText().equals(TEST_ASSIGNMENT_NAME)) {
					found=true;
					we.findElement(By.xpath("..//a")).click();
					break;
				}
			}
			Thread.sleep(SLEEP_DURATION);
			assertThat(found).withFailMessage("The test assignment was not found.").isTrue();
			
			// verify the grades. Change grades back to original values

			elements  = driver.findElements(By.xpath("//input"));
			for (int idx=0; idx < elements.size(); idx++) {
				WebElement element = elements.get(idx);
				assertThat(element.getAttribute("value")).withFailMessage("Incorrect grade value.").isEqualTo(NEW_GRADE);
				
				// clear the input value by backspacing over the value
				while(!element.getAttribute("value").equals("")){
			        element.sendKeys(Keys.BACK_SPACE);
			    }
				if (!originalGrades.get(idx).equals("")) element.sendKeys(originalGrades.get(idx));
				Thread.sleep(SLEEP_DURATION);
			}
			driver.findElement(By.id("sgrade")).click();
			Thread.sleep(SLEEP_DURATION);
			
			w = driver.findElement(By.id("gmessage"));
			assertThat(w.getText()).withFailMessage("After saving grades, message should be \"Grades saved.\"").startsWith("Grades saved");


		} catch (Exception ex) {
			throw ex;
		} finally {

			driver.quit();
		}

	}
	
	@Test
	public void addAssignmentTest() throws Exception {
		
		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		driver.get(URL);
		Thread.sleep(SLEEP_DURATION);
		
		
		try {
			//Find add assignment button and click it to open new page
			WebElement submit = driver.findElement(By.id("addAssignment"));
			submit.click();
			Thread.sleep(SLEEP_DURATION);
			
			// Find all input elements on webpage
			WebElement name = driver.findElement(By.id("name"));
			WebElement id = driver.findElement(By.id("idInput"));
			WebElement date = driver.findElement(By.id("date"));
			WebElement saveAssignment = driver.findElement(By.id("sAssignment"));
			WebElement message = driver.findElement(By.id("gmessage"));
			
			// Give assignment name, use course 31045, and make due-date 2023-10-17
			name.sendKeys("addAssignment test");
			id.sendKeys("31045");
			date.sendKeys("2023-10-17");
			Thread.sleep(SLEEP_DURATION);
			
			//Click save assignment button
			saveAssignment.click();
			Thread.sleep(SLEEP_DURATION);
			
			// Make sure that message says assignment saved.
			assertEquals("Assignment saved. ", message.getText());
			
			
		} catch (Exception ex) {
			throw ex;
		} finally {

			driver.quit();
		}

	}
	
	@Test
	public void deleteAssignmentTest() throws Exception {
		
		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		driver.get(URL);
		Thread.sleep(SLEEP_DURATION);
		
		
		try {
			// Will always try to delete assignment with id 1
			WebElement delete = driver.findElement(By.id("deleteOption1"));
			WebElement message = driver.findElement(By.id("message"));
			
			// Click delete assignment button
			delete.click();
			Thread.sleep(SLEEP_DURATION);
			
			// Make sure that message says assignment deleted
			assertEquals("Assignment Deleted. ", message.getText());
			
			
		} catch (Exception ex) {
			throw ex;
		} finally {

			driver.quit();
		}

	}
	
	@Test
	public void editAssignmentTest() throws Exception {
		
		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		driver.get(URL);
		Thread.sleep(SLEEP_DURATION);
		
		
		try {
			// Get all web elements with td tag
			List<WebElement> elements  = driver.findElements(By.xpath("//td"));
			boolean found = false;
			
			// :Loop through all elements with td tag and look for db design assignment, then click edit
			for (WebElement we : elements) {
				if (we.getText().equals(TEST_ASSIGNMENT_NAME)) {
					found=true;
					driver.findElement(By.id("editAssignment0")).click();
					break;
				}
			}
			assertThat(found).withFailMessage("The test assignment was not found.").isTrue();
			
			// Find all input elements on page
			WebElement name = driver.findElement(By.id("name"));
			WebElement id = driver.findElement(By.id("id"));
			WebElement date = driver.findElement(By.id("date"));
			WebElement saveAssignment = driver.findElement(By.id("editAssignment"));
			WebElement message = driver.findElement(By.id("gmessage"));
			
			// Update assignment to have a new due date
			name.sendKeys(TEST_ASSIGNMENT_NAME);
			id.sendKeys("1");
			date.sendKeys("2023-10-17");
			Thread.sleep(SLEEP_DURATION);
			saveAssignment.click();
			Thread.sleep(SLEEP_DURATION);
			
			// Make sure message says assignment changes have been made
			assertEquals("Assignment changes saved. ", message.getText());
			
			driver.navigate().back();  // back button to last page
			Thread.sleep(SLEEP_DURATION);
			
			boolean check2 = false;
			boolean check1 = false;
			
			// Find db design assignment again
			elements  = driver.findElements(By.xpath("//td"));
			for (WebElement we : elements) {

				if (we.getText().equals(TEST_ASSIGNMENT_NAME)) {
					found=true;
					continue;
				}
				
				if (found && !check1) {
					assertEquals("CST 363 - Introduction to Database Systems", we.getText());
					check1 = true;
					continue;
				}
				
				if (check1) {
					assertEquals("2023-10-17", we.getText());
					check2 = true;
					return;
				}
			}
			
			// Ensure that assignment is found, make sure course is correct, and due date was changed.
			assertThat(found).withFailMessage("The test assignment was not found.").isTrue();
			assertThat(check1).withFailMessage("Assignment class edit failed.").isTrue();
			assertThat(check2).withFailMessage("Assignment due date edit failed.").isTrue();
		} catch (Exception ex) {
			throw ex;
		} finally {

			driver.quit();
		}

	}
}
