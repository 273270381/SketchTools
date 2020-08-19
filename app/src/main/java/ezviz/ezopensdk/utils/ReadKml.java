package ezviz.ezopensdk.utils;

import android.content.Context;

import com.esri.core.geometry.Point;


import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ezviz.ezopensdk.been.StyleId;
import ezviz.ezopensdk.been.StyleMap;


public class ReadKml {
    private String path;
    private List<String> list_name;
    private List<String> list_des;
    private List<Point> list_point;
    private List<List<Point>> list_collection;
    private Context context;
    private String type="";
    private List<StyleId> list_styleid;
    private List<StyleMap> list_stylemap;
    private List<String> list_style_url;

    public ReadKml(String path, Context context) {
        this.path = path;
        this.context = context;

        this.list_name = new ArrayList<>();
        this.list_des = new ArrayList<>();
        this.list_point = new ArrayList<>();
        this.list_collection = new ArrayList<>();
        this.list_styleid = new ArrayList<>();
        this.list_stylemap = new ArrayList<>();
        this.list_style_url = new ArrayList<>();
    }


    public List<String> getList_name() {
        return list_name;
    }

    public List<String> getList_des() {
        return list_des;
    }

    public List<Point> getList_point() {
        return list_point;
    }

    public List<List<Point>> getList_collection() {
        return list_collection;
    }

    public List<StyleId> getList_styleid() {
        return list_styleid;
    }

    public List<StyleMap> getList_stylemap() {
        return list_stylemap;
    }

    public List<String> getList_style_url() {
        return list_style_url;
    }

    public  void parseKml(){
        try {
            //InputStream inputStream = new FileInputStream(path);
            InputStream inputStream = context.getAssets().open(path);
            SAXReader reader = new SAXReader();
            Document document = null;
            try {
                document = reader.read(inputStream);
            } catch (DocumentException e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            Element root = document.getRootElement();//获取doc.kml文件的根结点
            listNodes(root);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    public  void parseKml2(){
        try {
            //InputStream inputStream = new FileInputStream(path);
            InputStream inputStream = context.getAssets().open(path);
            SAXReader reader = new SAXReader();
            Document document = null;
            try {
                document = reader.read(inputStream);
            } catch (DocumentException e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            Element root = document.getRootElement();//获取doc.kml文件的根结点
            listNodes2(root);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    private  void listNodes(Element node) {
        //假设当前节点内容不为空，则输出
        if(!(node.getTextTrim().equals("")) && "name".equals(node.getName())){
            //Log.d("当前结点内容：", node.getText());
            //parseHtml(node.getText());
            list_name.add(node.getText());
        }else if (!(node.getTextTrim().equals("")) && "description".equals(node.getName())) {
            list_des.add(node.getText());
        }else if ("Point".equals(node.getName())){
            type = "point";
        }else if("Polygon".equals(node.getName())){
            type = "polygon";
        }else if(!(node.getTextTrim().equals("")) && "coordinates".equals(node.getName())){
            String string = node.getText();
            if (type.equals("point")){
                Point p = new Point(Double.parseDouble(string.substring(0,string.indexOf(","))), Double.parseDouble(string.substring(string.indexOf(",")+1,string.lastIndexOf(","))));
                list_point.add(p);
            }else{
                String[] str = string.trim().split(" ");
                List<Point> list = new ArrayList<>();
                for (int i = 0 ; i < str.length ; i++){
                    String t = str[i];
                    Point p = new Point(Double.parseDouble(t.substring(0,t.indexOf(","))), Double.parseDouble(t.substring(t.indexOf(",")+1,t.lastIndexOf(","))));
                    list.add(p);
                }
                list_collection.add(list);
            }
        }else if("LineStyle".equals(node.getName())){
            Element element = node.element("color");
            //LineColor = element.getText();
            Element element1 = node.element("width");
            //LineWidth = element1.getText();
        }else if ("Style".equals(node.getName())){
            String StyleId = node.attributeValue("id");
            List<Element> element_list = node.elements("LineStyle");
            if (element_list.size()>0){
                Element element = element_list.get(0);
                String LineColor = element.element("color").getText();
                String lineColor = LineColor.substring(2,LineColor.length());
                String LineWidth = element.element("width").getText();
                //Log.i("TAG","LineColor="+LineColor);
                //Log.i("TAG","LineWidth="+LineWidth);
                //Log.i("TAG","StyleId="+StyleId);
                StyleId styleId = new StyleId(StyleId,lineColor,LineWidth);
                list_styleid.add(styleId);
            }
        }else if("StyleMap".equals(node.getName())){
            String StylemapId = node.attributeValue("id");
            Element element = node.element("Pair");
            Element element1 = element.element("styleUrl");
            String url = element1.getText().substring(1,element1.getText().length());
            //Log.i("TAG","stylemapurl="+url);
            //Log.i("TAG","****************");
            StyleMap styleMap = new StyleMap(StylemapId,url);
            list_stylemap.add(styleMap);
        }else if ("Placemark".equals(node.getName())){
            Element element = node.element("styleUrl");
            String styleUrl = element.getText().substring(1,element.getText().length());
            list_style_url.add(styleUrl);
        }
        //同一时候迭代当前节点以下的全部子节点
        //使用递归
        Iterator<Element> iterator = node.elementIterator();
        while(iterator.hasNext()){
            Element e = iterator.next();
            listNodes(e);
        }
    }
    private  void listNodes2(Element node) {
        //假设当前节点内容不为空，则输出
        if(!(node.getTextTrim().equals("")) && "name".equals(node.getName())){
            //Log.d("当前结点内容：", node.getText());
            //parseHtml(node.getText());
            list_name.add(node.getText());
        }else if (!(node.getTextTrim().equals("")) && "description".equals(node.getName())) {
            list_des.add(node.getText());
        }else if ("Point".equals(node.getName())){
            type = "point";
        }else if("Polygon".equals(node.getName())){
            type = "polygon";
        }else if(!(node.getTextTrim().equals("")) && "coordinates".equals(node.getName())){
            String string = node.getText();
            if (type.equals("point")){
                Point p = new Point(Double.parseDouble(string.substring(0,string.indexOf(","))), Double.parseDouble(string.substring(string.indexOf(",")+1,string.lastIndexOf(","))));
                list_point.add(p);
            }else{
                String[] str = string.trim().split(" ");
                List<Point> list = new ArrayList<>();
                for (int i = 0 ; i < str.length ; i++){
                    String t = str[i];
                    Point p = new Point(Double.parseDouble(t.substring(0,t.indexOf(","))), Double.parseDouble(t.substring(t.indexOf(",")+1,t.lastIndexOf(","))));
                    list.add(p);
                }
                list_collection.add(list);
            }
        }
        //同一时候迭代当前节点以下的全部子节点
        //使用递归
        Iterator<Element> iterator = node.elementIterator();
        while(iterator.hasNext()){
            Element e = iterator.next();
            listNodes2(e);
        }
    }
}
