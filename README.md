# Targetprocess and Testng

This is a lib that enables you to create test plan runs for your integration test cases on Target Process.

You just have to use TestNG and use maven failsafe-plugin.


### Usage

     
1. Install the artifact (targetprocess-testng) on your maven repo.

        mvn clean install
        
2. Then on your project containing the integration tests, setup your pom.xml with the necessary dependencies like the following one:

        <dependencies>
            <dependency>
                <groupId>org.testng</groupId>
                <artifactId>testng</artifactId>
                <version>LATEST</version>
                <scope>test</scope>
            </dependency>
            <dependency>
                <groupId>pt.drsoares.plugins</groupId>
                <artifactId>targetprocess-testng</artifactId>
                <version>1.0-SNAPSHOT</version>
                <scope>test</scope>
                <type>test-jar</type>
            </dependency>
        </dependencies>
    
        <build>
            <plugins>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-failsafe-plugin</artifactId>
                    <executions>
                        <execution>
                            <id>integration-test</id>
                            <goals>
                                <goal>integration-test</goal>
                                <goal>verify</goal>
                            </goals>
                        </execution>
                    </executions>
                    <configuration>
                        <testSourceDirectory>src/test/java/pt/drsoares/plugins/integrationtests/</testSourceDirectory>
                        <properties>
                            <property>
                                <name>listener</name>
                                <value>pt.drsoares.plugins.targetprocess.TestCaseListener</value>
                            </property>
                        </properties>
                        <systemPropertyVariables>
                            <targetProcessUrl>https://targetprocesshostname</targetProcessUrl>
                            <targetProcessUser>username</targetProcessUser>
                            <targetProcessPassword>password</targetProcessPassword>
                        </systemPropertyVariables>
                    </configuration>
                </plugin>
            </plugins>
        </build>
        
3. Add the `TestCase` annotation with the respective id (and testPlan id (Optional)) to your Integration Test
    
        package pt.drsoares.plugins.integrationtests;
        
        import org.testng.annotations.Test;
        import pt.drsoares.plugins.targetprocess.annotations.TestCase;

        import static org.testng.Assert.assertTrue;

        public class DummyIT {

            @TestCase(id = "111959", testPlan = "111958")
            @Test
            public void test() {
                ...
            }
        }
        
4. Run your integration tests

        mvn failsafe:integration-test

5. After that you'll notice that your test Test Case on Target Process will have a new Test Plan Run with the result of your ITest.