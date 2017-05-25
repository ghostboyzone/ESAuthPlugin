package org.elasticsearch.plugin.ghostboyzone;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentBuilderString;
import org.elasticsearch.env.Environment;
import org.elasticsearch.plugin.ghostboyzone.config.ESAuthConfig;
import org.elasticsearch.plugin.ghostboyzone.util.ContentBuilder;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestStatus; 

public class ESAuthPluginAction extends BaseRestHandler{
    
    private final ESAuthConfig config;
    
    @Inject
	public ESAuthPluginAction(Settings settings, RestController controller, Client client) {
		super(settings, controller, client);
		
		Environment environment = new Environment(settings);
	    this.config = ESAuthConfig.getInstance(environment);
	      
		controller.registerHandler(RestRequest.Method.GET, "/_auth/config_reload", this);
		controller.registerHandler(RestRequest.Method.GET, "/_auth/config_show", this);
	}

	@Override
	protected void handleRequest(final RestRequest request, final RestChannel channel, Client client) throws Exception {
		
		// logger.info("Path:" + request.path() + "uri:" + request.uri());
		
		XContentBuilder builder = ContentBuilder.restContentBuilder(request);
		
		if (request.path().equals("/_auth/config_reload")) {
			this.config.reloadConfig();
			
			builder.startObject()  
	        .field(new XContentBuilderString("status"), RestStatus.OK)  
	        .field(new XContentBuilderString("message"),  
	                "config reload success")  
	        .endObject();
			channel.sendResponse(  
	                new BytesRestResponse(RestStatus.OK, builder));
		} else if (request.path().equals("/_auth/config_show")) {
			builder.startObject() 
			.field("open_auth", this.config.getSet().getAsBoolean("open_auth", false))
			.field("username", config.getSet().get("username", "").trim())
			.field("password", config.getSet().get("password", "").trim())
			
			.field("open_ip_auth", this.config.getSet().getAsBoolean("open_ip_auth", false))
			.field("ip_auth_list", this.config.getSet().getAsArray("ip_auth_list", new String[0]))
	        .endObject();
			channel.sendResponse(  
	                new BytesRestResponse(RestStatus.OK, builder));
		}
		
		//super.handleRequest(request, channel);
	}
	
}
