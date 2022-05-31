package monster.nor.prunes.logger

import org.slf4j.Logger
import org.slf4j.LoggerFactory

var debugTraceMode = false

val <T : Any> T.log: Logger
    get() = LoggerFactory.getLogger(javaClass.name)

fun Logger.error(e: Throwable) {
    log.error(e.stackTraceToString())
}

fun Logger.trace(e: Throwable) {
    if(debugTraceMode)
        error(e)
}
