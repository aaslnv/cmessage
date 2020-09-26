package kz.cmessage.core.util;

        import kz.cmessage.core.session.model.Session;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.stereotype.Component;

@Component
public class SessionUtil {

    private JwtUtil jwtUtil;

    @Autowired
    public SessionUtil(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    public Session getSession(){
        return new Session();
    }
}
