import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

public class kuir {
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        String option = args[0];

        if (option.equals("-c")) {
            makeCollection mc = new makeCollection(args[1]);
            mc.MC();
        } else if (option.equals("-k")) {
            String Route = args[1];
            makeKeyword mk = new makeKeyword(Route);
            mk.makeXML();
        }
    }
}
