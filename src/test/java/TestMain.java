import com.whisper.agent.HotLoadWatch;

/**
 * @author little whisper
 * @date 2021/1/5 13:56
 */
public class TestMain {
    public static void main(String[] args) {
        HotLoadWatch.start("E:\\sbt-workplace\\comwhisepr\\src\\main\\resources\\com.whisepr-1.0-SNAPSHOT.jar", "E:\\sbt-workplace\\comwhisepr\\target\\script");
        new Thread(() -> {
            while(true) {
                User user = new User();
                System.out.println(user.getName());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
