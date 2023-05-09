import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;

public class Window extends JFrame {
    static int WINDOW_WIDTH = 700;
    static int WINDOW_HEIGHT = 600;

    Stock searchedStock;

    JTextField searchStockBox;
    JLabel result_symbol;
    JLabel result_companyName;
    JLabel result_industry;
    JLabel result_desc;
    JButton buyStockBtn;
    JLabel currentDate;
    JPanel playerStocksPanel;

    ArrayList<JLabel> playerStocksLabels = new ArrayList<>();

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
        result_industry = new JLabel();
        result_desc = new JLabel();
        stockLookUpPanel.add(result_symbol);
        stockLookUpPanel.add(result_companyName);
        stockLookUpPanel.add(result_industry);
        stockLookUpPanel.add(result_desc);

        buyStockBtn = new JButton("Buy Stock");
        buyStockBtn.setVisible(false);
        buyStockBtn.addActionListener(e -> {
            buyStock();
        });
        stockLookUpPanel.add(buyStockBtn);

        add(stockLookUpPanel, BorderLayout.CENTER);
    }

    private void init_topBar() {
        currentDate = new JLabel();

        JPanel topBarPanel = new JPanel();
        JButton pauseBtn = new JButton("|>");
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

    private void playerStocksPanel_addStock(Stock stock){
        JLabel stockLabel = new JLabel(stock.symbol +" "+ stock.getPriceString());
        playerStocksLabels.add(stockLabel);
        playerStocksPanel.add(stockLabel);
    }

    public void updatePlayerStocks() {
        for (int i = 0; i < Stock.ALL.size(); i++) {
            Stock stock = Stock.ALL.get(i);
            playerStocksLabels.get(i).setText(stock.symbol +" "+ stock.getPriceString());
        }
    }

    private void searchForStock() {
        searchedStock = WebScraper.getStock(searchStockBox.getText());

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
            result_industry.setText("");
            result_desc.setText("");
            buyStockBtn.setVisible(false);
        }

    }

    private void buyStock() {
        App.db.saveStock(searchedStock);
        searchedStock.updatePriceHistory();
        playerStocksPanel_addStock(searchedStock);
    }

    private void changeClockSpeed(int speed) {
        if (speed == 0) {
            App.gameClock.togglePause();
        }
    }

    public void setCurrentDate(String date) {
        currentDate.setText(date);
    }
}
