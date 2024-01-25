import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class WriteSkewAnomalyTest {
  public static void main(String arg[]) throws InterruptedException {
    final Data data1 = new Data(1, 0);
    final Data data2 = new Data(2, 0);
    final Transaction transaction1 = new Transaction();
    final Transaction transaction2 = new Transaction();

    ExecutorService threadPool = Executors.newFixedThreadPool(2);

    threadPool.submit(() -> {
      transaction1.read(data1);
      transaction1.read(data2);
      int value = transaction1.getValue(data1.getDataId());
      transaction1.write(data2.getDataId(), value + 1);
      transaction1.commit();
    });

    threadPool.submit(() -> {
      transaction2.read(data2);
      transaction2.read(data1);
      int value = transaction2.getValue(data2.getDataId());
      transaction2.write(data1.getDataId(), value + 1);
      transaction2.commit();
    });

    threadPool.shutdown();
    threadPool.awaitTermination(1, TimeUnit.MINUTES);

    int finalTransaction1Value1 = transaction1.getValue(data1.getDataId());
    int finalTransaction1Value2 = transaction1.getValue(data2.getDataId());
    if (finalTransaction1Value1 == 1 && finalTransaction1Value2 == 1) {
      System.err.println(
          "Write Skew Anomaly occurred: finalTransaction1Value1 is 1 and finalTransaction1Value2 is 1");
    }
  }
}
