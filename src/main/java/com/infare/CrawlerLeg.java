package com.infare;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.sql.SQLException;

public class CrawlerLeg {
    private int incID;
    private int lastCheckedDate;
    private WebDriver driver;
    FlightInfoDB flightInfoDB;

    public void setDriver() {
        System.setProperty("webdriver.gecko.driver", "geckodriver.exe");
        this.driver = new FirefoxDriver();
    }

    public void loadPage(String url) {
        driver.get(url);

    }

    public void startFareDataCollection() throws SQLException {
        flightInfoDB = new FlightInfoDB();
        flightInfoDB.createFareDataTable();
    }

    public void setLastCheckedDate(int lastCheckedDate) {
        this.lastCheckedDate = lastCheckedDate;
    }

    public int getLastCheckedDate() {
        return lastCheckedDate;
    }

    public void fillSearchForm(String departureAirport, String destinationAirpor) {
        WebElement element = driver.findElement(By.name("searchForm"));
        element.findElement(By.id("airport-select-origin")).clear();
        element.findElement(By.id("airport-select-origin")).sendKeys(departureAirport);
        element.findElement(By.id(departureAirport)).click();
        element.findElement(By.id("airport-select-destination")).sendKeys(destinationAirpor);
        element.findElement(By.id(destinationAirpor)).click();
    }


    public void chooseTripType() {
        driver.findElement(By.name("searchForm")).findElement(By.name("radio_1")).sendKeys(Keys.DOWN);
    }

    public void chooseLowFareCal() {
        WebElement element = driver.findElement(By.name("searchForm"));
        element.findElement(By.name("radio_2")).sendKeys(Keys.DOWN);
    }

    public void searchButton() {
        driver.findElement(By.id("searchButton")).click();
    }

    public void chooseMonth(String month) {
        driver.findElement(By.id("ctl00_ctl00_MainContentRegion_MainRegion_ctl00_ipcFareCalendarSearchBar_ddlDepartureMonth")).sendKeys(month);
    }

    public void chooseCurrency(String currency) {
        driver.findElement(By.id("ctl00_ctl00_MainContentRegion_MainRegion_ctl00_ipcFareCalendarSearchBar_ddlCurrency")).sendKeys(currency);
    }

    public void chooseDirectFlight() {
        driver.findElement(By.id("ctl00_ctl00_MainContentRegion_MainRegion_ctl00_ipcFareCalendarSearchBar_chkRouteTripType")).click();
    }

    public void flightDepartures() throws InterruptedException {
        Thread.sleep(2500);
        if (driver.findElements(By.cssSelector("span.navlink:nth-child(2)")).size() != 0) {
            showNextDay();
        } else {
            String outboundDateID = "OutboundFareCal" + this.lastCheckedDate;
            System.out.println(outboundDateID);
            waitForPageLoad();
            if (driver.findElements(By.id(outboundDateID)).size() != 0) {
                showFlightDepartures(outboundDateID);
            } else {
                System.out.println("not found next day link of fare data on" + this.lastCheckedDate++);
                flightDepartures();
            }
        }
    }

    private void showFlightDepartures(String outboundDateID) throws InterruptedException {
        driver.findElement(By.id(outboundDateID)).click();
        waitForPageLoad();
        if (driver.findElements(By.id("ctl00_ctl00_MainContentRegion_MainRegion_ctl00_lbtContinue")).size() != 0) {
            driver.findElement(By.id("ctl00_ctl00_MainContentRegion_MainRegion_ctl00_lbtContinue")).click();
        }

    }

    private void showNextDay() throws InterruptedException {
        waitForPageLoad();
        driver.findElement(By.cssSelector("span.navlink:nth-child(2)")).click();
        System.out.println("next page from " + this.lastCheckedDate++);
    }

    public void collectFareData() throws InterruptedException {
        if (driver.findElements(By.id("avaday-outbound-result")).size() != 0) {
            waitForPageLoad();
            collectOddRowData();
            waitForPageLoad();
            collectEvenRowData();
        }
    }

    private void collectOddRowData() throws InterruptedException {
        if (driver.findElements(By.id("FlightSelectOutboundStandardLowFare0")).size() != 0) {
            driver.findElement(By.id("FlightSelectOutboundStandardLowFare0")).click();
        } else if (driver.findElements(By.id("FlightSelectOutboundStandardLowFarePlus0")).size() != 0) {
            driver.findElement(By.id("FlightSelectOutboundStandardLowFarePlus0")).click();
        } else if (driver.findElements(By.id("FlightSelectOutboundStandardFlex0")).size() != 0) {
            driver.findElement(By.id("FlightSelectOutboundStandardFlex0")).click();
        } else {
            return;
        }
        waitForPageLoad();
            String id = setID(driver.findElement(By.cssSelector("div.selectioncontainer:nth-child(1) > div:nth-child(1) > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(5) > td:nth-child(1)")).getText());
            String date = convertDate(driver.findElement(By.cssSelector(".headerbox > table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(2)")).getText());
            String depAirport = driver.findElement(By.cssSelector("tr.oddrow:nth-child(2)>td:nth-child(1)>div:nth-child(1)")).getText();
            String arrAirport = driver.findElement(By.cssSelector("tr.oddrow:nth-child(2)>td:nth-child(2)>div:nth-child(1)")).getText();
            String depTime = driver.findElement(By.cssSelector("tr.oddrow:nth-child(1)>td:nth-child(1)>div:nth-child(1)")).getText();
            String arrTime = driver.findElement(By.cssSelector("tr.oddrow:nth-child(1)>td:nth-child(2)>div:nth-child(1)")).getText();
            String price = driver.findElement(By.cssSelector("td.totalfarecell:nth-child(2)")).getText().substring(1);
            String taxes = driver.findElement(By.cssSelector("div.selectioncontainer:nth-child(1) > div:nth-child(1) > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(15) > td:nth-child(2)")).getText().substring(1);
            try {
                flightInfoDB.fillFareData(id, date, depAirport, arrAirport, depTime, arrTime, price, taxes);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return;
    }


    private void collectEvenRowData() throws InterruptedException {
        waitForPageLoad();
        if (driver.findElements(By.id("FlightSelectOutboundStandardLowFare1")).size() != 0) {
            driver.findElement(By.id("FlightSelectOutboundStandardLowFare1")).click();
        } else if (driver.findElements(By.id("FlightSelectOutboundStandardLowFarePlus1")).size() != 0) {
            driver.findElement(By.id("FlightSelectOutboundStandardLowFarePlus1")).click();
        } else if (driver.findElements(By.id("FlightSelectOutboundStandardFlex1")).size() != 0) {
            driver.findElement(By.id("FlightSelectOutboundStandardFlex1")).click();
        } else {
            return;
        }
        waitForPageLoad();
            String id = setID(driver.findElement(By.cssSelector("div.selectioncontainer:nth-child(1) > div:nth-child(1) > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(5) > td:nth-child(1)")).getText());
            String date = convertDate(driver.findElement(By.cssSelector(".headerbox > table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(2)")).getText());
            String price = driver.findElement(By.cssSelector("td.totalfarecell:nth-child(2)")).getText().substring(1);
            String taxes = driver.findElement(By.cssSelector("div.selectioncontainer:nth-child(1) > div:nth-child(1) > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(15) > td:nth-child(2)")).getText().substring(1);
            Thread.sleep(2000);
                String depAirport = driver.findElement(By.cssSelector("tr.evenrow:nth-child(4) > td:nth-child(1) > div:nth-child(1)")).getText();
                String arrAirport = driver.findElement(By.cssSelector("tr.evenrow:nth-child(4) > td:nth-child(2) > div:nth-child(1)")).getText();
                String depTime = driver.findElement(By.cssSelector("tr.evenrow:nth-child(3)>td:nth-child(1)>div:nth-child(1)")).getText();
                String arrTime = driver.findElement(By.cssSelector("tr.evenrow:nth-child(3)>td:nth-child(2)>div:nth-child(1)")).getText();
                try {
                    flightInfoDB.fillFareData(id, date, depAirport, arrAirport, depTime, arrTime, price, taxes);
                } catch (SQLException e) {
                    e.printStackTrace();
              //  }
            }
        return;
    }

    private void waitForPageLoad() throws InterruptedException {
        Thread.sleep(1000);
        if (driver.findElements(By.cssSelector(".updateprogressbox")).size() != 0 && driver.findElement(By.cssSelector(".updateprogressbox")).isDisplayed()) {
            Thread.sleep(1500);
            waitForPageLoad();
        }
        return;
    }


    private String convertDate(String date) {
        String yearVal = date.substring(date.length() - 4);
        String monthVal = date.substring(date.length() - 8, date.length() - 5);
        String dayVal = date.substring(date.length() - 12, date.length() - 10);
        String convDate = yearVal + "-" + monthVal + "-" + dayVal;
        return convDate;
    }

    private String setID(String id) {
        String newID = id.substring(7, 13) + "-" + (++incID);
        return newID;
    }
public void endFareDataCollection(){
    driver.quit();
}

}
