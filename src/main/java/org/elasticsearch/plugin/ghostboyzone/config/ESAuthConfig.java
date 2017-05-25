package org.elasticsearch.plugin.ghostboyzone.config;

import java.nio.file.Path;
import org.elasticsearch.env.Environment;
import org.elasticsearch.common.settings.Settings;

public class ESAuthConfig {
	private Settings customSettings;
	private final Environment env;
	
	private static ESAuthConfig instance = null;
	
	public static ESAuthConfig getInstance(Environment env) {
		if (instance == null) {
			instance = new ESAuthConfig(env);
		}
		return instance;	
	}
	
    private ESAuthConfig(Environment env) {
    	this.env = env;
        this.loadConfig();
    }
    
    public Settings getSet() {
    	return customSettings;
    }
    
    private void loadConfig() {
    	Path path = this.env.configFile().resolve("auth.yml");
        this.customSettings = Settings.builder().loadFromPath(path).build();
    }
    
    public void reloadConfig() {
    	this.loadConfig();
    	return;
    }
}