import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class indexer {
    public String[][] list = new String[5][2];
    String Route;
    public indexer(String Route) {
        this.Route = Route;
    }
    public int idk;
    public String[][] parseString() throws IOException, ClassNotFoundException {
        int id = callXML();
        ArrayList<String> word = new ArrayList<String>();
        ArrayList<Integer> tmp=new ArrayList<Integer>();
        int tmpcount=0;
        int i, k;
        String str = "";
        for (i = 0; i < id; i++) {
            str = list[i][1];
            String[] fre = str.split("#");
            for(k=0;k<fre.length;k++){
                word.add(fre[k]);
                System.out.println(fre[k]);
            }
            tmp.add(k);
        }String[] list=word.toArray(new String[word.size()]);
        Integer[] tmplist=tmp.toArray(new Integer[tmp.size()]);
        ArrayList<String> alllist=new ArrayList<String>();
        for(i=0;i<word.size();i++){
            String[] fre=list[i].split(":");
            alllist.add(fre[0]);
            alllist.add(fre[1]);
        }
        String[] all=alllist.toArray(new String[alllist.size()]);
        ArrayList<String> wordlist = new ArrayList<String>();
        ArrayList<String> frelist = new ArrayList<String>();
        for(i=0;i<all.length;i++){
            if(i%2==0)
                wordlist.add(all[i]);
            else
                frelist.add(all[i]);
        }
        String[] allword=wordlist.toArray(new String[wordlist.size()]);
        String[] allfre=frelist.toArray(new String[frelist.size()]);
        double makeinte[]=new double[allfre.length];
        k=0;

        for(i=0;i<allfre.length;i++){
            makeinte[i]=Double.parseDouble(allfre[i]);
        }
        String[][] allof=new String[allword.length][allword.length];
        for(i=0;i<allword.length;i++){
            allof[i][0]=allword[i];
            allof[i][1]=allfre[i];
        }
        idk=id;
        return allof;
    }

    public void putHashMap() throws IOException, ClassNotFoundException {
        int i;
        String[][] allof=parseString();
        Integer[][] freqlist=new Integer[allof.length][allof.length];
        for(i=0;i<allof.length;i++){
            for(int k=i;k<allof.length;k++){
                int count=1;
                if(allof[i][0]==allof[k][0]){
                    count++;
                    freqlist[i][0]=count;}
                else{
                    freqlist[i][0]=count;}
            }
        }
        double makeinte[]=new double[allof.length];
        for(i=0;i<allof.length;i++) {
            makeinte[i]=Double.parseDouble(allof[i][1]);
            makeinte[i]=makeinte[i]*Math.log(idk/freqlist[i][0]);
        }
        FileOutputStream fileStream=new FileOutputStream("./data/index.post");
        ObjectOutputStream objectOutputStream= new ObjectOutputStream(fileStream);
        HashMap<String, Double> Hashlist=new HashMap<String, Double>();
        for(i=0;i<allof.length;i++){
            Hashlist.put(allof[i][0],makeinte[i]);
        }
        objectOutputStream.writeObject(Hashlist);
        objectOutputStream.close();
        printIndex();
    }

    public int callXML() {
        int i=0;

        try {
            DocumentBuilderFactory docBuildFact = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuild = docBuildFact.newDocumentBuilder();
            Document doc = docBuild.parse(Route);
            doc.getDocumentElement().normalize();


            NodeList id = doc.getElementsByTagName("doc");

            for (i = 0; i < id.getLength(); i++) {

                System.out.println("---------- doc id " + i + "번째 ------------------");

                Node idNode = id.item(i);

                if (idNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element idElmnt = (Element) idNode;

                    NodeList titleList = idElmnt.getElementsByTagName("title");
                    Element titleElmnt = (Element) titleList.item(0);
                    Node title = titleElmnt.getFirstChild();
                    list[i][0] = title.getNodeValue();
                    NodeList bodyList = idElmnt.getElementsByTagName("body");
                    Element bodyElmnt = (Element) bodyList.item(0);
                    Node tel = bodyElmnt.getFirstChild();
                    list[i][1] = tel.getNodeValue();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }


    public void printIndex() throws IOException, ClassNotFoundException {
        FileInputStream fileStream = new FileInputStream(Route);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileStream);

        Object object = objectInputStream.readObject();
        objectInputStream.close();

        HashMap hashMap = (HashMap)object;
        Iterator<String> it = hashMap.keySet().iterator();

        while(it.hasNext()){
            String key = it.next();
            ArrayList value = (ArrayList) hashMap.get(key);
            System.out.println(key + " → " + value);
        }
    }
}
