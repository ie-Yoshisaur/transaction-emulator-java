import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DirtyReadAnomalyTest {
  public static void main(String arg[]) throws InterruptedException {
    final Data data = new Data(1, 0);
    final Transaction transaction1 = new Transaction();
    final Transaction transaction2 = new Transaction();

    ExecutorService threadPool = Executors.newFixedThreadPool(2);

    threadPool.submit(() -> {
      transaction1.read(data);
      transaction1.commit();
    });

    threadPool.submit(() -> {
      transaction2.read(data);
      int value = transaction2.getValue(data.getDataId());
      transaction2.write(data.getDataId(), value + 1);
      transaction2.rollback();
    });

    threadPool.shutdown();
    threadPool.awaitTermination(1, TimeUnit.MINUTES);

    int finalDataValue = data.getValue();
    int finalTransaction1Value = transaction1.getValue(data.getDataId());
    if (finalDataValue == 0 && finalTransaction1Value == 1) {
      System.err.println(
          "Dirty Read Anomaly occurred: data.getValue() is 0 and transaction1.getValue() is 1");
    }
  }
}
