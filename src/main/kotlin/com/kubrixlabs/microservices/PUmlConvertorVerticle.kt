package com.kubrixlabs.microservices

import io.vertx.core.AbstractVerticle
import io.vertx.core.buffer.Buffer

import net.sourceforge.plantuml.SourceStringReader
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream

class PUmlConvertorVerticle : AbstractVerticle() {
    val LOG = LoggerFactory.getLogger(PUmlConvertorVerticle::class.java)

    override fun start() {
        LOG.info("start")

        vertx.eventBus().consumer<Any>(BusAddress.CONVERSIONS.name) { message ->
            val pumlString = message.body().toString()

            val reader = SourceStringReader(pumlString)
            val byteBuffer = ByteArrayOutputStream()
            reader.outputImage(byteBuffer)

            message.reply(Buffer.buffer(byteBuffer.toByteArray()))
        }
    }
}
// apt-get install graphviz
