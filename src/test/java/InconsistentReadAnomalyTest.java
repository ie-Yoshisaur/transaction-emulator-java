public class InconsistentReadAnomalyTest {
  public static void main(String arg[]) throws InterruptedException {
    final Data data = new Data(1, 0);
    final Transaction transaction1 = new Transaction();
    final Transaction transaction2 = new Transaction();

    Thread thread1 = new Thread(() -> {
      transaction1.read(data);
      transaction1.read(data);
      transaction1.commit();
    });

    Thread thread2 = new Thread(() -> {
      transaction2.read(data);
      int value = 0;
      if (transaction2.values.containsKey(data.dataId)) {
        value = transaction2.values.get(data.dataId);
      }
      transaction2.write(data.dataId, value + 1);
      transaction2.commit();
    });

    thread1.start();
    thread2.start();

    thread1.join();
    thread2.join();

    int initialValue = transaction1.initialState.get(data.dataId);
    int finalTransactionValue = transaction1.values.get(data.dataId);
    if (initialValue == 0 && finalTransactionValue == 1) {
      System.err.println(
          "Inconsistent Read anomaly occurred: initialValue is 0 and finalTransactionValue is 1");
    }
  }
}
