package io.zrz.jnpm.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.zrz.jnpm.semver.ExactVersion;
import io.zrz.jnpm.semver.VersionRange;

@JsonIgnoreProperties(value = {
  "_id",
  "_npmOperationalInternal",
  "_from",
  "_npmVersion",
  "_shasum",
  "_nodeVersion",
  "_defaultsLoaded",
  "_resolved",
  "_engineSupported",
  "_npmUser" })
public class NpmPackageVersionMeta {

  public JsonNode license;
  public String version;
  public String main;
  public List<String> homepage;
  public String description;
  public String name;

  //
  public List<String> keywords;

  @Value.Immutable
  public interface DistInfo {
    String shasum();
    String tarball();
  }

  public JsonNode author;
  public DistInfo dist;
  public JsonNode directories;
  public JsonNode repository;
  public JsonNode bugs;
  public Map<String, JsonNode> scripts;
  public JsonNode licenses;
  public JsonNode engines;
  public JsonNode deprecated;
  public JsonNode files;
  public JsonNode browserify;

  @JsonDeserialize(contentAs = VersionRange.class)
  public Map<String, VersionRange> dependencies = new HashMap<>();
  @JsonDeserialize(contentAs = VersionRange.class)
  public Map<String, VersionRange> devDependencies = new HashMap<>();
  @JsonDeserialize(contentAs = VersionRange.class)
  public Map<String, VersionRange> peerDependencies = new HashMap<>();
  @JsonDeserialize(contentAs = VersionRange.class)
  public Map<String, VersionRange> optionalDependencies = new HashMap<>();

  public JsonNode maintainers;

  private Map<String, JsonNode> values = new HashMap<>();

  /**
   * 
   */

  @JsonAnySetter
  public void set(String key, JsonNode value) {
    this.values.put(key, value);
  }

  /**
   * 
   */

  public String id() {
    return String.format("%s@%s", name, version);
  }

  @Override
  public String toString() {
    return String.format("{%s@%s}", name, version);
  }

  public ExactVersion version() {
    return ExactVersion.fromString(version);
  }

}
