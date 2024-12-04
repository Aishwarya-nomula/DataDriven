package datadrivenfromexcel;


import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.*;

import java.io.FileInputStream;
import java.io.IOException;

import static org.testng.Assert.*;

public class DataDrivenReadExcelData {

  private WebDriver driver;

  @BeforeMethod
  public void setUp() {

    driver = new ChromeDriver();
    driver.manage().window().maximize();
    driver.get("https://www.saucedemo.com/");
  }

  @Test(dataProvider = "LoginData")
  public void loginTest(String username, String password) throws IOException {

    WebElement usernameField = driver.findElement(By.id("user-name"));
    WebElement passwordField = driver.findElement(By.id("password"));
    WebElement loginButton = driver.findElement(By.id("login-button"));


    usernameField.clear();
    usernameField.sendKeys(username);
    passwordField.clear();
    passwordField.sendKeys(password);
    loginButton.click();

    String currentUrl = driver.getCurrentUrl();
    if (currentUrl.equals("https://www.saucedemo.com/inventory.html")) {
      System.out.println("Login successful for user: " + username);

      WebElement productPageTitle = driver.findElement(By.className("title"));
      assertEquals(productPageTitle.getText(), "Products", "Product page title should be displayed.");
    } else {

      WebElement errorMessage = driver.findElement(By.cssSelector("[data-test='error']"));
      assertTrue(errorMessage.isDisplayed(), "Error message should be displayed for invalid login.");
      System.out.println("Login failed for user: " + username + ". Error: " + errorMessage.getText());
    }

    driver.navigate().refresh();
  }

  @DataProvider(name = "LoginData")
  public Object[][] testDataGenerator() throws IOException {

    String excelFilePath = "C://Users//nomula.aishwarya//Desktop//Testing//mypracticeframeworkss//TestData//LoginData (1).xlsx";
    FileInputStream fileInputStream = new FileInputStream(excelFilePath);


    XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
    XSSFSheet sheet = workbook.getSheet("Sheet1");
    int numberOfRows = sheet.getPhysicalNumberOfRows();
    int numberOfColumns = sheet.getRow(0).getPhysicalNumberOfCells();


    Object[][] testData = new Object[numberOfRows][numberOfColumns];
    DataFormatter formatter = new DataFormatter();

    for (int i = 0; i < numberOfRows; i++) {
      XSSFRow row = sheet.getRow(i);
      for (int j = 0; j < numberOfColumns; j++) {
        XSSFCell cell = row.getCell(j);
        testData[i][j] = formatter.formatCellValue(cell);
      }
    }
    workbook.close();
    return testData;
  }

  @AfterMethod
  public void tearDown() {

    if (driver != null) {
      driver.quit();
    }
  }
}
