package io.zrz.jnpm.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.zrz.jnpm.semver.ExactVersion;
import io.zrz.jnpm.semver.TagIdentifier;

@JsonIgnoreProperties(value = {
    "_id",
    "_attachments",
    "_rev"
})
public class NpmPackageMeta {

  public String name;
  public List<String> keywords;
  public JsonNode license;
  public String description;
  public String readme;
  public String readmeFilename;

  @JsonDeserialize(keyAs = ExactVersion.class)
  public Map<ExactVersion, NpmPackageVersionMeta> versions = new HashMap<>();
  public Map<String, String> time;
  public Map<String, Boolean> users;
  public List<String> homepage;
  public JsonNode maintainers;
  public JsonNode repository;
  public JsonNode bugs;

  @JsonProperty("dist-tags")
  @JsonDeserialize(keyAs = TagIdentifier.class, contentAs = ExactVersion.class)
  public Map<TagIdentifier, ExactVersion> distTags = new HashMap<>();

  private Map<String, JsonNode> values = new HashMap<>();

  /**
   * 
   */

  @JsonAnySetter
  public void set(String key, JsonNode value) {
    this.values.put(key, value);
  }

}
