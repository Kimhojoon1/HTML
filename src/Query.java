import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.io.StreamCorruptedException;
import java.util.Iterator;

public class Query {
    public String Query,Route;
    public String [][] list=new String[5][2];
    public int id;

    public Query(String Route, String Query){
        this.Route=Route;
        this.Query=Query;
    }

    public String [][] withXML() {
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
                    this.id=i+1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<String> newHashMap() throws IOException, ClassNotFoundException {
        KeywordExtractor KE = new KeywordExtractor();
        KeywordList KL = KE.extractKeyword(Query, true);
        ArrayList <String> wordall = new ArrayList<>();
        int i,j;
        for(i = 0; i < KL.size(); i++){
            Keyword kwrd = KL.get(i);
            wordall.add(kwrd.getString());
        }
        FileInputStream fileStream = new FileInputStream("./data/index.post");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileStream);

        Object object = objectInputStream.readObject();
        objectInputStream.close();

        withXML();
        HashMap hashMap = (HashMap)object;
        ArrayList <String> result = new ArrayList<>();
        double add = 0;
        for(j = 0; j < id; j++){
            for(i = 0; i < wordall.size(); i++){
                ArrayList<String> tmp = (ArrayList<String>) hashMap.get(wordall.get(i));
                String [] splitStr = tmp.get(j).split(", ");
                add = add + Double.parseDouble(splitStr[1]);
            }
            result.add(Double.toString(add));
            add = 0;
        }
        return result;

    }

    public void checkRank() throws IOException, ClassNotFoundException {
        ArrayList <String> result2 = newHashMap();
        String[] result1=result2.toArray(new String[result2.size()]);
        int[] rank=new int[result2.size()];
        double[] result=new double[result2.size()];
        int i,k ;
        for(i=0;i<result2.size();i++){
            double t=Double.parseDouble(result1[i]);
            result[i]=t;
            rank[i]=1;
        }
        for(i=0;i<result2.size();i++){
            for(k=0;k<result2.size();k++){
                if(result[i]<result[k]){
                    rank[i]++;
                }
            }
        }
        for(i=0;i<5;i++){
            System.out.println(rank[i]);
        }
        for(i=0;i<result2.size();i++){
            if(rank[i]==1)
                System.out.println("1등 : " + list[i][0]);
        }
        for(i=0;i<result2.size();i++){
            if(rank[i]==2)
                System.out.println("2등 : " + list[i][0]);
        }
        for(i=0;i<result2.size();i++){
            if(rank[i]==3)
                System.out.println("3등 : " + list[i][0]);
        }
    }
}
