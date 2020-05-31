package com.kubrixlabs.microservices

import com.google.common.base.Splitter
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.Router
import org.slf4j.LoggerFactory

class WebServerVerticle : AbstractVerticle() {
    val LOG = LoggerFactory.getLogger(WebServerVerticle::class.java)

    override fun start(startPromise: Promise<Void>) {
        LOG.info("start")

        val router = Router.router(vertx)
        router.get("/convert/puml").handler { routingContext ->
            val query = routingContext.request().query()
            val queryMap: Map<String, String> = Splitter.on('&')
                .withKeyValueSeparator('=')
                .split(query)
            vertx.eventBus().request<Buffer>(BusAddress.DOWNLOADS.name,
                Buffer.buffer(queryMap.get("src"))) { response ->
                val buffer = response.result().body()
                routingContext.response()
                    .putHeader("content-type", "image/png")
                    .end(buffer)
            }
        }

        vertx.createHttpServer().requestHandler(router).listen(8888) { http ->
            if (http.succeeded()) {
                startPromise.complete()
                LOG.info("HTTP server started on port 8888")
            } else {
                startPromise.fail(http.cause());
            }
        }
    }
}
// http://52.29.9.109:8888/convert/puml?src=https://raw.githubusercontent.com/DBuret/journal/master/test.puml
