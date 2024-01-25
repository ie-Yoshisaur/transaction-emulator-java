import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TransactionTest {
  public static void main(String arg[]) throws InterruptedException {
    final Data data = new Data(1, 0);
    final Transaction transaction1 = new Transaction();
    final Transaction transaction2 = new Transaction();

    ExecutorService threadPool = Executors.newFixedThreadPool(2);

    threadPool.submit(() -> {
      transaction1.read(data);
      int currentValue = transaction1.getValue(data.getDataId());
      transaction1.write(data.getDataId(), currentValue + 1);
      transaction1.commit();
    });

    threadPool.submit(() -> {
      transaction2.read(data);
      int currentValue = transaction2.getValue(data.getDataId());
      transaction2.write(data.getDataId(), currentValue + 1);
      transaction2.commit();
    });

    threadPool.shutdown();
    threadPool.awaitTermination(1, TimeUnit.MINUTES);

    assert 2 == data.getValue();
    System.err.println("data is not 2");
  }
}
