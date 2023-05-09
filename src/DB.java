import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.sql.Date;

import org.json.JSONArray;
import org.json.JSONObject;

public class DB {
    //infro for DB connection
    private String url = "jdbc:postgresql://localhost:5432/stocks";
    private String user = "postgres";
    private String password = "admin";
    
    
    private Connection con;
    private Statement stmt;
    private PreparedStatement preparedStmt;
    private String dbName = "stocks";
    

    public DB() {
        try {
            String params = "?useSSL=false&autoReconnect=true&allowMultiQueries=true";
            this.con = DriverManager.getConnection(url + params, user, password);
            this.stmt = this.con.createStatement();
            System.out.println("Successfully established connection with DB " + dbName);
        } catch (Exception e) {
            System.err.println("Problems creating DB connection");
            e.printStackTrace();
        }

    }

    public void saveStock(Stock stock) {
        try {
            preparedStmt = con.prepareStatement("INSERT INTO stocks VALUES (?, ?, ?, ?)");
            preparedStmt.setString(1, stock.symbol);
            preparedStmt.setString(2, stock.companyName);
            preparedStmt.setString(3, stock.industry);
            preparedStmt.setString(4, stock.description);

            preparedStmt.executeUpdate();
            System.out.println("Saved " + stock.symbol + " to DB.");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public LocalDate getStock_history_startDate(String stockSymbol){
        return getStockHistoryDate("history_startdate", stockSymbol);
    }

    public LocalDate getStock_history_endDate(String stockSymbol){
        return getStockHistoryDate("history_enddate", stockSymbol);
    }

    private LocalDate getStockHistoryDate(String dateType, String stockSymbol){
        String query = "SELECT "+dateType+" FROM stocks WHERE symbol = ?";

        try {
            preparedStmt = con.prepareStatement(query);
            preparedStmt.setString(1, stockSymbol);
            ResultSet result = preparedStmt.executeQuery();

            while(result.next()){
                if(result.getDate(dateType) == null) return null;
                else return result.getDate(dateType).toLocalDate();
            }
        } catch (SQLException e) {
            System.out.println("Unable to retrieve "+stockSymbol+" history "+dateType);
            e.printStackTrace();
        }

        return null;
    }

    public void savePriceHistory(HashMap<LocalDate, Double> history, String stockSymbol, LocalDate startDate, LocalDate endDate) {
        String query = "INSERT INTO pricehistory VALUES (?,?,?)";

        try {
            preparedStmt = con.prepareStatement(query);

            for (Map.Entry<LocalDate, Double> entry : history.entrySet()) {
                preparedStmt.setDate(1, Date.valueOf(entry.getKey()));
                preparedStmt.setString(2, stockSymbol);
                preparedStmt.setDouble(3, entry.getValue());
                preparedStmt.addBatch();
            }

            preparedStmt.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }

       
        updatStockHistoryDates(stockSymbol, startDate, endDate);
    }

    private void updatStockHistoryDates(String stockSymbol, LocalDate startDate, LocalDate endDate){
        String query = "UPDATE stocks SET history_startdate = ?::date, history_enddate = ?::date WHERE symbol = ?;";

        try {
            preparedStmt = con.prepareStatement(query);

            preparedStmt.setString(1, startDate.toString());
            preparedStmt.setString(2, endDate.toString());
            preparedStmt.setString(3, stockSymbol);

            preparedStmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Unable to update "+stockSymbol+" history dates");
            e.printStackTrace();
        }
    }

    public ArrayList<Stock> getAllStocks() {
        ArrayList<Stock> stocks = new ArrayList<>();
        String query = "SELECT * FROM stocks";

        try {
            ResultSet results = stmt.executeQuery(query);

            while (results.next()) {
                String symbol = results.getString("symbol");
                String compnayName = results.getString("company_name");
                String industry = results.getString("industry");
                String description = results.getString("description");

                stocks.add(new Stock(symbol, compnayName, industry, description));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stocks;
    }

    public HashMap<LocalDate, HashMap<Stock, Double>> getPriceHistory(LocalDate startDate, LocalDate endDate){
        String query = "SELECT * FROM pricehistory WHERE date > ?::date AND date <= ?::date;";
        ResultSet result = null;
        
        //retrieve price history
        try {
            preparedStmt = con.prepareStatement(query);
            preparedStmt.setString(1, startDate.toString());
            preparedStmt.setString(2, endDate.toString());
            result = preparedStmt.executeQuery();
        } catch (SQLException e) {
            System.out.println("Unable to retrieve price history");
            e.printStackTrace();
            return null;
        }

       
        //process price history
        //1. prepare history
        HashMap<LocalDate, HashMap<Stock, Double>> history = new HashMap<>();
        for(int i = 1; i <= Stock.getCacheSize(); i++){
            history.put(startDate.plusDays(i), new HashMap<Stock, Double>());
        }

        //2. populate history with DB data
        try {
            while(result.next()){ 
                LocalDate date = result.getDate("date").toLocalDate();
                Stock stock = Stock.getBySymbol(result.getString("symbol"));
                Double price = result.getDouble("price");
                
                history.get(date).put(stock, price);
            }
        } catch (SQLException e) {
            System.out.println("Unable to proccess price history");
            e.printStackTrace();
            return null;
        }
        
        
        
        return history;
    } 

    public void executeUpdate(String query) throws SQLException {
        this.stmt.executeUpdate(query);
    }
}
