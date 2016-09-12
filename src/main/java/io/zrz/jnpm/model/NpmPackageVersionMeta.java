package io.zrz.jnpm.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.zrz.jnpm.semver.NpmExactVersion;
import io.zrz.jnpm.semver.NpmVersionRange;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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

  @ToString
  @EqualsAndHashCode
  public static class DistInfo {
    public String shasum;
    public String tarball;
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

  @JsonDeserialize(contentAs = NpmVersionRange.class)
  public Map<String, NpmVersionRange> dependencies = new HashMap<>();
  @JsonDeserialize(contentAs = NpmVersionRange.class)
  public Map<String, NpmVersionRange> devDependencies = new HashMap<>();
  @JsonDeserialize(contentAs = NpmVersionRange.class)
  public Map<String, NpmVersionRange> peerDependencies = new HashMap<>();
  @JsonDeserialize(contentAs = NpmVersionRange.class)
  public Map<String, NpmVersionRange> optionalDependencies = new HashMap<>();

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

  public String toString() {
    return String.format("{%s@%s}", name, version);
  }

  public NpmExactVersion version() {
    return NpmExactVersion.fromString(version);
  }

}
