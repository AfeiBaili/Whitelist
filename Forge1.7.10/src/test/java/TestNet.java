import cn.afeibaili.whitelist.network.ListNetwork;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * @author AfeiBaili
 * @version 2026/9/5 20:40
 */

public class TestNet {
    @Test
    public void test1() {
        try {
            InputStream stream = ListNetwork.getInputStream(new URL(ListNetwork.WHITELIST_URI));
            List<String> list = ListNetwork.readLines(stream);

            System.out.println(list);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
