package com.github.bpmapi.util

import com.github.bpmapi.BpmApi
import org.apache.logging.log4j.LogManager

internal val logger = LogManager.getLogger(BpmApi.id)

internal inline fun info(supplier: () -> String) = logger.info(supplier())
internal inline fun warn(supplier: () -> String) = logger.warn(supplier())
internal inline fun debug(supplier: () -> String) = logger.debug(supplier())
internal inline fun error(supplier: () -> String) = logger.error(supplier())
internal inline fun trace(supplier: () -> String) = logger.trace(supplier())
