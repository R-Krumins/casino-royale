import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
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
    // JSONArray history;

    public Stock(String symbol, String companyName, String industry, String description) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.industry = industry;
        this.description = description;
        this.price = 100;
    }

    public static void retriveStocksFromDB() {
        Stock.ALL = App.db.getAllStocks();
    }

    public String getPriceString(){
        return "$" + dFormater.format(this.price);
    }

    // public void getPrice(Date cum) {
    // JSONObject price = this.history.getJSONObject(0);
    // JSONObject json = new JSONObject(price);
    // System.out.println(json.isEmpty());
    // System.out.println(json.has("mapType"));
    // Date date = new Date(json.getLong("x"));
    // String money = json.getString("y");

    // System.out.println(date + " -> " + money);
    // }

    // public void cum() {
    // for (int i = 0; i < history.length(); i++) {
    // JSONObject json = history.getJSONObject(i);
    // long epoch = json.getLong("x");
    // LocalDate date =
    // Instant.ofEpochMilli(epoch).atZone(ZoneId.systemDefault()).toLocalDate();
    // Double money = json.getDouble("y");

    // System.out.println(date + " -> " + money);
    // }

    // }
}
