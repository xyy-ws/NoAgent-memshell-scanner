package NoAgent;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class classnamecheck {
    public static  List<String> blacklist = new ArrayList<String>();
    public static List<String> risklist = new ArrayList<String>();
    public static boolean jspcheck(String classname){
        if(classname.contains("_jsp"))
            return true;
        else  return false;
    }
    public static boolean blacklistcheck(String classname){//这个是必定为内存马的
        blacklist.add("net.rebeyond");
        blacklist.add("com.metasploit");
        for(String black:blacklist){
            if(classname.contains(black))
                return true;

        }
        return false;

    }
    public static boolean risklistcheck(String classname){
       return true;
    }
    public static boolean classFileIsExists(String classname) throws ClassNotFoundException {
        Class clazz = Class.forName(classname);
        if(clazz == null){
            return false;
        }
        URL is;
        String classNamePath = classname.replace(".", "/") + ".class";
        if(clazz.getClassLoader()==null)
        {
            is = ClassLoader.getSystemClassLoader().getResource(classNamePath);
        }
        else
        {is = clazz.getClassLoader().getResource(classNamePath);}
        if(is == null){
            return false;
        }else{
            return true;
        }
    }

    public static boolean interfacecheck(Class cls) throws ClassNotFoundException {
        List<String> riskSuperClassesName = new ArrayList<String>();
        riskSuperClassesName.add("javax.servlet.http.HttpServlet");
        String classname = cls.getName();
        List<String> interfaces = new ArrayList<String>();
        while (cls!=null&&classname!="java.lang.Object"){
            for(Class inter : cls.getInterfaces())
            {
                interfaces.add(inter.getName());
            }
            if( // 继承危险父类的目标类
                    (cls.getSuperclass() != null && riskSuperClassesName.contains(cls.getSuperclass().getName())) ||
                            // 实现特殊接口的目标类,待完善
                           cls.getName().equals("org.springframework.web.servlet.handler.AbstractHandlerMapping") ||
                            interfaces.contains("javax.servlet.Filter") ||
                            interfaces.contains("javax.servlet.Servlet") ||
                            interfaces.contains("javax.servlet.ServletRequestListener")||
                            interfaces.contains("javax.servlet.FilterChain")
            )
                return true;
            cls=cls.getSuperclass();
        }
        return false;

    }

}
