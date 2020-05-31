package com.kubrixlabs.microservices

import io.vertx.core.AbstractVerticle

class MainVerticle : AbstractVerticle(){
    override fun start() {
        vertx.deployVerticle(DownloadVerticle())
        vertx.deployVerticle(PUmlConvertorVerticle())
        vertx.deployVerticle(WebServerVerticle())
    }
}
