package org.techlyric.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.codec.http.HttpServerUpgradeHandler.UpgradeCodec;
import io.netty.handler.codec.http.HttpServerUpgradeHandler.UpgradeCodecFactory;
import io.netty.handler.codec.http2.Http2CodecUtil;
import io.netty.handler.codec.http2.Http2ServerUpgradeCodec;
import io.netty.util.AsciiString;

import javax.net.ssl.SSLContext;

/**
 * Sets up the Netty pipeline for the example server. Depending on the endpoint
 * config, sets up the pipeline for NPN or cleartext HTTP upgrade to HTTP/2.
 */
public final class Http2ServerInitializer extends ChannelInitializer<SocketChannel> {

	private SslContext sslCtx = null;
	private int maxHttpContentLength = 1024;

	private static final UpgradeCodecFactory upgradeCodecFactory = new UpgradeCodecFactory() {
        @Override
        public UpgradeCodec newUpgradeCodec(CharSequence protocol) {
            if (AsciiString.contentEquals(Http2CodecUtil.HTTP_UPGRADE_PROTOCOL_NAME, protocol)) {
                return new Http2ServerUpgradeCodec(new HelloWorldHttp2HandlerBuilder().build());
            } else {
                return null;
            }
        }
    };


	public Http2ServerInitializer(SslContext sslCtx) {
		this(sslCtx, 8 * 1024);
	}

	public Http2ServerInitializer(SslContext sslCtx, int maxHttpContentLength) {
		this.sslCtx = sslCtx;
		this.maxHttpContentLength = maxHttpContentLength;
	}

	@Override
	public void initChannel(SocketChannel ch) {
		if (sslCtx != null) {
			 ch.pipeline().addLast(sslCtx.newHandler(ch.alloc()), new Http2OrHttpHandler());
		} else {

		}
	}

    /**
     * Class that logs any User Events triggered on this channel.
     */
    private static class UserEventLogger extends ChannelInboundHandlerAdapter {
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
            System.out.println("User Event Triggered: " + evt);
            ctx.fireUserEventTriggered(evt);
        }
    }

}