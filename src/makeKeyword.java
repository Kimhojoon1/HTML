import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.xml.sax.SAXException;


public class makeKeyword {
    String Route;
    makeKeyword(String Route){
        this.Route = Route;
    }

    public String [][] callXML() {
        String [][] list=new String[5][2];
        try {
            DocumentBuilderFactory docBuildFact = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuild = docBuildFact.newDocumentBuilder();
            Document doc = docBuild.parse(Route);
            doc.getDocumentElement().normalize();


            NodeList id = doc.getElementsByTagName("doc");

            for (int i = 0; i < id.getLength(); i++) {

                System.out.println("---------- doc id " + i + "번째 ------------------");

                Node idNode = id.item(i);

                if (idNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element idElmnt = (Element) idNode;

                    NodeList titleList = idElmnt.getElementsByTagName("title");
                    Element titleElmnt = (Element) titleList.item(0);
                    Node title = titleElmnt.getFirstChild();
                    list[i][0]=title.getNodeValue();
                    NodeList bodyList = idElmnt.getElementsByTagName("body");
                    Element bodyElmnt = (Element) bodyList.item(0);
                    Node tel = bodyElmnt.getFirstChild();
                    list[i][1]=tel.getNodeValue();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void makeXML() throws IOException, SAXException, ParserConfigurationException {

        String [][] changelist = changeKkma(callXML());

        try{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            Document document = db.newDocument();
            Element docs = document.createElement("docs");
            document.appendChild(docs);
            for(int i = 0; i < changelist.length; i++){
                String num = Integer.toString(i);

                String getTitle = changelist[i][0];
                String getBody = changelist[i][1];

                Element doc = document.createElement("doc");
                Element title = document.createElement("title");
                Element body = document.createElement("body");
                docs.appendChild(doc);
                doc.setAttribute("id", num);
                doc.appendChild(title);
                title.appendChild(document.createTextNode(getTitle));
                doc.appendChild(body);
                body.appendChild(document.createTextNode(getBody));
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            DOMSource src = new DOMSource(document);
            StreamResult result = new StreamResult(new FileOutputStream(new File("C:\\Users\\user\\IdeaProjects\\SimpleIR\\data\\index.xml")));

            transformer.transform(src, result);

        }catch (Exception e){
            System.out.println(e);
        }
    }

    public String useKkma(String list){

        String changedBody = "";

        KeywordExtractor ke = new KeywordExtractor();
        KeywordList kl = ke.extractKeyword(list, true);

        for(int i = 0; i < kl.size(); i++){
            Keyword kwrd = kl.get(i);
            if (i != kl.size() - 1){
                changedBody = changedBody + kwrd.getString() + ":" + kwrd.getCnt() + "#";
            }else{
                changedBody = changedBody + kwrd.getString() +":" + kwrd.getCnt();
            }
        }
        return changedBody;
    }

    public String [][] changeKkma(String [][] list){
        String [][] kkmaUsedText = new String[list.length][list[0].length];
        for(int i = 0; i < list.length; i++){
            for(int j = 0; j < list[0].length; j++){
                if(j == list[0].length - 1){
                    kkmaUsedText[i][j] = useKkma(list[i][j]);
                }else{
                    kkmaUsedText[i][j]= list[i][j];
                }
            }
        }
        return kkmaUsedText;
    }


}
