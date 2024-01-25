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
      int value = transaction2.getValue(data.getDataId());
      transaction2.write(data.getDataId(), value + 1);
      transaction2.commit();
    });

    threadPool.shutdown();
    threadPool.awaitTermination(1, TimeUnit.MINUTES);

    int initialValue = transaction1.getInitialState().get(data.getDataId());
    int finalTransactionValue = transaction1.getValue(data.getDataId());
    if (initialValue == 0 && finalTransactionValue == 1) {
      System.err.println(
          "Inconsistent Read anomaly occurred: initialValue is 0 and finalTransactionValue is 1");
    }
  }
}
