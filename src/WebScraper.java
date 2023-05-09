import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;

public class WebScraper {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-dd-MM");

    public static Stock getStock(String stock) {
        String url = "https://api.nasdaq.com/api/company/" + stock + "/company-profile";
        JSONObject response = makeRequest(url);

        JSONObject validStock;
        try {
            validStock = response.getJSONObject("data");
        } catch (Exception e) {
            return null;
        }

        String symbol = validStock.getJSONObject("Symbol").getString("value");
        String companyName = validStock.getJSONObject("CompanyName").getString("value");
        String industry = validStock.getJSONObject("Industry").getString("value");
        String description = validStock.getJSONObject("CompanyDescription").getString("value");

        return new Stock(symbol, companyName, industry, description);

    }

    public static boolean stockExists(String stock) {
        String url = "https://api.nasdaq.com/api/company/" + stock + "/company-profile";
        JSONObject response = makeRequest(url);

        try {
            response.getJSONObject("data");
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    private static JSONObject makeRequest(String url) {
        try {
            String response = Jsoup
                    .connect(url)
                    .timeout(3000)
                    .userAgent("Mozilla/5.0 (Windows 10; Win64; x64)")
                    .ignoreContentType(true)
                    .execute()
                    .body();

            return new JSONObject(response);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static HashMap<LocalDate, Double> getStockHistory(String stockSymbol, LocalDate fromDate, LocalDate toDate) {
        String url = "https://api.nasdaq.com/api/quote/" + stockSymbol 
                + "/chart?assetclass=stocks&fromdate=" + formatter.format(fromDate) 
                + "&todate="+ formatter.format(toDate);

        JSONObject response = makeRequest(url);
        JSONArray historyJSON = response.getJSONObject("data").getJSONArray("chart");

        HashMap<LocalDate, Double> history = new HashMap<>();

        for (int i = 0; i < historyJSON.length(); i++) {
            JSONObject point = historyJSON.getJSONObject(i);
            long epoch = point.getLong("x");
            // epoch must me rounded to that time is 00:00
            LocalDate pointDate = Instant.ofEpochMilli(epoch).atZone(ZoneId.systemDefault()).toLocalDate();
            Double pointPrice = point.getDouble("y");

            history.put(pointDate, pointPrice);
        }

        return history;
    }

    private static JSONObject getTestResponse() {
        return new JSONObject();
    }

    // private static Stock createStock(JSONObject json) {
    // String id = json.getString("symbol");
    // String companyName = json.getString("companyName");
    // double price = Double.parseDouble(json
    // .getJSONObject("primaryData")
    // .getString("lastSalePrice")
    // .replace("$", ""));

    // return new Stock(id, companyName, price);

    // }
}
