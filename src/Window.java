import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class Window extends JFrame {
    static int WINDOW_WIDTH = 700;
    static int WINDOW_HEIGHT = 600;

    Stock searchedStock;

    JTextField searchStockBox;
    JLabel result_symbol;
    JLabel result_price;
    JLabel result_companyName;
    JLabel result_industry;
    JLabel result_desc;
    JButton buyStockBtn;
    JButton sellStockBtn;
    JLabel currentDate;
    JPanel playerStocksPanel;
    JLabel porfolioValue;
    JLabel liquidityValue;
    JButton pauseBtn;

    ArrayList<JLabel> playerStocksLabels = new ArrayList<>();
    HashMap<LocalDate, Double> searchedStockPrices;

    public Window() {
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setTitle("Stock Trader 3000");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        init_components();
        setVisible(true);
    }

    private void init_components() {
        setLayout(new BorderLayout());
        init_stockLookUpPanel();
        init_topBar();
        init_playerStocks();
        init_playerStatsPanel();
    }

    private void init_stockLookUpPanel() {
        JPanel stockLookUpPanel = new JPanel();
        stockLookUpPanel.setLayout(new BoxLayout(stockLookUpPanel, BoxLayout.Y_AXIS));

        // top row
        JPanel topRow = new JPanel();
        topRow.setLayout(new BoxLayout(topRow, BoxLayout.X_AXIS));
        stockLookUpPanel.add(topRow);

        searchStockBox = new JTextField();
        searchStockBox.setMaximumSize(new Dimension(250, 30));
        topRow.add(searchStockBox);

        JButton searchStockBtn = new JButton("Search");
        searchStockBtn.addActionListener(e -> {
            searchForStock();
        });
        topRow.add(searchStockBtn);

        // bottom;
        result_symbol = new JLabel();
        result_companyName = new JLabel();
        result_price = new JLabel();
        result_industry = new JLabel();
        result_desc = new JLabel();
        stockLookUpPanel.add(result_symbol);
        stockLookUpPanel.add(result_companyName);
        stockLookUpPanel.add(result_price);
        stockLookUpPanel.add(result_industry);
        stockLookUpPanel.add(result_desc);

        buyStockBtn = new JButton("Buy");
        buyStockBtn.setVisible(false);
        buyStockBtn.addActionListener(e -> {
            buyStock();
        });
        stockLookUpPanel.add(buyStockBtn);

        sellStockBtn = new JButton("Sell");
        sellStockBtn.setVisible(false);
        sellStockBtn.addActionListener(e -> {
            sellStock();
        });
        stockLookUpPanel.add(sellStockBtn);

        add(stockLookUpPanel, BorderLayout.CENTER);
    }

    private void init_topBar() {
        currentDate = new JLabel();

        JPanel topBarPanel = new JPanel();
        pauseBtn = new JButton("|>");
        JButton x1SpeedBtn = new JButton("1x");
        JButton x3SpeedBtn = new JButton("2x");
        JButton x5SpeedBtn = new JButton("5x");
        pauseBtn.addActionListener(e -> {
            changeClockSpeed(0);
        });
        x1SpeedBtn.addActionListener(e -> {
            changeClockSpeed(1);
        });
        x3SpeedBtn.addActionListener(e -> {
            changeClockSpeed(3);
        });
        x5SpeedBtn.addActionListener(e -> {
            changeClockSpeed(5);
        });

        topBarPanel.add(currentDate);
        topBarPanel.add(pauseBtn);
        topBarPanel.add(x1SpeedBtn);
        topBarPanel.add(x3SpeedBtn);
        topBarPanel.add(x5SpeedBtn);

        add(topBarPanel, BorderLayout.NORTH);
    }

    private void init_playerStocks() {
        playerStocksPanel = new JPanel();
        playerStocksPanel.setLayout(new BoxLayout(playerStocksPanel, BoxLayout.Y_AXIS));
        add(playerStocksPanel, BorderLayout.EAST);

        for (Stock stock : Stock.ALL) {
            playerStocksPanel_addStock(stock);
        }
    }

    private void init_playerStatsPanel() {
        JPanel playerStatsPanel = new JPanel();
        playerStatsPanel.setLayout(new BoxLayout(playerStatsPanel, BoxLayout.Y_AXIS));
        add(playerStatsPanel, BorderLayout.WEST);

        JLabel porfolioText = new JLabel("Porfolio value:");
        porfolioValue = new JLabel("$0");
        JLabel liquidityText = new JLabel("Liquidity:");
        liquidityValue = new JLabel(Stock.getplayerLiquidity());

        playerStatsPanel.add(porfolioText);
        playerStatsPanel.add(porfolioValue);
        playerStatsPanel.add(liquidityText);
        playerStatsPanel.add(liquidityValue);
    }

    private void playerStocksPanel_addStock(Stock stock) {
        JLabel stockLabel = new JLabel(stock.symbol + " " + stock.getPriceString());
        playerStocksLabels.add(stockLabel);
        playerStocksPanel.add(stockLabel);
    }

    public void updateWindow(LocalDate date) {

        if (searchedStockPrices != null && searchedStockPrices.get(date) != null)
            result_price.setText("$" + searchedStockPrices.get(date).toString());

        for (int i = 0; i < Stock.ALL.size(); i++) {
            Stock stock = Stock.ALL.get(i);
            playerStocksLabels.get(i)
                    .setText(stock.symbol + " " + stock.getPriceString() + " (x" + stock.getCount() + ")");
        }

        liquidityValue.setText(Stock.getplayerLiquidity());
        porfolioValue.setText(Stock.getplayerPortfolioValue());
    }

    private void searchForStock() {

        String search = searchStockBox.getText().toUpperCase();
        // first check if this stock is already owned by the player
        searchedStock = Stock.getBySymbol(search);
        // if no find it from the web
        if (searchedStock == null) {
            searchedStock = WebScraper.getStock(search);
            searchedStockPrices = WebScraper.getStockHistory(searchedStock.symbol, App.gameClock.getCurrentDate(),
                    App.gameClock.getEndDate());
            sellStockBtn.setVisible(false);
        } else {
            sellStockBtn.setVisible(true);
        }

        // show info for searched stock
        if (searchedStock != null) {
            result_symbol.setText(searchedStock.symbol);
            result_companyName.setText(searchedStock.companyName);
            result_industry.setText(searchedStock.industry);
            result_desc.setText("<html>" + searchedStock.description + "</html>");
            buyStockBtn.setVisible(true);
        } else {
            searchedStock = null;
            result_symbol.setText("No such stock exists!");
            result_companyName.setText("");
            result_price.setText("");
            result_industry.setText("");
            result_desc.setText("");
            buyStockBtn.setVisible(false);
        }

    }

    private void buyStock() {
        if (Stock.ALL.contains(searchedStock)) {
            searchedStock.incrementCount(1);
            return;
        }

        Stock.icrementPlayerLiquidity(-searchedStock.price);
        App.db.saveStock(searchedStock);
        searchedStock.updatePriceHistory();
        Stock.ALL.add(searchedStock);
        Stock.updateCache(searchedStock);
        playerStocksPanel_addStock(searchedStock);
    }

    private void sellStock() {
        if (searchedStock.getCount() > 0) {
            searchedStock.incrementCount(-1);
        }
    }

    private void changeClockSpeed(int speed) {
        if (speed == 0) {
            App.gameClock.togglePause();
            pauseBtn.setText(App.gameClock.isPaused() ? "|>" : "||");
        }
    }

    public void setCurrentDate(String date) {
        currentDate.setText(date);
    }
}
