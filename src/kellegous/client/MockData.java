package kellegous.client;

import kellegous.client.Controller.Callback;
import kellegous.client.Controller.Response;

public class MockData {
  public static class Client implements Controller.Client {

    @Override
    public void loadAllRevisions(int n, Callback<Response> callback) {
    }

    @Override
    public void loadNewRevisions(double sinceRevision, Callback<Response> callback) {
    }
  }
}
