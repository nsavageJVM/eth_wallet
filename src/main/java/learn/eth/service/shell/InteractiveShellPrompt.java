package learn.eth.service.shell;

import learn.eth.service.Security;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

@Component
public class InteractiveShellPrompt implements PromptProvider {


    @Autowired
    private Security auth;


    @Override
    public AttributedString getPrompt() {

        if(!auth.isLoggedIn()) {
            return  new AttributedString("please log in to wally's wallets:>",
                    AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
        } else
        return  new AttributedString("wally's wallets:>",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
    }
}
