import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DB {
    private Connection con;
    private Statement stmt;
    private PreparedStatement preparedStmt;
    private String dbName = "stocks";
    private String user = "postgres";
    private String password = "guest";

    public DB() {
        try {
            String params = "?useSSL=false&autoReconnect=true&allowMultiQueries=true";
            this.con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + dbName + params,
                    user, password);
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

    public void executeUpdate(String query) throws SQLException {
        this.stmt.executeUpdate(query);
    }
}
