package com.solutions.aryaan.domparsingex;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity {

    PlaceholderFragment taskFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null){
            taskFragment = new PlaceholderFragment();
            getSupportFragmentManager().beginTransaction().add(taskFragment,"MyFragment").commit();
        }else{
            getSupportFragmentManager().findFragmentByTag("MyFragment");
        }
        taskFragment.startTask();
    }

    public static class PlaceholderFragment extends Fragment {

        TechCrunchTask downloadTask;

        public PlaceholderFragment(){

        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setRetainInstance(true);
        }

        public void startTask(){
            if(downloadTask != null){
                downloadTask.cancel(true);
            }else{
                downloadTask = new TechCrunchTask();
                downloadTask.execute();
            }
        }
    }

    public static class TechCrunchTask extends AsyncTask<Void,Void,ArrayList<HashMap<String,String>>>{

        @Override
        protected ArrayList<HashMap<String,String>> doInBackground(Void... voids) {

            String downloadURL = "http://feeds.feedburner.com/techcrunch/android?format=xml";
            ArrayList<HashMap<String,String>> results = new ArrayList<>();
            try {
                URL url = new URL(downloadURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                InputStream inputStream = connection.getInputStream();
                results = processXML(inputStream);
            } catch (Exception e) {
                Message.logMessage(""+e);
            }
            return results;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
            Message.logMessage(result+" ");
        }

        public ArrayList<HashMap<String,String>> processXML(InputStream inputStream)throws Exception{
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document xmlDocument = documentBuilder.parse(inputStream);

            Element rootElement = xmlDocument.getDocumentElement();
            Message.logMessage(""+rootElement.getTagName());

            NodeList itemList = rootElement.getElementsByTagName("item");
            NodeList itemChildren = null;
            NamedNodeMap mediaThumbnailAttribute = null;
            Node currentItem = null;
            Node currentChild = null;
            Node currentAttribute = null;
            int count = 0;
            ArrayList<HashMap<String,String>> results = new ArrayList<>();
            HashMap<String,String> currentMap = null;

            for (int i = 0;i<itemList.getLength();i++){
                currentItem = itemList.item(i);
                Message.logMessage(""+currentItem.getNodeName());
                currentMap = new HashMap<>();

                itemChildren = currentItem.getChildNodes();
                for (int j=0;j<itemChildren.getLength();j++){
                    currentChild = itemChildren.item(j);
                    //Message.logMessage(""+currentChild.getNodeName());
                    if (currentChild.getNodeName().equalsIgnoreCase("title")){
                        //Message.logMessage(currentChild.getTextContent());
                        currentMap.put("title",currentChild.getTextContent());
                    }
                    if(currentChild.getNodeName().equalsIgnoreCase("pubDate")){
                        //Message.logMessage(currentChild.getTextContent());
                        currentMap.put("pubDate",currentChild.getTextContent());
                    }
                    if(currentChild.getNodeName().equalsIgnoreCase("description")){
                        //Message.logMessage(currentChild.getTextContent());
                        currentMap.put("description",currentChild.getTextContent());
                    }
                    if (currentChild.getNodeName().equalsIgnoreCase("media:thumbnail")){
                        /*mediaThumbnailAttribute = currentChild.getAttributes();
                        for (int k = 0;k<mediaThumbnailAttribute.getLength();k++){
                            currentAttribute = mediaThumbnailAttribute.item(i);
                            if (currentAttribute.getNodeName().equalsIgnoreCase("url")){
                                Message.logMessage(currentAttribute.getTextContent());
                            }

                        }*/
                        //Message.logMessage(currentChild.getAttributes().item(0).getTextContent());
                        count++;
                        if(count == 2){
                            //Message.logMessage(currentChild.getAttributes().item(0).getTextContent());
                            currentMap.put("imageURL",currentChild.getAttributes().item(0).getTextContent());
                        }
                        //Message.logMessage(count+" ");

                    }
                }
                //Message.logMessage(" "+currentMap);
                if (currentMap != null && !currentMap.isEmpty()){
                    results.add(currentMap);
                }
                count = 0;

            }
            return results;
        }
    }
}
