import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

public class Stock {
    String symbol;
    String companyName;
    String industry;
    String description;
    double price;

    private static final DecimalFormat dFormater = new DecimalFormat("0.00");
    public static ArrayList<Stock> ALL = new ArrayList<>();
    
    //cache and its parameters
    private static HashMap<LocalDate, HashMap<Stock, Double>> cache;
    private static LocalDate cachedMaxDate = App.gameClock.getCurrentDate();
    private static int chacheSize = 365; //in days

    public Stock(String symbol, String companyName, String industry, String description) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.industry = industry;
        this.description = description;
        this.price = 100;
    }

    public void updatePriceHistory() {
        //game dates
        LocalDate gameCurrentDate = App.gameClock.getCurrentDate();
        LocalDate gameEndDate = App.gameClock.getEndDate();    
        //stock dates in db
        LocalDate fromDate = App.db.getStock_history_startDate(this.symbol);
        LocalDate toDate = App.db.getStock_history_endDate(this.symbol);


        if(fromDate == null){
            getAndSavePriceHistory(gameCurrentDate, gameEndDate);
            return;
        }
            
        if(fromDate.isBefore(gameCurrentDate))
            getAndSavePriceHistory(fromDate, gameCurrentDate);
        else if(fromDate.isAfter(gameCurrentDate))
            getAndSavePriceHistory(gameCurrentDate, fromDate);

        if(toDate.isBefore(gameEndDate))
            getAndSavePriceHistory(toDate, gameEndDate);    
    }

    private void getAndSavePriceHistory(LocalDate fromDate, LocalDate toDate){
        HashMap<LocalDate, Double> history = WebScraper.getStockHistory(this.symbol, fromDate, toDate);
        App.db.savePriceHistory(history, this.symbol, fromDate, toDate);
    }

    public static void cacheNext(){
        LocalDate nextMaxdate = cachedMaxDate.plusDays(chacheSize);
        cache = App.db.getPriceHistory(cachedMaxDate, nextMaxdate);
        cachedMaxDate = nextMaxdate;
    }

    public static void updatePrices(LocalDate date){
        cache.get(date).forEach((stock, price) -> {
            stock.price = price;
        });
    }

    public static void retriveStocksFromDB() {
        Stock.ALL = App.db.getAllStocks();
    }

    public String getPriceString() {
        return "$" + dFormater.format(this.price);
    }
    
    public static int getCacheSize(){
        return Stock.chacheSize;
    }

    public static Stock getBySymbol(String symbol){
        for(Stock stock : Stock.ALL){
            if(stock.symbol.equals(symbol)) return stock;
        }
        
        return null;
    }
}
