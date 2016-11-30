package com.solutions.aryaan.domparsingex;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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

    public static class TechCrunchTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            String downloadURL = "http://feeds.feedburner.com/techcrunch/android?format=xml";
            try {
                URL url = new URL(downloadURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                InputStream inputStream = connection.getInputStream();
                processXML(inputStream);
            } catch (Exception e) {
                Message.logMessage(""+e);
            }
            return null;
        }

        public void processXML(InputStream inputStream)throws Exception{
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document xmlDocument = documentBuilder.parse(inputStream);

            Element rootElement = xmlDocument.getDocumentElement();
            Message.logMessage(""+rootElement.getTagName());

            NodeList itemList = rootElement.getElementsByTagName("item");
            NodeList childItem = null;
            Node currentItem = null;
            Node currentChild = null;
            for (int i = 0;i<itemList.getLength();i++){
                currentItem = itemList.item(i);
                Message.logMessage(""+currentItem.getNodeName());

                childItem = currentItem.getChildNodes();
                for (int j=0;j<childItem.getLength();j++){
                    currentChild = childItem.item(j);
                    Message.logMessage(""+currentChild.getNodeName());
                    if (currentChild.getNodeName().equalsIgnoreCase("title")){
                        Message.logMessage(currentChild.getTextContent());
                    }
                }
            }
        }
    }
}
