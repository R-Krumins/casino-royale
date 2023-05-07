import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;

public class WebScraper {

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-dd-MM");

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

    public static JSONArray getStockHistory(String stock, Date date) {
        String url = "https://api.nasdaq.com/api/quote/" + stock + "/chart?assetclass=stocks&fromdate="
                + formatter.format(date) + "&todate=2023-05-06";

        JSONObject response = makeRequest(url);
        return response.getJSONObject("data").getJSONArray("chart");
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
