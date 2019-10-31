import org.junit.Test;

import java.text.MessageFormat;

public class MessageFormaterTest {
    @Test
    public void test() {
        //MessageFormat temp = new MessageFormat("{3,}, {1}");
        String format = MessageFormat.format("{0,,}, {1}", null, 1);
       System.out.println(format);
    }
}
