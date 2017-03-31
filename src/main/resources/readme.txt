=======================
 
  pom.xml 配置方法 

=======================
            <plugin>
                <groupId>com.codegen</groupId>
                <artifactId>maven-code-plugin</artifactId>
                <version>1.0.0</version>
                <configuration>
                    <!-- 默认值 是 com.mysql.jdbc.Driver -->
                    <driverClass>com.mysql.jdbc.Driver</driverClass>
                    <dbUrl>jdbc:mysql://10.100.13.238:3306/test_caifu</dbUrl>

                    <dbUser>dev_select_licai</dbUser>
                    <dbPassword>d!e@v#_select_licai</dbPassword>

                    <!-- 默认是 % 全部，支持前缀形式 比如：user_% -->
                    <tbNamePattern>%</tbNamePattern>
                    <genType>1</genType>

                    <modelPkg>com.hc.henghuirong.server.dao.test</modelPkg>
                    <daoPkg>com.hc.henghuirong.server.dao.test</daoPkg>
                    <wikiPkg>com.hc.henghuirong.server.dao.test</wikiPkg>
                    <!--<xmlPkg>com.bbb.sqlmap</xmlPkg>-->

                    <!-- 默认是 工程 src/main/java 目录 -->
                    <srcFolder>${project.build.sourceDirectory}</srcFolder>

                    <!-- 基础类的设置 -->
                    <baseObjectClass>com.bbb.BaseObject</baseObjectClass>
                    <pkClass>com.bbb.annotation.PrimaryKey</pkClass>


                </configuration>
            </plugin>
			
==================

  执行 plugin 的方法 

==================			

           mvn ibatisgen:db2code 
           mvn jadegen:db2code  
           



