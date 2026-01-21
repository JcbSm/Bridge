import com.github.jcbsm.bridge.util.ChatRelayFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ChatRelayFormatterTests {

    @Test
    @DisplayName("parseMentions")
    public void parseMentionsTest(){
        System.out.println(ChatRelayFormatter.parseMentions("Hello @Imaginer2468, I'm mentioning you and Jacob @JcbSm."));
    }
}
