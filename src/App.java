
import java.awt.FontMetrics;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class App {

    public static DB db;
    public static Window window;
    public static GameClock gameClock;

    public static void main(String[] args) throws Exception {

        gameClock = new GameClock(LocalDate.of(2016, 1, 1));

        db = new DB();
        Stock.retriveStocksFromDB();
        Stock.cacheNext();
        window = new Window();

        gameClock.start();

        // Date date = new GregorianCalendar(2014, Calendar.FEBRUARY, 11).getTime();
        // db.savePriceHistory(WebScraper.getStockHistory("AAPL", date), "AAPL");

    }
}
