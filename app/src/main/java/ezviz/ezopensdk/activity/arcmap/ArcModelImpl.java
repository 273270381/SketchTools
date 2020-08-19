package ezviz.ezopensdk.activity.arcmap;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;

import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.TextSymbol;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ezviz.ezopensdk.R;
import ezviz.ezopensdk.been.AppOperator;
import ezviz.ezopensdk.been.StyleId;
import ezviz.ezopensdk.been.StyleMap;
import ezviz.ezopensdk.utils.CopyFontFile;
import ezviz.ezopensdk.utils.ReadKml;

/**
 * Created by hejunfeng on 2020/8/13 0013
 */
public class ArcModelImpl implements ArcModel{

    @Override
    public void loadCamera(Context context,String url, ArcCallBack callBack) {
        {
            PictureMarkerSymbol pictureMarkerSymbol = new PictureMarkerSymbol((BitmapDrawable) context.getResources().getDrawable(R.mipmap.marker_1));
            ReadKml readKml = new ReadKml(url, context);
            readKml.parseKml2();
            List<String> list_name = readKml.getList_name();
            List<String> list_des = readKml.getList_des();
            List<Point> list_point = readKml.getList_point();
            for (int i = 0; i < list_point.size(); i++) {
                int finalI = i;
                Map<String, Object> map = new HashMap<>();
                map.put("style", "marker");
                map.put("name", list_name.get(finalI + 2));
                map.put("des", list_des.get(finalI));
                Graphic pointGraphic = new Graphic(list_point.get(i), pictureMarkerSymbol, map);
                TextSymbol t = new TextSymbol(12, list_name.get(finalI + 2), Color.GREEN);
                t.setFontFamily(new File(CopyFontFile.FONT_PATH).getPath());
                t.setOffsetX(-10);
                t.setOffsetY(-22);
                Map<String, Object> map2 = new HashMap<>();
                map2.put("style", "text");
                Graphic graphic_text = new Graphic(list_point.get(finalI), t, map2);
                callBack.loadCamera(pointGraphic, graphic_text);
            }
        }
    }

    @Override
    public void loadinfo(Context context, String url2, ArcCallBack callBack) {
        {
            ReadKml readKml = new ReadKml(url2, context);
            readKml.parseKml();
            List<String> list_name_info = readKml.getList_name();
            List<String> list_des_info = readKml.getList_des();
            List<List<Point>> list_collection = readKml.getList_collection();
            List<StyleId> list_styleid = readKml.getList_styleid();
            List<StyleMap> list_stylemap = readKml.getList_stylemap();
            List<String> list_style_url = readKml.getList_style_url();
            //线型
            for (int i = 0; i < list_collection.size(); i++) {
                Polyline polyline = new Polyline();
                polyline.startPath(list_collection.get(i).get(0));
                for (int j = 1; j < list_collection.get(i).size(); j++) {
                    polyline.lineTo(list_collection.get(i).get(j));
                }
                Map<String, Object> map = new HashMap<>();
                map.put("style", "line");
                String url = list_style_url.get(i);
                String linecolor = "";
                String linewidth = "";
                for (StyleMap styleMap : list_stylemap) {
                    if (styleMap.getId().equals(url)) {
                        String stylemapUrl = styleMap.getStyleUrl();
                        for (StyleId styleid : list_styleid) {
                            if (styleid.getId().equals(stylemapUrl)) {
                                linecolor = styleid.getLineColor();
                                linewidth = styleid.getLineWidth();
                            }
                        }
                    }
                }
                SimpleLineSymbol simpleLineSymbol_info = new SimpleLineSymbol(Color.parseColor("#" + linecolor), Integer.parseInt(linewidth));
                Graphic line = new Graphic(polyline, simpleLineSymbol_info, map);
                TextSymbol t = new TextSymbol(12, list_name_info.get(i + 1) + list_des_info.get(i), Color.WHITE);
                t.setFontFamily(new File(CopyFontFile.FONT_PATH).getPath());
                t.setOffsetX(-20);
                Map<String, Object> map2 = new HashMap<>();
                map2.put("style", "text");
                Graphic ts = new Graphic(polyline, t, map2);
                callBack.loadinfo(line,ts);
            }
        }
    }
}
