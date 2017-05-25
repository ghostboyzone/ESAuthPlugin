package org.elasticsearch.plugin.ghostboyzone.http;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.network.NetworkService;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.util.BigArrays;
import org.elasticsearch.env.Environment;
import org.elasticsearch.http.HttpChannel;
import org.elasticsearch.http.HttpRequest;
import org.elasticsearch.http.netty.NettyHttpServerTransport;
import org.elasticsearch.plugin.ghostboyzone.config.ESAuthConfig;
import org.elasticsearch.plugin.ghostboyzone.util.ContentBuilder;

public class GhostHttpServerTransport extends NettyHttpServerTransport{
	private final ESAuthConfig config;
	
	@Inject
	public GhostHttpServerTransport(Settings settings, NetworkService networkService, BigArrays bigArrays) {
		super(settings, networkService, bigArrays);
		Environment environment = new Environment(settings);
		this.config = ESAuthConfig.getInstance(environment);
	}

	@Override
	protected void dispatchRequest(final HttpRequest request, final HttpChannel channel) {
		logger.info("GhostHttpServerTransport In dispatch");
		
		boolean permisAuth = ContentBuilder.checkPermission(request, channel, this.config, logger);
		if (!permisAuth) {
			return;
		}
		boolean ipAuth = ContentBuilder.checkIpPermission(request, channel, this.config, logger);
		if (!ipAuth) {
			return;
		}
		
		super.dispatchRequest(request, channel);
	}
	
}
