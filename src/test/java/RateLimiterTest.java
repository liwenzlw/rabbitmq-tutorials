import com.google.common.util.concurrent.RateLimiter;
import org.junit.Test;

public class RateLimiterTest {
    @Test
    public void test() throws Exception{
        long startTime = System.currentTimeMillis();

        RateLimiter limiter = RateLimiter.create(10);// 100ms
        if (limiter.tryAcquire(21))// 获取21个令牌
            System.out.println(System.currentTimeMillis() - startTime); // 过去2000 毫秒

        Thread.currentThread().sleep(1001);
        limiter.tryAcquire(1);//代码3
        System.out.println(System.currentTimeMillis() - startTime);

        limiter.tryAcquire(5);
        System.out.println(System.currentTimeMillis() - startTime);
    }
}
