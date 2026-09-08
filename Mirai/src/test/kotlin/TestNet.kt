import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.test.Test

/**
 * 网络测试
 *
 * @author AfeiBaili
 * @version 2026/9/5 01:17
 */

class TestNet {
    @Test
    fun test1() {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        println(LocalDateTime.now().format(formatter))
    }
}