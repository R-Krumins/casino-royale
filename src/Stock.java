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
    private int count;
    LocalDate oldestAvalaibeDate;

    private static double playerLiquidity = 10_000;
    private volatile static double playerPortfolioValue = 0;

    private static final DecimalFormat dFormater = new DecimalFormat("0.00");
    public static ArrayList<Stock> ALL = new ArrayList<>();

    // cache and its parameters
    private static HashMap<LocalDate, HashMap<Stock, Double>> cache = new HashMap<>();
    private static LocalDate cachedMaxDate = App.gameClock.getCurrentDate();
    private static int chacheSize = 365; // in days
    // threshold date for when chache should be updated
    private static LocalDate cacheNextThresholdDate = App.gameClock.getCurrentDate().plusDays(chacheSize / 2);

    public Stock(String symbol, String companyName, String industry, String description) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.industry = industry;
        this.description = description;
        this.price = 0;
        this.count = 1;
    }

    public void updatePriceHistory() {
        // game dates
        LocalDate gameCurrentDate = App.gameClock.getCurrentDate();
        LocalDate gameEndDate = App.gameClock.getEndDate();
        // stock dates in db
        LocalDate fromDate = App.db.getStock_history_startDate(this.symbol);
        LocalDate toDate = App.db.getStock_history_endDate(this.symbol);

        if (fromDate == null) {
            getAndSavePriceHistory(gameCurrentDate, gameEndDate);
            return;
        }

        if (fromDate.isBefore(gameCurrentDate))
            getAndSavePriceHistory(fromDate, gameCurrentDate);
        else if (fromDate.isAfter(gameCurrentDate))
            getAndSavePriceHistory(gameCurrentDate, fromDate);

        if (toDate.isBefore(gameEndDate))
            getAndSavePriceHistory(toDate, gameEndDate);
    }

    private void getAndSavePriceHistory(LocalDate fromDate, LocalDate toDate) {
        HashMap<LocalDate, Double> history = WebScraper.getStockHistory(this.symbol, fromDate, toDate);
        App.db.savePriceHistory(history, this.symbol, fromDate, toDate);
    }

    public static void cacheNext() {
        System.out.println("Starting cache procedure...");
        LocalDate nextMaxdate = cachedMaxDate.plusDays(chacheSize);
        cache.putAll(App.db.getPriceHistory(cachedMaxDate, nextMaxdate));
        cachedMaxDate = nextMaxdate;
        System.out.println("Cache updated upto " + cachedMaxDate);
    }

    public static void updateCache(Stock stock) {
        App.db.getPriceHistory(App.gameClock.getCurrentDate(), cachedMaxDate, stock.symbol).forEach((date, value) -> {
            if (cache.get(date) != null)
                cache.get(date).putAll(value);
        });
    }

    public static void updatePrices(LocalDate date) {
        if (App.gameClock.getCurrentDate().isAfter(cacheNextThresholdDate)) {
            cacheNextThresholdDate = cacheNextThresholdDate.plusDays(chacheSize);
            new Thread(() -> {
                cacheNext();
            }).start();
        }

        cache.get(date).forEach((stock, newPrice) -> {
            playerPortfolioValue += (newPrice - stock.price) * stock.count;
            stock.price = newPrice;
        });
    }

    public static void retriveStocksFromDB() {
        Stock.ALL = App.db.getAllStocks();
    }

    public String getPriceString() {
        return "$" + dFormater.format(this.price);
    }

    public static String getplayerLiquidity() {
        return "$" + dFormater.format(playerLiquidity);
    }

    public static void icrementPlayerLiquidity(double value) {
        Stock.playerLiquidity += value;
    }

    public static String getplayerPortfolioValue() {
        return "$" + dFormater.format(playerPortfolioValue);
    }

    public static int getCacheSize() {
        return Stock.chacheSize;
    }

    public void incrementCount(int value) {
        this.count += value;
        Stock.playerPortfolioValue += this.price * value;
    }

    public int getCount() {
        return this.count;
    }

    public static Stock getBySymbol(String symbol) {
        for (Stock stock : Stock.ALL) {
            if (stock.symbol.equals(symbol))
                return stock;
        }

        return null;
    }
}
