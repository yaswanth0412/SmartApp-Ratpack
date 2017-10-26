package com.ratpack.smartapp;

import ratpack.guice.Guice;
import ratpack.handling.Chain;
import ratpack.server.BaseDir;
import ratpack.server.RatpackServer;

public class MyApp {

  public static void main(String[] args) throws Exception {
	
    RatpackServer.start(s -> s
        .serverConfig(c -> c.baseDir(BaseDir.find()))
        .registry(Guice.registry(b -> b.module(MyModule.class)))
        .handlers(chain -> chain
        	.get("switches",SmartGetHandler.class)
        	.post("setLevel", SmartAppHandler.class)
            .prefix("static", nested -> nested.fileSystem("assets/images", Chain::files)) // Bind the /static app path to the src/ratpack/assets/images dir
            .all(ctx -> ctx.render("root handler!"))
        )
    );
  }
}						
