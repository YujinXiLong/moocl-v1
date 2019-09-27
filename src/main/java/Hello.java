import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@WebServlet("/hello")
public class Hello extends HttpServlet{

    private static final long serialVersionUID = 1322421512190092266L;
    @Override
    protected void service(HttpServletRequest request ,HttpServletResponse response) {

        System.out.println("hello");
    }
}
