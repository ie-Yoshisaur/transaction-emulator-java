import java.util.HashMap;
import java.util.Map;

public class Transaction {
  private Map<Integer, Data> dataMap;
  private Map<Integer, Integer> values;
  private Map<Integer, Integer> initialState;
  private boolean isCheckpointSet;

  public Transaction() {
    this.dataMap = new HashMap<>();
    this.values = new HashMap<>();
    this.initialState = new HashMap<>();
    this.isCheckpointSet = false;
  }

  public void read(Data data) {
    if (!this.isCheckpointSet) {
      int dataId = data.getDataId();
      this.dataMap.put(dataId, data);
      int value = data.getValue();
      this.values.put(dataId, value);
      if (!this.initialState.containsKey(dataId)) {
        this.initialState.put(dataId, value);
      }
    }
  }

  public void write(int dataId, int value) {
    if (!this.isCheckpointSet) {
      if (this.values.containsKey(dataId) && this.dataMap.containsKey(dataId)) {
        this.setValue(dataId, value);
        Data data = this.dataMap.get(dataId);
        data.setValue(value);
      }
    }
  }

  public void rollback() {
    for (Map.Entry<Integer, Integer> entry : this.initialState.entrySet()) {
      int dataId = entry.getKey();
      int initialValue = entry.getValue();
      if (this.dataMap.containsKey(dataId)) {
        Data data = this.dataMap.get(dataId);
        data.setValue(initialValue);
      }
      this.values.put(dataId, initialValue);
    }
    isCheckpointSet = true;
  }

  public void commit() { isCheckpointSet = true; }

  public Integer getValue(int dataId) { return values.get(dataId); }

  public void setValue(int dataId, int value) { values.put(dataId, value); }

  public Map<Integer, Integer> getInitialState() { return initialState; }
}
