import java.time.LocalDate;

public class GameClock extends Thread {

    private LocalDate currentDate; // current game date
    private LocalDate endDate; // end date for game (real world current time)
    private boolean isPaused = true;

    public int clockSpeed; // in days skipped each tick

    public GameClock(LocalDate currentDate) {
        this.currentDate = currentDate;
        this.endDate = LocalDate.now();
        this.clockSpeed = 1;
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

    public LocalDate getCurrentDate() {
        return this.currentDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    private void updateDate() {
        currentDate = currentDate.plusDays(clockSpeed);
        App.window.setCurrentDate(currentDate.toString());
    }

    private void updateStocks() {
        Stock.updatePrices(currentDate);
        App.window.updateWindow(currentDate);
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

    public boolean isPaused() {
        return this.isPaused;
    }

}
