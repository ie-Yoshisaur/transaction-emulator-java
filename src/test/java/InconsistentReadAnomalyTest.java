import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class InconsistentReadAnomalyTest {
  public static void main(String arg[]) throws InterruptedException {
    final Data data = new Data(1, 0);
    final Transaction transaction1 = new Transaction();
    final Transaction transaction2 = new Transaction();

    ExecutorService threadPool = Executors.newFixedThreadPool(2);

    threadPool.submit(() -> {
      transaction1.read(data);
      transaction1.read(data);
      transaction1.commit();
    });

    threadPool.submit(() -> {
      transaction2.read(data);
      int value = transaction2.values.get(data.dataId);
      transaction2.write(data.dataId, value + 1);
      transaction2.commit();
    });

    threadPool.shutdown();
    threadPool.awaitTermination(1, TimeUnit.MINUTES);

    int initialValue = transaction1.initialState.get(data.dataId);
    int finalTransactionValue = transaction1.values.get(data.dataId);
    if (initialValue == 0 && finalTransactionValue == 1) {
      System.err.println(
          "Inconsistent Read anomaly occurred: initialValue is 0 and finalTransactionValue is 1");
    }
  }
}
