import java.time.LocalDate;

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

    }
}
