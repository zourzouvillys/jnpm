package io.zrz.jnpm.repo;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.google.common.net.UrlEscapers;

import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;

public class NpmRemotePackageRepository {

  private String url;

  public NpmRemotePackageRepository(String url) {
    this.url = url;
  }

  /**
   * queries a specific tag.
   * 
   * @param name
   * @param tag
   * @return
   * 
   */

  @SneakyThrows(IOException.class)
  public Response query(String name, Optional<String> etag) {

    OkHttpClient client = new OkHttpClient().newBuilder().readTimeout(30, TimeUnit.SECONDS).build();

    Builder request = new Request.Builder()
        .url(String.format("%s/%s", url, (UrlEscapers.urlPathSegmentEscaper().escape(name))))
        .header("Accept", "text/json");

    if (etag.isPresent()) {
      request = request.header("If-None-Match", etag.get().toString());
    }

    return client.newCall(request.build()).execute();

  }

}
