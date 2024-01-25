import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

public class TransactionTest {

  @Test
  public void testConcurrentTransactions() throws InterruptedException {
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
      transaction1.write(data.getDataId(), currentValue + 1);
      transaction2.commit();
    });

    threadPool.shutdown();
    threadPool.awaitTermination(1, TimeUnit.MINUTES);

    assertEquals(2, data.getValue());
  }
}
