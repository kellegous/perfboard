package kellegous.client.model;

import com.google.gwt.core.client.JavaScriptObject;

import kellegous.client.Xhr;
import kellegous.client.data.Callback;

public class RealClient implements Client {

  private final String m_branch;
  private final String m_baseUrl;

  public RealClient(String baseUrl, String branch) {
    assert !baseUrl.endsWith("/");
    m_branch = branch;
    m_baseUrl = baseUrl;
  }

  private static String urlForLoad(String baseUrl, String branch, int n) {
    return baseUrl + "/load/" + branch + "/" + n;
  }

  @Override
  public void load(int n, final Callback<LoadResponse> callback) {
    Xhr.getJson(urlForLoad(m_baseUrl, m_branch, n), new Xhr.Callback<JavaScriptObject>() {
      @Override
      public void didFail() {
        callback.didFail();
      }

      @Override
      public void didCallback(JavaScriptObject value) {
        // TODO(knorton): Assert that value looks like a LoadResponse.
        callback.didCallback(value.<LoadResponse> cast());
      }
    });
  }
}
