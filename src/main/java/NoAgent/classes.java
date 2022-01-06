package NoAgent;

import javassist.*;

import java.io.IOException;

public class classes {
    public static void dump(String classname,String targetpath){
        ClassPool pool = ClassPool.getDefault();
        pool.appendClassPath(new LoaderClassPath(detect.cl));
        CtClass string_clazz = null;
        try {
            string_clazz = pool.get(classname);
            string_clazz.writeFile(targetpath);
        } catch (NotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CannotCompileException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
