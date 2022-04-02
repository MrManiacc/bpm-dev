package com.github.bpm.util

import com.github.bpm.Bpm
import com.github.bpmapi.BpmApi
import org.apache.logging.log4j.LogManager

internal val logger = LogManager.getLogger(Bpm.id)

internal fun info(supplier: () -> String) = logger.info(supplier)
internal fun warn(supplier: () -> String) = logger.warn(supplier)
internal fun debug(supplier: () -> String) = logger.debug(supplier)
internal fun error(supplier: () -> String) = logger.error(supplier)
internal fun trace(supplier: () -> String) = logger.trace(supplier)
