# NoAgent-memshell-scanner
NoAgent内存马检测程序 

使用环境 java8 + tomcat

使用方法：将NoAgent.jar文件与dll文件放到lib目录下，并更新detect.jsp中的各文件位置。然后将其放到服务器上运行即可。

在filter和servlet内存马检测参考了tomcat-memshell-scanner.jsp项目

检测范围 

  agent型内存马
  
  tomcat filter型，servlet型，listener型，valve型
  
  spring interceptor,controller型内存马已更新

添加了非agent型内存马的kill功能。
  
  
