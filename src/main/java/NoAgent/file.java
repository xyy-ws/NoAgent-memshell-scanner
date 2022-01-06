package NoAgent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class file {
    public static void bufferedWriterMethod(String filepath, String content) throws IOException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filepath))) {
            bufferedWriter.write(content);
        }
    }
    public static void createjavafile(String javaPath) throws IOException {
        File javafile = new File(javaPath);
        File fileParent = javafile.getParentFile();//返回的是File类型,可以调用exsit()等方法
        String fileParentPath = javafile.getParent();//返回的是String类型
        if (!fileParent.exists()) {
            fileParent.mkdirs();// 能创建多级目录
        }
        if (!javafile.exists())
            javafile.createNewFile();//有路径才能创建文件
    }
    public static Boolean hasFile(File path,String filename) {
        String newfilename = filename.replace("_002d","-").replace("_005f","_");
        /*if(filename.contains("_"))//处理classname _后会带有002d,005f等字符
        {
            String[] strArr = filename.split("_");
            if(strArr.length>2){
                newfilename = strArr[0];
                for(int i=1;i<strArr.length-1;i++){
                strArr[i] = strArr[i].substring(4);
                newfilename = newfilename.concat("_"+strArr[i]);
                }
            newfilename = newfilename.concat("_"+strArr[strArr.length-1]);}
        }
*/
        File[] listFiles = path.listFiles();
        for (File file1 : listFiles) {
            if (file1.getName().replace(".","_").equals(newfilename)) {
                return true;  // 如果是目录且没有发现有文件，回调自身继续查询
            }
            if(file1.isDirectory())
            {
                hasFile(file1,filename);
            }
        }
        return false;
    }
}

