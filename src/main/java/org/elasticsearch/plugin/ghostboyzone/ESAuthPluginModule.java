package org.elasticsearch.plugin.ghostboyzone;

import org.elasticsearch.common.inject.AbstractModule;

public class ESAuthPluginModule extends AbstractModule {
	@Override  
    protected void configure() {  
        bind(ESAuthPluginAction.class).asEagerSingleton();  
    }  
}
