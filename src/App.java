
import java.time.LocalDate;

public class App {

    public static DB db;
    public static Window window;
    public static GameClock gameClock;

    public static void main(String[] args) throws Exception {
        db = new DB();
        Stock.retriveStocksFromDB();
        window = new Window();

        gameClock = new GameClock(LocalDate.of(2010, 1, 1));
        gameClock.start();

    }
}
