package com.infare;

import java.sql.SQLException;

public class Crawler {
    public static void main(String[] args) throws SQLException, InterruptedException {
        CrawlerLeg cl = new CrawlerLeg();
        cl.setDriver();
        cl.loadPage("http://www.norwegian.com/uk");
        cl.fillSearchForm("OSL", "RIX");
        cl.chooseTripType();
        cl.chooseLowFareCal();
        cl.searchButton();
        cl.chooseMonth("September 2018");
        cl.chooseCurrency("EUR");
        cl.chooseDirectFlight();
        cl.startFareDataCollection();
        cl.setLastCheckedDate(1);
        while (cl.getLastCheckedDate() <= 30) {
            cl.flightDepartures();
            cl.collectFareData();
        }
        cl.endFareDataCollection();

    }
}
