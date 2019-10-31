package io.zrz.jnpm.tooling;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class UpdateDownstreams {

  public static void main(String[] args) throws IOException {

    Files.list(Paths.get("/Users/theo/git/auth0"))
      .map(e -> NpmPackageRef.fromDirectory(e))
      .forEach(System.err::println);

  }

}
