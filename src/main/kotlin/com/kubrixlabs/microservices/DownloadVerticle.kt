package com.kubrixlabs.microservices

import io.vertx.core.AbstractVerticle
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpClientOptions
import io.vertx.core.http.HttpMethod
import org.slf4j.LoggerFactory
import java.net.URL

class DownloadVerticle : AbstractVerticle() {
    val LOG = LoggerFactory.getLogger(DownloadVerticle::class.java)

    override fun start() {
        LOG.info("start")

        vertx.eventBus().consumer<Any>(BusAddress.DOWNLOADS.name) { message ->
            val url = URL(message.body().toString())
            LOG.info("received url: ${url}")

            val options = HttpClientOptions()
            options.setSsl(true)
            val client = vertx.createHttpClient(options)
            client.request(HttpMethod.GET, if (url.protocol == "https") 443 else 80, url.host, url.path) { response ->
                response.bodyHandler { body ->
                    vertx.eventBus().request<Buffer>(BusAddress.CONVERSIONS.name, Buffer.buffer(body.bytes)) { response ->
                        message.reply(response.result().body())
                    }
                }
            }.end()
        }
    }
}
