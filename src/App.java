
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class App {

    public static DB db;
    public static Window window;
    public static GameClock gameClock;

    public static void main(String[] args) throws Exception {
        db = new DB();

        Date date = new GregorianCalendar(2014, Calendar.FEBRUARY, 11).getTime();
        db.savePriceHistory(WebScraper.getStockHistory("AAPL", date), "AAPL");
        // Stock.retriveStocksFromDB();
        // window = new Window();

        // gameClock = new GameClock(LocalDate.of(2010, 1, 1));
        // gameClock.start();

    }
}
