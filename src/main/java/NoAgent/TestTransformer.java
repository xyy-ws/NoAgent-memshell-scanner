package NoAgent;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class TestTransformer implements ClassFileTransformer {
    //目标类名称，  .分隔
    private String targetClassName;
    //目标类名称，  /分隔
    private String targetVMClassName;
    private String targetMethodName;


    public TestTransformer(String className, String methodName){
        this.targetVMClassName = new String(className).replaceAll("\\.","\\/");
        this.targetMethodName = methodName;
        this.targetClassName=className;
    }
    public static String txt2String(File file){
               String result = "";
                 try{
                       BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
                       String s = null;
                         while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                                result = result + "\n" +s;
                             }
                         br.close();
                     }catch(Exception e){
                         e.printStackTrace();
                     }
                 return result;
             }

    //类加载时会执行该函数，其中参数 classfileBuffer为类原始字节码，返回值为目标字节码，className为/分隔
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        //判断类名是否为目标类名
        if(!className.equals(targetVMClassName)){
            System.out.println("not do transform");
            return classfileBuffer;
        }
        try {
            System.out.println("do transform");
            ClassPool classPool = ClassPool.getDefault();
            CtClass cls = classPool.get(this.targetClassName);
            System.out.println(cls.getName());
            CtMethod ctMethod = cls.getDeclaredMethod(this.targetMethodName);
            System.out.println(ctMethod.getName());
            ctMethod.insertBefore("{ System.out.println(\"start\"); }");
            ctMethod.insertAfter("{ System.out.println(\"end\"); }");
            //File file = new File("E://内存马//java-agent//123.txt");
           // String src = txt2String(file);
           // System.out.println(src);
            //ctMethod.insertAfter("if(flag==0)" +
              //    "{persist();flag++;}");
            // 创建方法
            //CtMethod m = new CtMethod(CtClass.intType,"add",
            ///        new CtClass[]{CtClass.intType,CtClass.intType},cls);
           // m.setModifiers(Modifier.PUBLIC);
            //m.setBody("{System.out.println(\"www.sxt.cn\");return $1+$2;}");

            //cls.addMethod(m);
            //cls.detach();
            System.out.println("123");
            return cls.toBytecode();
        } catch (Exception e) {

        }
        return classfileBuffer;
    }


}