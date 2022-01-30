<%@ page import="java.net.URL" %>
<%@ page import="java.net.URLClassLoader" %>
<%@ page import="java.net.MalformedURLException" %>
<%@ page import="java.lang.reflect.Method" %>
<%@ page import="java.lang.reflect.InvocationTargetException" %>
<%@ page import="java.lang.reflect.Field" %>
<%@ page import="org.apache.catalina.Pipeline" %>
<%@ page import="java.util.EventListener" %>
<%@ page import="org.apache.catalina.core.StandardContext" %>
<%@ page import="java.util.concurrent.CopyOnWriteArrayList" %>
<%@ page import="org.apache.catalina.connector.Request" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.lang.management.ManagementFactory" %>
<%@ page import="java.io.File" %>
<%@ page import="java.util.Map" %>
<%@ page import="org.apache.catalina.Valve" %>
<%@ page import="org.apache.catalina.core.StandardWrapper" %>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="java.io.BufferedReader" %><%--
  Created by IntelliJ IDEA.
  User: xyyl1l
  Date: 2021/12/23
  Time: 11:54
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
    String java = "java";
    String outputdir ="D:\\内存马\\dumpclass-master";//导出内存中class的位置，可自定义
    String javaexe = "C:\\Users\\xyy\\.jdks\\corretto-1.8.0_312\\bin\\java.exe";//dumpclass需要使用java8 此为其地址
    String dumpclassjar = "D:\\内存马\\dumpclass-master\\dumpclass-0.0.2.jar";//dumpclass-0.0.2.jar地址
    File jsppath = new File("D:\\内存马\\tomcat-filter-shell\\src\\main\\webapp");
    String dllpath = "D:\\内存马\\tomcat-filter-shell\\src\\main\\webapp\\WEB-INF\\lib\\NOAgent.dll";
    String NoAgentjarpath = "D:\\内存马\\tomcat-filter-shell\\src\\main\\webapp\\WEB-INF\\lib\\NoAgent.jar";
    public Object getStandardContext(HttpServletRequest request) throws NoSuchFieldException, IllegalAccessException {
        Object context = request.getSession().getServletContext();
        Field _context = context.getClass().getDeclaredField("context");
        _context.setAccessible(true);
        Object appContext = _context.get(context);
        Field __context = appContext.getClass().getDeclaredField("context");
        __context.setAccessible(true);
        Object standardContext = __context.get(appContext);
        return standardContext;
    }
    public HashMap<String,Object> getFilterConfig(HttpServletRequest request) throws Exception{
        Object standardContext = getStandardContext(request);
        Field _filterConfigs = standardContext.getClass().getDeclaredField("filterConfigs");
        _filterConfigs.setAccessible(true);
        HashMap<String,Object> filterConfigs = (HashMap<String,Object>)_filterConfigs.get(standardContext);
        return filterConfigs;
    }

    // FilterMap[]
    public Object[] getFilterMaps(HttpServletRequest request) throws Exception{
        Object standardContext = getStandardContext(request);
        Field _filterMaps = standardContext.getClass().getDeclaredField("filterMaps");
        _filterMaps.setAccessible(true);
        Object filterMaps = _filterMaps.get(standardContext);

        Object[] filterArray = null;
        try { // tomcat 789
            Field _array = filterMaps.getClass().getDeclaredField("array");
            _array.setAccessible(true);
            filterArray = (Object[]) _array.get(filterMaps);
        }catch (Exception e){ // tomcat 6
            filterArray = (Object[]) filterMaps;
        }

        return filterArray;
    }
    public String getFilterName(Object filterMap) throws Exception{
        Method getFilterName = filterMap.getClass().getDeclaredMethod("getFilterName");
        getFilterName.setAccessible(true);
        return (String)getFilterName.invoke(filterMap,null);
    }
    public synchronized HashMap<String,Object> getChildren(HttpServletRequest request) throws Exception{
        Object standardContext = getStandardContext(request);
        Field _children = standardContext.getClass().getSuperclass().getDeclaredField("children");
        _children.setAccessible(true);
        HashMap<String,Object> children = (HashMap<String, Object>) _children.get(standardContext);
        return children;
    }

    public synchronized HashMap<String,String> getServletMaps(HttpServletRequest request) throws Exception{
        Object standardContext = getStandardContext(request);
        Field _servletMappings = standardContext.getClass().getDeclaredField("servletMappings");
        _servletMappings.setAccessible(true);
        HashMap<String,String> servletMappings = (HashMap<String,String>)_servletMappings.get(standardContext);
        return servletMappings;
    }
    public synchronized CopyOnWriteArrayList<EventListener> getListeners(HttpServletRequest request) throws NoSuchFieldException, IllegalAccessException {
        Object standardContext = getStandardContext(request);
        Field _applicationEventlistenersList = standardContext.getClass().getDeclaredField("applicationEventListenersList");
        _applicationEventlistenersList.setAccessible(true);
        CopyOnWriteArrayList<EventListener> applicationEventlistenersList = (CopyOnWriteArrayList<EventListener>)_applicationEventlistenersList.get(standardContext);
        return applicationEventlistenersList;
    }
    public synchronized Pipeline getPipeline(HttpServletRequest request) throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        Field reqF = request.getClass().getDeclaredField("request");
        reqF.setAccessible(true);
        Request req = (Request) reqF.get(request);
        StandardContext context = (StandardContext) req.getContext();
        Pipeline pipeline = context.getPipeline();
        return pipeline;
    }
    public ClassLoader getclassloader(String path) throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        URL url = new URL("file:" + path);
        ClassLoader loader = new URLClassLoader(new URL[]{url}, Thread.currentThread().getContextClassLoader());
        return loader;
    }
    public Object getInsn(Object Detecter,Class detect,String path) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method genImp = detect.getDeclaredMethod("genImp",String.class,detect);
        Object Insn = genImp.invoke(Detecter,path,Detecter);
        return Insn;
    }
    public boolean classFileIsExists(Object Detecter,Class detect,Class cls) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method classFileIsExists = detect.getDeclaredMethod("classFileIsExists",Class.class);
        boolean flag = (boolean) classFileIsExists.invoke(Detecter,cls);
        return flag;
    }
    public String codecheckanddump(Object Detecter,String classname,String outputdir,Class detect) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method codecheckanddump = detect.getDeclaredMethod("codecheckanddump",String.class,String.class);
        String code = (String) codecheckanddump.invoke(Detecter,classname,outputdir);
        return code;
    }
    public boolean blacklistcheck(Class classnamecheck , String classname) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method blacklistcheck = classnamecheck.getDeclaredMethod("blacklistcheck",String.class);
        boolean flag = (boolean) blacklistcheck.invoke(null,classname);
        return flag;
    }
    public boolean jspcheck(Class classnamecheck , String classname) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method jspcheck = classnamecheck.getDeclaredMethod("jspcheck",String.class);
        boolean flag = (boolean) jspcheck.invoke(null,classname);
        return flag;
    }
    public boolean interfacecheck(Class classnamecheck , Class cls) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method interfacecheck = classnamecheck.getDeclaredMethod("interfacecheck",Class.class);
        boolean flag = (boolean) interfacecheck.invoke(null,cls);
        return flag;
    }
    public boolean hasFile(Class file,File path, String classname) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method hasFile = file.getDeclaredMethod("hasFile",File.class,String.class);
        boolean flag = (boolean) hasFile.invoke(null,path,classname);
        return flag;
    }
    public synchronized void deleteFilter(HttpServletRequest request,String filterName) throws Exception{
        Object standardContext = getStandardContext(request);
        //移出FilterDef
        // org.apache.catalina.core.StandardContext#removeFilterDef
        HashMap<String,Object> filterConfig = getFilterConfig(request);
        Object appFilterConfig = filterConfig.get(filterName);
        Field _filterDef = appFilterConfig.getClass().getDeclaredField("filterDef");
        _filterDef.setAccessible(true);
        Object filterDef = _filterDef.get(appFilterConfig);
        Class clsFilterDef = null;
        try{
            // Tomcat 8
            clsFilterDef = Class.forName("org.apache.tomcat.util.descriptor.web.FilterDef");
        }catch (Exception e){
            // Tomcat 7
            clsFilterDef = Class.forName("org.apache.catalina.deploy.FilterDef");
        }
        Method removeFilterDef = standardContext.getClass().getDeclaredMethod("removeFilterDef", new Class[]{clsFilterDef});
        removeFilterDef.setAccessible(true);
        removeFilterDef.invoke(standardContext,filterDef);
        //移出映射
        // org.apache.catalina.core.StandardContext#removeFilterMap
        Class clsFilterMap = null;
        try{
            // Tomcat 8
            clsFilterMap = Class.forName("org.apache.tomcat.util.descriptor.web.FilterMap");
        }catch (Exception e){
            // Tomcat 7
            clsFilterMap = Class.forName("org.apache.catalina.deploy.FilterMap");
        }
        Object[] filterMaps = getFilterMaps(request);
        for(Object filterMap:filterMaps){
            Field _filterName = filterMap.getClass().getDeclaredField("filterName");
            _filterName.setAccessible(true);
            String filterName0 = (String)_filterName.get(filterMap);
            if(filterName0.equals(filterName)){
                Method removeFilterMap = standardContext.getClass().getDeclaredMethod("removeFilterMap", new Class[]{clsFilterMap});
                removeFilterDef.setAccessible(true);
                removeFilterMap.invoke(standardContext,filterMap);
            }
        }
    }

    public synchronized void deleteServlet(HttpServletRequest request,String servletName) throws Exception{
        HashMap<String,Object> childs = getChildren(request);
        Object objChild = childs.get(servletName);
        String urlPattern = null;
        HashMap<String,String> servletMaps = getServletMaps(request);
        for(Map.Entry<String,String> servletMap:servletMaps.entrySet()){
            if(servletMap.getValue().equals(servletName)){
                urlPattern = servletMap.getKey();
                break;
            }
        }

        if(urlPattern != null) {
            // 反射调用 org.apache.catalina.core.StandardContext#removeServletMapping
            Object standardContext = getStandardContext(request);
            Method removeServletMapping = standardContext.getClass().getDeclaredMethod("removeServletMapping", new Class[]{String.class});
            removeServletMapping.setAccessible(true);
            removeServletMapping.invoke(standardContext, urlPattern);
            // Tomcat 6必须removeChild 789可以不用
            // 反射调用 org.apache.catalina.core.StandardContext#removeChild
            Method removeChild = standardContext.getClass().getDeclaredMethod("removeChild", new Class[]{org.apache.catalina.Container.class});
            removeChild.setAccessible(true);
            removeChild.invoke(standardContext, objChild);
        }
    }

    public synchronized void deletelistener(HttpServletRequest request,String listenername)throws Exception{
        //StandardContext standardContext = (StandardContext)getStandardContext(request);
        //String[] listeners = standardContext.findApplicationListeners();
        CopyOnWriteArrayList<EventListener> applicationEventlistenersList = getListeners(request);
        for(EventListener listener:applicationEventlistenersList){
        if(listener.getClass().getName().equals(listenername))
            applicationEventlistenersList.remove(listener);
        }
    }

    public synchronized void deletevalve(HttpServletRequest request,String valvename)throws Exception{
        StandardContext standardContext = (StandardContext)getStandardContext(request);
        Pipeline pipeline = standardContext.getPipeline();
        Valve[] valves = pipeline.getValves();
        for(Valve valve : valves){
            if(valve.getClass().getName().equals(valvename))
                pipeline.removeValve(valve);
        }
    }


    ClassLoader loader;

    {
        try {
            loader = getclassloader(NoAgentjarpath);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    Class detect;

    {
        try {
            detect = loader.loadClass("NoAgent.detect");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    Class classnamecheck;

    {
        try {
            classnamecheck = loader.loadClass("NoAgent.classnamecheck");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    Class file;

    {
        try {
            file = loader.loadClass("NoAgent.file");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    Object Detecter;

    {
        try {
            Detecter = detect.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    Object insn;

    {
        try {
            insn = getInsn(Detecter,detect,dllpath);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

%>
<%


    // get name representing the running Java virtual machine.
    String name = ManagementFactory.getRuntimeMXBean().getName();
    System.out.println(name);
// get pid
    int pid =  Integer.parseInt(name.split("@")[0]);//获取pid
    System.out.println("Pid is:" + pid);

    Class<?> instrument_clazz = Class.forName("sun.instrument.InstrumentationImpl");
    Method getAllLoadedClasses = instrument_clazz.getMethod("getAllLoadedClasses");//列出所有内存中的类
    Class<?>[] clazzes = (Class<?>[]) getAllLoadedClasses.invoke(insn);
    Map<String,Class> riskmap = new HashMap<String,Class>();

    String action = request.getParameter("action");
    String className = request.getParameter("className");
    String kind = request.getParameter("kind");
    if(action != null && action.equals("kill")&&kind.equals("filter")) {
        deleteFilter(request,className);
    }else if(action != null && action.equals("kill")&&kind.equals("servlet")){
        deleteServlet(request,className);
    }
    else if(action != null && action.equals("kill")&&kind.equals("listener")){
        deletelistener(request,className);
    }
    else if(action != null && action.equals("kill")&&kind.equals("valve")){
        deletevalve(request,className);
    }
    else if(action != null && action.equals("dump") && className != null){
        Process pr = Runtime.getRuntime().exec(javaexe + " -jar " + dumpclassjar + " -p " + pid + " -o " + outputdir +" " +className);
        pr.waitFor();
        BufferedReader in = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
        String line;
        while ((line = in.readLine()) != null) {
            out.println(line);
        }
        String code = null;
        try {
            code = codecheckanddump(Detecter,className,outputdir,detect);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        out.write("save in " + outputdir + "<br>");
        if(code!=null)
        {   if(kind.equals("javaagent_memshell")){
            out.print("<font color=red >you need to check the code youself</font><br/>");
        }
            else if(code.substring(0,9).equals("Dangerous"))
            {
                out.print("<font color=red >!!!detected!!!</font><br/>");
            }
            out.println(code.substring(9));
        }
        else{
            out.println(code);
            out.write("fail to dump");
        }
    } else {
        out.write("<table border=\"1\" cellspacing=\"0\" width=\"95%\" style=\"table-layout:fixed;word-break:break-all;background:#f2f2f2\">\n" +
                "    <thead>\n" +
                "        <th width=\"10%\">memshell_type</th>\n" +
                "        <th width=\"10%\">Patern</th>\n" +
                "        <th width=\"20%\">class</th>\n" +
                "        <th width=\"20%\">classLoader</th>\n" +
                "        <th width=\"25%\">class file path</th>\n" +
                "        <th width=\"5%\">dump class</th>\n" +
                "        <th width=\"5%\">kill</th>\n" +
                "    </thead>\n" +
                "    <tbody>");
        for (Class cls : clazzes) {
            String classname = cls.getName();
            if (classname.contains("@") || classname.contains("[") || classname.contains("$"))
                continue;

            if (blacklistcheck(classnamecheck, classname)) {//detecter.dump(classname);
                System.out.println("memshell found" + classname);
                continue;
            }
            if (jspcheck(classnamecheck, classname)) {
                System.out.println(classname + " jsp found");
                if (hasFile(file,jsppath, classname.substring(15)))//判断类是否有对应本地jsp文件
                {
                    out.write(String.format("<td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td style=\"text-align:center\"><a href=\"?action=dump&className=%s&kind=jsp_memshell\">dump</a></td><td style=\"text-align:center\"><a href=\"?action=kill&className=%s\">kill</a></td>"
                            , "jsp_memshell"
                            , "null"
                            , classname
                            , cls.getClassLoader().getClass().getName()
                            , classFileIsExists(Detecter, detect, cls)
                            , classname
                            , "null"));
                    out.write("</tr>");
                    if (!classFileIsExists(Detecter, detect, cls)) //判断类是否有对应本地class文件,有待完善，待添加白名单
                        riskmap.put(classname, cls);
                    continue;
                }
                continue;
            }
            if (!classFileIsExists(Detecter, detect, cls) &&!classname.contains("jdk.internal.reflect.GeneratedMethodAccessor") && !classname.contains("sun.reflect.Generated") && !classname.contains("com.sun.jmx") && !classname.contains("com.intellij.")) {//判断类是否有对应本地文件,白名单sun.reflect.Generated为调用反射时会生成的类，com.sun.jmx.remote为idea调用的类
                System.out.println(classname + "   dangerous!!!");
                out.write(String.format("<td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td style=\"text-align:center\"><a href=\"?action=dump&className=%s&kind=noclass_memshell\">dump</a></td><td style=\"text-align:center\"><a href=\"?action=kill&className=%s\">kill</a></td>"
                        , "noclass_memshell"
                        , "null"
                        , classname
                        , cls.getClassLoader().getClass().getName()
                        , classFileIsExists(Detecter, detect, cls)
                        , classname
                        , "null"));
                out.write("</tr>");
                riskmap.put(classname, cls);
                continue;
            }
            if (interfacecheck(classnamecheck,cls)) {
                System.out.println(classname + " riskinterface found");

                out.write(String.format("<td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td style=\"text-align:center\"><a href=\"?action=dump&className=%s&kind=javaagent_memshell\">dump</a></td><td style=\"text-align:center\"><a href=\"?action=kill&className=%s\">kill</a></td>"
                        , "javaagent_memshell"
                        , "null"
                        , classname
                        , cls.getClassLoader().getClass().getName()
                        , "interface" + cls.getInterfaces().getClass().getName()
                        , classname
                        , "null"));
                out.write("</tr>");
                riskmap.put(classname, cls);
            }
        }
        //tomcat内存马探测
        HashMap<String, Object> filterConfigs = getFilterConfig(request);
        Object[] filterMaps1 = getFilterMaps(request);
        for (int i = 0; i < filterMaps1.length; i++) {
            out.write("<tr>");
            Object fm = filterMaps1[i];
            Object appFilterConfig = filterConfigs.get(getFilterName(fm));
            if (appFilterConfig == null) {
                continue;
            }
            Field _filter = appFilterConfig.getClass().getDeclaredField("filter");
            _filter.setAccessible(true);
            Object filter = _filter.get(appFilterConfig);
            String filterClassName = filter.getClass().getName();
            String filterClassLoaderName = filter.getClass().getClassLoader().getClass().getName();
            out.write(String.format("<td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td style=\"text-align:center\"><a href=\"?action=dump&className=%s&kind=filter\">dump</a></td><td style=\"text-align:center\"><a href=\"?action=kill&className=%s&kind=filter\">kill</a></td>"
                    , getFilterName(fm)
                    , null
                    , filterClassName
                    , filterClassLoaderName
                    , classFileIsExists(Detecter, detect, filter.getClass())
                    , filterClassName
                    , getFilterName(fm)));
            out.write("</tr>");
        }
        HashMap<String, Object> children = getChildren(request);
        Map<String, String> servletMappings = getServletMaps(request);

        int servletId = 0;
        for (Map.Entry<String, String> map : servletMappings.entrySet()) {
            String servletMapPath = map.getKey();
            String servletName1 = map.getValue();
            StandardWrapper wrapper = (StandardWrapper) children.get(servletName1);

            Class servletClass = null;
            try {
                servletClass = Class.forName(wrapper.getServletClass());
            } catch (Exception e) {
                Object servlet = wrapper.getServlet();
                if (servlet != null) {
                    servletClass = servlet.getClass();
                }
            }
            if (servletClass != null) {
                out.write("<tr>");
                String servletClassName = servletClass.getName();
                String servletClassLoaderName = null;
                try {
                    servletClassLoaderName = servletClass.getClassLoader().getClass().getName();
                } catch (Exception e) {
                }
                out.write(String.format("<td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td style=\"text-align:center\"><a href=\"?action=dump&className=%s&kind=servlet\">dump</a></td><td style=\"text-align:center\"><a href=\"?action=kill&className=%s&kind=servlet\">kill</a></td>"
                        , servletName1
                        , servletMapPath
                        , servletClassName
                        , servletClassLoaderName
                        , classFileIsExists(Detecter, detect, servletClass)
                        , servletClassName
                        , servletName1));
                out.write("</tr>");
            }
            servletId++;
        }

        CopyOnWriteArrayList<EventListener> applicationEventlistenersList = getListeners(request);
        int listenerID = 0;
        for (EventListener eventListener : applicationEventlistenersList) {
            String eventListenername = eventListener.getClass().getName();
            String eventListenerloader = eventListener.getClass().getClassLoader().getClass().getName();
            out.write(String.format("<td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td style=\"text-align:center\"><a href=\"?action=dump&className=%s&kind=listener\">dump</a></td><td style=\"text-align:center\"><a href=\"?action=kill&className=%s&kind=listener\">kill</a></td>"
                    , "listener"
                    , '/'
                    , eventListenername
                    , eventListenerloader
                    , classFileIsExists(Detecter, detect, eventListener.getClass())
                    , eventListenername
                    , eventListenername));
            out.write("</tr>");
            listenerID++;
        }
        Pipeline pipeline = getPipeline(request);
        Valve valve = pipeline.getFirst();
        int valveID = 0;
        while (valve != null) {
            String valvename = valve.getClass().getName();
            String valveloadername = valve.getClass().getClassLoader().getClass().getName();
            out.write(String.format("<td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td style=\"text-align:center\"><a href=\"?action=dump&className=%s&kind=valve\">dump</a></td><td style=\"text-align:center\"><a href=\"?action=kill&className=%s&kind=valve\">kill</a></td>"
                    , "valve"
                    , '/'
                    , valvename
                    , valveloadername
                    , classFileIsExists(Detecter, detect, valve.getClass())
                    , valvename
                    , valvename));
            out.write("</tr>");
            valveID++;
            valve = valve.getNext();
        }
        //Timer
        /*ThreadGroup group = Thread.currentThread().getThreadGroup();
        ThreadGroup topGroup = group;
        // 遍历线程组树，获取根线程组
        while (group != null) {
            topGroup = group;
            group = group.getParent();
        }
        // 激活的线程数加倍
        int estimatedSize = topGroup.activeCount() * 2;
        Thread[] slackList = new Thread[estimatedSize];
        // 获取根线程组的所有线程
        int actualSize = topGroup.enumerate(slackList);
        // copy into a list that is the exact size
        Thread[] list = new Thread[actualSize];
        System.arraycopy(slackList, 0, list, 0, actualSize);
        System.out.println("Thread list size == " + list.length);
        for (Thread thread : list) {
            System.out.println(thread.getName());
            if(thread.getName().contains("Timer")){

                out.write(String.format("<td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td style=\"text-align:center\"><a href=\"?action=dump&className=%s&kind=valve\">dump</a></td><td style=\"text-align:center\"><a href=\"?action=kill&servletName=%s\">kill</a></td>"
                        , "Timer线程"
                        , '/'
                        , thread.getName()
                        , thread.getClass().getClassLoader().getClass().getName()
                        , classFileIsExists(Detecter, detect, thread.getClass())
                        , thread.getName()
                        , "null"));
                out.write("</tr>");
            }
        }*/

    }
%>
<html>
<head>
    <title>Title</title>
</head>
<body>

</body>
</html>
