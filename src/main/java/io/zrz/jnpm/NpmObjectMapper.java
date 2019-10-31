package io.zrz.jnpm;

import java.io.IOException;
import java.nio.file.Path;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.zrz.jnpm.model.NpmPackageMeta;
import io.zrz.jnpm.model.NpmPackageVersionMeta;

public class NpmObjectMapper {

  private static final NpmObjectMapper INSTANCE = new NpmObjectMapper();

  private final ObjectMapper mapper = new ObjectMapper();

  public NpmObjectMapper() {
    this.mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
  }

  public static NpmObjectMapper getInstance() {
    return INSTANCE;
  }

  public static NpmPackageVersionMeta readPackageVersionMeta(byte[] bytes)
      throws JsonParseException,
      JsonMappingException,
      IOException {
    return INSTANCE.mapper.readValue(bytes, NpmPackageVersionMeta.class);
  }

  public static NpmPackageVersionMeta readPackageVersionMeta(Path path)
      throws JsonParseException,
      JsonMappingException,
      IOException {
    return INSTANCE.mapper.readValue(path.toFile(), NpmPackageVersionMeta.class);
  }

  public static NpmPackageMeta readPackageMeta(Path packageJson)
      throws JsonParseException,
      JsonMappingException,
      IOException {
    return INSTANCE.mapper.readValue(packageJson.toFile(), NpmPackageMeta.class);
  }

  public static NpmPackageMeta readPackageMeta(byte[] bytes)
      throws JsonParseException,
      JsonMappingException,
      IOException {
    return INSTANCE.mapper.readValue(bytes, NpmPackageMeta.class);
  }

  public static byte[] write(NpmPackageMeta data) throws JsonProcessingException {
    return INSTANCE.mapper.writeValueAsBytes(data);
  }

  public static JsonParser createParser(byte[] fetch) throws JsonParseException, IOException {
    JsonFactory jsonfactory = new JsonFactory(INSTANCE.mapper);
    return jsonfactory.createParser(fetch);
  }

}
