package io.zrz.jnpm.repo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.NotImplementedException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.common.base.Preconditions;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import io.zrz.jnpm.NpmObjectMapper;
import io.zrz.jnpm.NpmPackageRepository;
import io.zrz.jnpm.model.NpmPackageMeta;
import io.zrz.jnpm.model.NpmPackageVersionMeta;
import io.zrz.jnpm.semver.ExactVersion;
import io.zrz.jnpm.semver.VersionRange;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * adapter which uses filesystem cache.
 * 
 * avoids parsing any unneeded objects.
 * 
 * @author theo
 *
 */

public class NpmRepoCache implements NpmPackageRepository {

  private final NpmRemotePackageRepository repo;
  private final Path cache;

  private Set<String> tried = new HashSet<>();

  public NpmRepoCache(NpmRemotePackageRepository repo, Path path) {
    this.repo = repo;
    this.cache = path;
  }

  /**
   * 
   */

  @Override
  public NpmPackageMeta query(String name) {
    try {
      return NpmObjectMapper.readPackageMeta(fetch(name));
    }
    catch (Exception ex) {
      throw new RuntimeException(String.format("While parsing package.json for '%s'", name), ex);
    }
  }

  private byte[] fetch(String name) {
    try {

      // lookup
      final Path cachefile = cache.resolve(Paths.get(serverName(), name, ".cache.json"));

      final byte[] data;

      if (Files.exists(cachefile)) {

        Duration age = Duration.between(Files.getLastModifiedTime(cachefile).toInstant(), Instant.now());

        if ((age.toDays() < 7) || tried.contains(name)) {
          return Files.readAllBytes(cachefile);
        }

        tried.add(name);

        Optional<String> etag = etag(cachefile);

        // if we fail to refresh, then use stale data.
        try {

          Response body = repo.query(name, etag);

          if (body.code() == 304) {
            Files.setLastModifiedTime(cachefile, FileTime.fromMillis(System.currentTimeMillis()));
            return Files.readAllBytes(cachefile);
          }
          else if (body.code() == 200) {
            data = body.body().bytes();
            Files.write(cachefile, add(body, data), StandardOpenOption.TRUNCATE_EXISTING);
          }
          else {
            throw new RuntimeException("Invalid HTTP status refreshing: " + body.code());
          }

        }
        catch (Exception ex) {
          // use the old one for now.
          ex.printStackTrace();
          return Files.readAllBytes(cachefile);
        }

      }
      else {

        Response body = repo.query(name, Optional.empty());

        if (body.code() != 200) {
          throw new RuntimeException("Invalid HTTP status fetching: " + body.code());
        }

        data = body.body().bytes();

        Files.createDirectories(cachefile.getParent());
        Files.write(cachefile, add(body, data), StandardOpenOption.CREATE_NEW);

      }

      return data;

    }
    catch (Exception ex) {
      throw new RuntimeException(String.format("While fetching package.json for '%s'", name), ex);
    }
  }

  private String serverName() {
    return "registry.npmjs.org";
  }

  private Optional<String> etag(Path cachefile) {
    try {
      JsonParser jp = NpmObjectMapper.createParser(Files.readAllBytes(cachefile));

      JsonToken current = jp.nextToken();

      Preconditions.checkArgument(current == JsonToken.START_OBJECT, current);

      //
      while (jp.nextToken() != JsonToken.END_OBJECT) {

        String fieldName = jp.getCurrentName();
        // move from field name to field value
        current = jp.nextToken();

        if (fieldName.equals("_etag")) {
          return Optional.of(jp.getValueAsString());
        }
        else {
          jp.skipChildren();
        }
      }

      return Optional.empty();
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }

  }

  private byte[] add(Response body, byte[] data) {
    try {
      if (body.header("ETag") == null) {
        return data;
      }

      JsonParser jp = NpmObjectMapper.createParser(data);

      JsonFactory f = new JsonFactory();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      JsonGenerator jg = f.createGenerator(baos);

      JsonToken current = jp.nextToken();
      Preconditions.checkArgument(current == JsonToken.START_OBJECT, current);

      jg.copyCurrentEvent(jp);

      jg.writeStringField("_etag", body.header("ETag"));
      //
      while (jp.nextToken() != null) {
        jg.copyCurrentEvent(jp);
      }

      jg.close();

      return baos.toByteArray();
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }

  }

  @Override
  public ExactVersion distTag(String name, String tag) {

    try {
      JsonParser jp = NpmObjectMapper.createParser(fetch(name));

      JsonToken current = jp.nextToken();

      Preconditions.checkArgument(current == JsonToken.START_OBJECT, current);

      //
      while (jp.nextToken() != JsonToken.END_OBJECT) {

        String fieldName = jp.getCurrentName();
        // move from field name to field value
        current = jp.nextToken();

        if (fieldName.equals("dist-tags")) {

          if (current == JsonToken.START_OBJECT) {

            while (jp.nextToken() != JsonToken.END_OBJECT) {

              String ttag = jp.getCurrentName();

              // move from field name to field value
              current = jp.nextToken();

              // read the record into a tree model,
              // this moves the parsing position to the end of it

              if (ttag.equals(tag)) {
                return ExactVersion.fromString(jp.getText());
              }

              jp.skipChildren();

            }

          }
          else {
            System.out.println("Error: records should be an array: skipping.");
            jp.skipChildren();
          }

        }
        else {
          jp.skipChildren();
        }
      }

      throw null;

    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }

  }

  @Override
  public List<ExactVersion> query(String name, VersionRange versionSpec) {
    try {
      Objects.requireNonNull(versionSpec);

      JsonParser jp = NpmObjectMapper.createParser(fetch(name));

      JsonToken current = jp.nextToken();

      Preconditions.checkArgument(current == JsonToken.START_OBJECT, current);

      List<ExactVersion> results = new ArrayList<>();

      //
      while (jp.nextToken() != JsonToken.END_OBJECT) {

        String fieldName = jp.getCurrentName();
        // move from field name to field value
        current = jp.nextToken();

        if (fieldName.equals("versions")) {

          if (current == JsonToken.START_OBJECT) {

            while (jp.nextToken() != JsonToken.END_OBJECT) {

              ExactVersion version = ExactVersion.fromString(jp.getCurrentName());

              // move from field name to field value
              current = jp.nextToken();

              // read the record into a tree model,
              // this moves the parsing position to the end of it

              if (versionSpec.satisfiedBy(version)) {
                results.add(version);
              }

              jp.skipChildren();

            }

          }
          else {
            System.out.println("Error: records should be an array: skipping.");
            jp.skipChildren();
          }

        }
        else {
          jp.skipChildren();
        }
      }

      return results;

    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }

  }

  @Override
  public NpmPackageVersionMeta query(String name, ExactVersion version) {
    try {
      JsonParser jp = NpmObjectMapper.createParser(fetch(name));

      JsonToken current = jp.nextToken();

      Preconditions.checkArgument(current == JsonToken.START_OBJECT, current);

      //
      while (jp.nextToken() != JsonToken.END_OBJECT) {

        String fieldName = jp.getCurrentName();
        // move from field name to field value
        current = jp.nextToken();

        if (fieldName.equals("versions")) {

          if (current == JsonToken.START_OBJECT) {

            while (jp.nextToken() != JsonToken.END_OBJECT) {

              // move from field name to field value
              if (ExactVersion.fromString(jp.getCurrentName()).equals(version)) {
                current = jp.nextToken();
                return jp.readValueAs(NpmPackageVersionMeta.class);
              }

              current = jp.nextToken();
              jp.skipChildren();

            }

          }
          else {
            System.out.println("Error: records should be an array: skipping.");
            jp.skipChildren();
          }

        }
        else {
          jp.skipChildren();
        }
      }

    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }

    throw new IllegalArgumentException(String.format("%s@%s", name, version));

  }

  @Override
  public Path dist(String packageName, ExactVersion packageVersion) {
    try {
      NpmPackageVersionMeta info = query(packageName, packageVersion);

      final Path tarball = this.cache.resolve(Paths.get(packageName, packageVersion.toString(), "package.tgz"));

      if (!Files.exists(tarball)) {

        String url = info.dist.tarball();

        // fetch the body.

        OkHttpClient client = new OkHttpClient().newBuilder().readTimeout(30, TimeUnit.SECONDS).build();

        Builder request =
          new Request.Builder()
            .url(url);

        //
        Response response = client.newCall(request.build()).execute();

        ResponseBody body = response.body();
        long contentLength = body.contentLength();
        BufferedSource source = body.source();

        Files.createDirectories(tarball.getParent());

        BufferedSink sink = Okio.buffer(Okio.sink(tarball.toFile()));

        long read = 0;

        while ((read = (source.read(sink.buffer(), 1024))) != -1) {

          System.err.print(".");
          System.err.flush();

        }

        sink.writeAll(source);
        sink.flush();
        sink.close();

      }

      // check shasum

      HashCode hashCode = com.google.common.io.Files.hash(tarball.toFile(), Hashing.sha1());

      if (hashCode.toString().equals(info.dist.shasum())) {
        return tarball;
      }

    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }

    throw new NotImplementedException("");

  }

}
