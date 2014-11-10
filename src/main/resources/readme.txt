=======================
 
  pom.xml 配置方法 

=======================
			<plugin>
				<groupId>com.codegen</groupId>
				<artifactId>ibatisgen</artifactId>
				<version>1.0</version>
				<executions>
					<execution>
						<configuration> 
						
						    <!-- 默认值 是 com.mysql.jdbc.Driver -->
						    <driverClass>com.mysql.jdbc.Driver</driverClass>
							<dbUrl>jdbc:mysql://127.0.0.1:3306/bbb</dbUrl>
							
							<dbUser>root</dbUser>
							<dbPassword></dbPassword>
							
							<!-- 默认是 % 全部，支持前缀形式 比如：user_% -->
							<tbNamePattern>%</tbNamePattern>
							
							<modelPkg>com.bbb.model</modelPackage>
							<daoPkg>com.bbb.dao</daoPkg>
							<xmlPkg>com.bbb.sqlmap</xmlPkg>
							
							<!-- 默认是 工程 src/main/java 目录 -->
							<srcFolder>${project.build.sourceDirectory}</srcFolder>
							
							<!-- 基础类的设置 -->
							<baseObjectClass>com.bbb.BaseObject</baseObjectClass>
							<pkClass>com.bbb.annotation.PrimaryKey</pkClass>
							<genericDaoClass>com.bbb.ibatis.GenericDaoiBatis</genericDaoClass>
							
						</configuration>
					</execution>
				</executions>
			</plugin>
			
==================

  执行 plugin 的方法 

==================			

           mvn ibatisgen:db2code 
           mvn jadegen:db2code  
           



