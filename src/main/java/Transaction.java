import java.util.HashMap;
import java.util.Map;

public class Transaction {
  public Map<Integer, Data> dataMap;
  public Map<Integer, Integer> values;
  public Map<Integer, Integer> initialState;
  public boolean isCheckpointSet;

  public Transaction() {
    this.dataMap = new HashMap<>();
    this.values = new HashMap<>();
    this.initialState = new HashMap<>();
    this.isCheckpointSet = false;
  }

  public void read(Data data) {
    if (!this.isCheckpointSet) {
      int dataId = data.dataId;
      this.dataMap.put(dataId, data);
      int value = data.value;
      this.values.put(dataId, value);
      if (!this.initialState.containsKey(dataId)) {
        this.initialState.put(dataId, value);
      }
    }
  }

  public void write(int dataId, int value) {
    if (!this.isCheckpointSet) {
      if (this.values.containsKey(dataId) && this.dataMap.containsKey(dataId)) {
        this.values.put(dataId, value);
        Data data = this.dataMap.get(dataId);
        data.value = value;
      }
    }
  }

  public void rollback() {
    for (Map.Entry<Integer, Integer> entry : this.initialState.entrySet()) {
      int dataId = entry.getKey();
      int initialValue = entry.getValue();
      if (this.dataMap.containsKey(dataId)) {
        Data data = this.dataMap.get(dataId);
        data.value = initialValue;
      }
      this.values.put(dataId, initialValue);
    }
    isCheckpointSet = true;
  }

  public void commit() { isCheckpointSet = true; }
}
