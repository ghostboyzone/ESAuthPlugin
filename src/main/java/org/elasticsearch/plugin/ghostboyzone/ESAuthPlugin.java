package org.elasticsearch.plugin.ghostboyzone;

import java.util.Collection;
import java.util.Collections;

import org.elasticsearch.common.inject.Module;
import org.elasticsearch.http.HttpServerModule;
import org.elasticsearch.plugin.ghostboyzone.http.GhostHttpServerTransport;
import org.elasticsearch.plugins.Plugin;

public class ESAuthPlugin extends Plugin {
  
  @Override
  public String name() {
    return "ESAuthPlugin";
  }

  @Override
  public String description() {
    return "ESAuthPlugin";
  }
  
  
  @Override  
  public Collection<Module> nodeModules() {  
	  //加入自定义处理模块
      return Collections.<Module> singletonList(new ESAuthPluginModule());  
  }
  
  public void onModule(final HttpServerModule module) {
	  module.setHttpServerTransport(GhostHttpServerTransport.class, name());
  }
  

}