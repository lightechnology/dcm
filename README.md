# dcm
Data Centre Manager
基于Netty5的支持多种网络通信格式的数据中心服务系统，动态加载网络数据接口和编解码器

------------------------代码风格自动检查-----------------------
目录config下有工具文件，需要执行安装文件install才能生效
checkstyle*.jar文件是核心执行文件
check.xml是配置文件，代码格式风格可以看配置文件了解，通过eclipse代码格式化功能基本符合这个要求的
有部分代码内容要求，要细看
pre-commit是git hooks执行脚本，代码在git commit时会自动执行检查，并输出如下例子内容提示
"
.......................Code Style Checking....................

Starting audit...
[WARN] /home/adol/workspace/eclipse/maven/java/xcmd-v2.1/dcm/src/main/java/org/bdc/dcm/netty/piple/HyChannelPiple.java:13: 'member def modifier' 缩进了8个缩进符，应为4个。 [Indentation]
[WARN] /home/adol/workspace/eclipse/maven/java/xcmd-v2.1/dcm/src/main/java/org/bdc/dcm/netty/piple/HyChannelPiple.java:13:1: 文件含有制表符 tab （这只是第一例）。 [FileTabCharacter]
[WARN] /home/adol/workspace/eclipse/maven/java/xcmd-v2.1/dcm/src/main/java/org/bdc/dcm/netty/piple/HyChannelPiple.java:15: 'ctor def modifier' 缩进了8个缩进符，应为4个。 [Indentation]
[WARN] /home/adol/workspace/eclipse/maven/java/xcmd-v2.1/dcm/src/main/java/org/bdc/dcm/netty/piple/HyChannelPiple.java:16: 'ctor def' 子元素缩进了16个缩进符，应为8个。 [Indentation]
[WARN] /home/adol/workspace/eclipse/maven/java/xcmd-v2.1/dcm/src/main/java/org/bdc/dcm/netty/piple/HyChannelPiple.java:17: 'ctor def rcurly' 缩进了8个缩进符，应为4个。 [Indentation]
[WARN] /home/adol/workspace/eclipse/maven/java/xcmd-v2.1/dcm/src/main/java/org/bdc/dcm/netty/piple/HyChannelPiple.java:18: 'method def modifier' 缩进了8个缩进符，应为4个。 [Indentation]
[WARN] /home/adol/workspace/eclipse/maven/java/xcmd-v2.1/dcm/src/main/java/org/bdc/dcm/netty/piple/HyChannelPiple.java:18:64: '{' 前应有空格。 [WhitespaceAround]
[WARN] /home/adol/workspace/eclipse/maven/java/xcmd-v2.1/dcm/src/main/java/org/bdc/dcm/netty/piple/HyChannelPiple.java:22: 'method def' 子元素缩进了16个缩进符，应为8个。 [Indentation]
[WARN] /home/adol/workspace/eclipse/maven/java/xcmd-v2.1/dcm/src/main/java/org/bdc/dcm/netty/piple/HyChannelPiple.java:26: 'method def rcurly' 缩进了8个缩进符，应为4个。 [Indentation]
Audit done.


.......................You must fix the errors and warnings first, then excute commit command again...........
"