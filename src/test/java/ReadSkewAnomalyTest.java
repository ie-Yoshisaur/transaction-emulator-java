import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ReadSkewAnomalyTest {
  public static void main(String arg[]) throws InterruptedException {
    final Data data1 = new Data(1, 0);
    final Data data2 = new Data(2, 0);
    final Transaction transaction1 = new Transaction();
    final Transaction transaction2 = new Transaction();

    ExecutorService threadPool = Executors.newFixedThreadPool(2);

    threadPool.submit(() -> {
      transaction1.read(data1);
      transaction1.read(data2);
      transaction1.commit();
    });

    threadPool.submit(() -> {
      transaction2.read(data1);
      int value1 = transaction2.values.get(data1.dataId);
      transaction2.read(data2);
      transaction2.write(data1.dataId, value1 + 1);
      int value2 = transaction2.values.get(data2.dataId);
      transaction2.write(data2.dataId, value2 + 1);
      transaction2.rollback();
    });

    threadPool.shutdown();
    threadPool.awaitTermination(1, TimeUnit.MINUTES);

    int finalTransaction1Value1 = transaction1.values.get(data1.dataId);
    int finalTransaction1Value2 = transaction1.values.get(data2.dataId);
    if (finalTransaction1Value1 == 0 && finalTransaction1Value2 == 1) {
      System.err.println(
          "Read Skew Anomaly occurred: finalTransaction1Value1 is 0 and finalTransaction1Value2 is 1");
    }
  }
}
