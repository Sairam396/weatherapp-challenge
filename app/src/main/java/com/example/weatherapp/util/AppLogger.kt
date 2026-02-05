package com.example.weatherapp.util

/**
 * App code (on device) can use android.util.Log
 * JVM unit tests cannot access android.util.Log
 * So I implement a safe logger:
 *   - Try to call android.util.Log via reflection (only works on Android runtime)
 *   - If not available (unit tests), fallback to println
 */
object AppLogger {
    private const val TAG = "WeatherApp"

    fun d(msg: String) = log("d", msg, null)
    fun i(msg: String) = log("i", msg, null)
    fun w(msg: String) = log("w", msg, null)
    fun e(msg: String, tr: Throwable? = null) = log("e", msg, tr)

    private fun log(level: String, msg: String, tr: Throwable?) {
        val message = "$TAG: $msg"

        val androidLogged = runCatching {
            val logClass = Class.forName("android.util.Log")
            val method = when (level) {
                "d" -> logClass.getMethod("d", String::class.java, String::class.java)
                "i" -> logClass.getMethod("i", String::class.java, String::class.java)
                "w" -> logClass.getMethod("w", String::class.java, String::class.java)
                else -> logClass.getMethod("e", String::class.java, String::class.java, Throwable::class.java)
            }

            if (level == "e") {
                method.invoke(null, TAG, msg, tr)
            } else {
                method.invoke(null, TAG, msg)
            }
            true
        }.getOrDefault(false)

        // JVM unit tests fallback
        if (!androidLogged) {
            if (tr != null) {
                println("$message\n${tr.stackTraceToString()}")
            } else {
                println(message)
            }
        }
    }
}
