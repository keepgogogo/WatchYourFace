package com.example.WatchYourFace;

public class JsonDataGet {
    private final String INVALID_IMAGE_SIZE="INVALID_IMAGE_SIZE";
    private final String IMAGE_DOWNLOAD_TIMEOUT="IMAGE_DOWNLOAD_TIMEOUT";
    private final String INVALID_IMAGE_URL="INVALID_IMAGE_URL";

    private String[] dataBack={"性别：","女生眼中的颜值：","男生眼中的颜值：","民族：","年龄：","","request_id"};
    private String  jsonFile=null;
    private char[] o;

//    public String problemGet(String stringCheck)
//    {
//
//        StringBuilder problem=new StringBuilder();
//        if((stringCheck.indexOf(INVALID_IMAGE_SIZE))!=-1) {
//            problem.append(INVALID_IMAGE_SIZE);
//        }
//        else if((stringCheck.indexOf(INVALID_IMAGE_URL))!=-1)
//        {
//            problem.append(INVALID_IMAGE_URL);
//        }
//        else if ((stringCheck.indexOf(IMAGE_DOWNLOAD_TIMEOUT))!=-1)
//        {
//            problem.append(IMAGE_DOWNLOAD_TIMEOUT);
//        }
//
//
//    }


    public String[] jsonDataGet(String jsonFile)
    {
        this.jsonFile=jsonFile;
        o=jsonFile.toCharArray();
        dataBack[0]+=getGender();
        dataBack[1]+=getBeautyAmongFemale();
        dataBack[2]+=getBeautyAmongMale();
        dataBack[3]+=getEthnicity();
        dataBack[4]+=getAge();
        dataBack[6]+= getRequestId();
        dataBack[5]+=getEmotion();
        return dataBack;
    }


    private String getEmotion() {
        String[] chineseEmotion={"开心：","平静：","愤怒：","厌恶：","恐惧：","伤心：","惊讶："};
        String[] englishEmotion={"happiness","neutral","anger","disgust","fear","sadness","surprise"};
        String data=new String();

        for(int c=0;c<7;c++)
        {
            StringBuilder op = new StringBuilder();
            int id = jsonFile.indexOf(englishEmotion[c]);
            while (o[id - 1] != ':') {
                id++;
            }
            int length = 5;
            for (int i = 0; i < length; i++) {
                op.append(o[id + i]);
                if (o[id + i] == '.') {
                    op.append((o[id+i+1]));
                    break;
                }
            }
            op.append('\n');
            data+=chineseEmotion[c]+op.toString();
        }
        return data;
    }

    private String getRequestId() {
        int id=jsonFile.indexOf("request_id");
        StringBuilder x=new StringBuilder();

        int numberOfDoubleQuotation=3;
        for(int i=id;numberOfDoubleQuotation!=0;i++)
        {
            if (o[i]=='"') {
                numberOfDoubleQuotation--;
            }
            if(numberOfDoubleQuotation==1) {
                x.append(o[i]);
            }
        }

        return x.toString();
    }

    private String getAge() {
        int x=2;
        int id=jsonFile.indexOf("age");

        while(x!=0)
        {
            if(o[id]==':')
            {
                x--;
            }
            id++;
        }



        int length= 2;
        if(o[id]==' ') {
            id++;
        }

        StringBuilder op=new StringBuilder();
        for(int i=0;i<length;i++) {
            op.append(o[id+i]);
        }

        return op.toString()+'\n';
    }

    private String getBeautyAmongMale() {
        int id=0;
        if((o[id=jsonFile.indexOf("male_score")-1])=='e') {
            id=jsonFile.indexOf("male_score",id+2);
        }

        while(o[id-1]!=':') {
            id++;
        }
        int length= 2;
//        id++;
        if(o[id]==' ') {
            id++;
        }
        StringBuilder op=new StringBuilder();
        for(int i=0;i<length;i++) {
            op.append(o[id+i]);
        }

        return op.toString()+'\n';
    }

    private String getEthnicity() {
        return ""+"\n";
    }

    private String getBeautyAmongFemale() {
        int id=jsonFile.indexOf("female_score");
        while(o[id-1]!=':') {
            id++;
        }

        int length= 2;
//        id++;
        if(o[id]==' ') {
            id++;
        }
        StringBuilder op=new StringBuilder();
        for(int i=0;i<length;i++) {
            op.append(o[id+i]);
        }

        return op.toString()+"\n";
    }

    private String getGender() {
        int id=jsonFile.indexOf("gender");
        while(o[id]!='M'&&o[id]!='F') {
            id++;
        }

        int length= 0;
        if(o[id]=='M') {
            length=4;
        } else {
            length=6;
        }

        StringBuilder op=new StringBuilder();
        for(int i=0;i<length;i++) {
            op.append(o[id+i]);
        }

        return op.toString()+"\n";
    }

}
