package ezviz.ezopensdk.utils;

public class ToChineseNumUtill {

    public  static String numberToChinese(int num){
        if(num == 0){
            return "零";
        }

        int weigth = 0;//节权位
        String chinese="";
        String chinese_section = "";
        boolean setZero =false;//下一小节是否需要零，第一次没有上一小节所以为false
        while (num >0){
            int section = num%10000;//得到最后面的小节
            if(setZero){//判断上一小节的千位是否为零，是就设置零
                chinese  = TooltoCh(0)+chinese;
            }
            chinese_section = sectionTrans(section);
            if(section != 0){//判断是都加节权位
                chinese_section = chinese_section +getWeight(weigth);
            }

            chinese = chinese_section+chinese;

            chinese_section = "";
            setZero = (section<1000)&&(section>0);
            num = num/10000;
            weigth++;

        }
        if((chinese.length()==2||(chinese.length() == 3))&&chinese.contains("一十")){
            chinese =  chinese.substring(1,chinese.length());
        }
        return chinese;
    }

    public  static String sectionTrans(int section){
        StringBuilder section_chinese =
                new StringBuilder();
        int pos = 0;//小节内部权位的计数器
        boolean zero = true;//小节内部的置零判断，每一个小节只能有一个零。
        while (section >0){
            int v = section%10;//得到最后一个数
            if(v ==0 ){
                if(!zero){
                    zero = true;//需要补零的操作，确保对连续多个零只是输出一个
                    section_chinese.insert(0,TooltoCh(0));
                }
            }else {
                zero = false;//有非零数字就把置零打开
                section_chinese.insert(0,getPos(pos));
                section_chinese.insert(0,TooltoCh(v));
            }
            pos++;
            section = section/10;
        }

        return section_chinese.toString();
    }

    public  static String TooltoCh(int num){

        switch (num){
            case 0:
                return "零";

            case 1:
                return "一";

            case 2:
                return "二";

            case 3:
                return "三";

            case 4:
                return "四";

            case 5:
                return "五";

            case 6:
                return "六";

            case 7:
                return "七";

            case 8:
                return "八";

            case 9:
                return "九";

        }

        return "erro";

    }

    public  static String getPos(int pos){
        switch (pos){
            case 1:
                return "十";

            case 2:
                return "百";

            case 3:
                return "千";



        }

        return "";

    }

    public  static String getWeight(int weight){
        switch (weight){
            case 1:
                return "万";

            case 2:
                return "亿";



        }

        return "";
    }

}

