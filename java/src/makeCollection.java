import java.io.File;
import java.io.FileOutputStream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class makeCollection{

    String Dir;
    public makeCollection(String Dir) {
        this.Dir = Dir;
    }
    public static String[] strArr = new String[2];
    public void MC(){

        File path = new File(this.Dir);
        File[] fileList = path.listFiles();
        int i = 0;
        for (i = 0; i < fileList.length; i++) {
            System.out.println(fileList[i]);
        }
        try{

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            org.w3c.dom.Document docu = docBuilder.newDocument();
            ((org.w3c.dom.Document) docu).setXmlStandalone(true);

            Element docs = (Element) docu.createElement("docs");
            docu.appendChild(docs);

            for (int k = 0; k < fileList.length; k++) {
                String t = Integer.toString(k);
                parse(fileList[k]);

                Element doc = (Element) docu.createElement("doc");
                docs.appendChild(doc);

                doc.setAttribute("id", t);

                Element title = (Element) docu.createElement("title");
                title.appendChild(((org.w3c.dom.Document) docu).createTextNode(strArr[0]));
                doc.appendChild(title);

                Element body = (Element) docu.createElement("body");
                body.appendChild(((org.w3c.dom.Document) docu).createTextNode(strArr[1]));
                doc.appendChild(body);

            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();

            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            DOMSource source = new DOMSource(docu);
            StreamResult result = new StreamResult(new FileOutputStream(new File("C:\\Users\\user\\IdeaProjects\\SimpleIR\\data\\collection.xml")));

            transformer.transform(source, result);

            System.out.println("=========END=========");

        } catch(Exception e){
            e.printStackTrace();
        }
    }


    public static String[] parse(File fileList) {

        try {
            Document doc = Jsoup.parse(fileList, "UTF-8");
            Elements p = doc.select("p");
            Elements title = doc.select("title");
            String strP = p.text();
            String strTitle = title.text();
            strArr[0] = strTitle;
            strArr[1] = strP;
        } catch (Exception e) {
            System.out.println(e);
        }
        return strArr;
    }
}
