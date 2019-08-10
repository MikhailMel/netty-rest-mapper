package ru.scratty.nettyrestmapper

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpRequestDecoder
import io.netty.handler.codec.http.HttpResponseEncoder
import io.netty.handler.codec.http.cors.CorsConfigBuilder
import io.netty.handler.codec.http.cors.CorsHandler
import org.slf4j.LoggerFactory
import ru.scratty.nettyrestmapper.parameter.parser.ParameterParsersHandler

class Server(
    private val port: Int,
    private val httpMethodsHandlers: List<HttpMethodHandler>,
    private val parameterParsersHandler: ParameterParsersHandler
) {

    companion object {
        private val log = LoggerFactory.getLogger(Server::class.java)
    }

    private lateinit var channelFuture: ChannelFuture

    constructor(port: Int, controllerHandler: ControllerHandler) : this(
        port,
        controllerHandler.httpMethodsHandlers,
        ParameterParsersHandler(emptyList())
    )

    constructor(
        port: Int,
        controllerHandler: ControllerHandler,
        parameterParsersHandler: ParameterParsersHandler
    ) : this(port, controllerHandler.httpMethodsHandlers, parameterParsersHandler)

    fun startServer() {
        val corsConfig = CorsConfigBuilder.forAnyOrigin()
            .allowNullOrigin()
            .allowCredentials()
            .build()

        val workerCount = Runtime.getRuntime().availableProcessors() * 2
        val workerGroup = NioEventLoopGroup(workerCount)

        val server = ServerBootstrap()
            .group(workerGroup)
            .channel(NioServerSocketChannel::class.java)
            .childHandler(object : ChannelInitializer<Channel>() {
                public override fun initChannel(ch: Channel) {
                    ch.pipeline()
                        .addLast(HttpResponseEncoder())
                        .addLast(HttpRequestDecoder())
                        .addLast(HttpObjectAggregator(Int.MAX_VALUE))
                        .addLast(CorsHandler(corsConfig))
                        .addLast(HttpMappingHandler(httpMethodsHandlers, parameterParsersHandler.parameterParsers))
                }
            })
            .option(ChannelOption.SO_BACKLOG, 500)
            .childOption(ChannelOption.SO_KEEPALIVE, true)

        channelFuture = server.bind(port).sync()

        log.info("##########################")
        log.info("##### SERVER STARTED #####")
        log.info("##########################")
    }

    fun stopServer() {
        channelFuture.channel().closeFuture().sync()
    }
}