package io.zrz.jnpm.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.immutables.value.Value;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.zrz.jnpm.semver.ExactVersion;
import io.zrz.jnpm.semver.TagIdentifier;

@JsonIgnoreProperties(value = {
  "_id",
  "_attachments",
  "_rev"
})
@Value.Immutable
@VavrEncodingEnabled
@Value.Style(overshadowImplementation = true, deepImmutablesDetection = true)
@JsonDeserialize(builder = ImmutableNpmPackageMeta.Builder.class)
@JsonSerialize
public interface NpmPackageMeta {

  String name();

  List<String> keywords();

  JsonNode license();

  String description();

  String readme();

  String readmeFilename();

  // @JsonDeserialize(keyAs = ExactVersion.class)
  Map<ExactVersion, NpmPackageVersionMeta> versions();

  Map<String, String> time();

  Map<String, Boolean> users();

  List<String> homepage();

  JsonNode maintainers();

  JsonNode repository();

  JsonNode bugs();

  ObjectNode scripts();

  @JsonDeserialize(keyAs = ExactVersion.class)
  String version();

  String main();

  @JsonProperty("dist-tags")
  @JsonDeserialize(keyAs = TagIdentifier.class, contentAs = ExactVersion.class)
  Map<TagIdentifier, ExactVersion> distTags = new HashMap<>();

  // private Map<String, JsonNode> values = new HashMap<>();

  /**
   * 
   */

  // @JsonAnySetter
  // default void set(String key, JsonNode value) {
  // this.values.put(key, value);
  // }

}
