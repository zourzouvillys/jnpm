package io.zrz.jnpm.tooling;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import io.zrz.jnpm.NpmObjectMapper;
import io.zrz.jnpm.model.NpmPackageMeta;
import io.zrz.jnpm.model.NpmPackageVersionMeta;

public class NpmPackageRef {

  public NpmPackageRef() {

  }

  public static NpmPackageRef fromDirectory(Path e) {

    Path packageJson = e.resolve("package.json");

    if (!Files.isRegularFile(packageJson)) {
      throw new IllegalArgumentException("missing package.json");
    }

    try {
      NpmPackageVersionMeta pkg = NpmObjectMapper.readPackageVersionMeta(packageJson);
      System.err.println(pkg);
    }
    catch (IOException e1) {
      // TODO Auto-generated catch block
      throw new RuntimeException(e1);
    }

    return new NpmPackageRef();

  }

}
