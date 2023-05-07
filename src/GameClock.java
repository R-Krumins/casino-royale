import java.time.LocalDate;

public class GameClock extends Thread {

    public LocalDate currentDate;
    private boolean isPaused = true;;

    public GameClock(LocalDate currentDate) {
        this.currentDate = currentDate;
    }

    @Override
    public void run() {
        synchronized (this) {
            while (true) {
                updateDate();
                updateStocks();
                sleep(100);

                if (isPaused) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private void updateDate() {
        currentDate = currentDate.plusDays(1);
        App.window.setCurrentDate(currentDate.toString());
    }

    private void updateStocks() {
        Stock.ALL.forEach(stock -> {
            stock.price += 0.1;
        });
        App.window.updatePlayerStocks();
    }

    private void sleep(int pauseTime) {
        try {
            Thread.sleep(pauseTime);
        } catch (Exception e) {
            System.out.println("Game clock exploded");
        }
    }

    public void togglePause() {
        if (isPaused) {
            synchronized (this) {
                this.isPaused = false;
                notify();
                System.out.println("Game Resumed.");
            }
        } else {
            this.isPaused = true;
            System.out.println("Game Paused.");
        }
    }

}
