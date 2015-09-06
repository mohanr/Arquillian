package com.arquillian.jms.e2e;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.jms.Message;
import javax.jms.TextMessage;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.spring.integration.test.annotation.SpringConfiguration;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.formatter.Formatters;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

@RunWith(Arquillian.class)
@SpringConfiguration("applicationContext.xml")
public class ShrinkWrappedMessageTest {

	private static Logger l = Logger.getLogger("Archive");
	
	@Autowired
	private JmsTemplate consumerJmsTemplate;
	
	@Autowired
	private MessageSender messageSender;
	
	
    @Deployment
    public static WebArchive createExistingWebArchive() {
    	WebArchive war = ShrinkWrap.create( ZipImporter.class,
	            "ShrinkWrap.war")
	            .importFrom(new File("D:/arquillian/ExistingShrinkWrap.war"))
	             .as(WebArchive.class) ;
		return war;
    }
    
    //@Deployment
    public static WebArchive createWebArchive() {
  
    	final WebArchive war=ShrinkWrap.create(WebArchive.class,"ShrinkWrap.war");
    	  
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class)
                				//.addPackage("com.arquillian.jms.e2e");
        						  .addClass("com.arquillian.jms.e2e.MessageSender")
	       						  .addClass("com.arquillian.jms.e2e.MessageSenderImpl")
	       						  .addClass("com.arquillian.jms.e2e.ShrinkWrappedMessageTest");

        war.addAsLibrary(jar);
    	war.addAsResource("applicationContext-another.xml");
    	war.addAsResource("applicationContext.xml");
    	war.addAsResource("spring-beans-embedded.xml");
    	war.addAsResource("arquillian.xml");
    	war.addAsResource("log4j.xml");

    	final EnterpriseArchive ear=ShrinkWrap.create(EnterpriseArchive.class,"ShrinkWrap.ear");

    	loadDependencies( war );
 
    	l.info(war.toString(Formatters.VERBOSE));
    	return war;
    }
    
    private static void dumpContents(  final WebArchive war  ){

	     String file = null;
	   	 for( Entry<ArchivePath, Node> content : war.getContent().entrySet()){
	   		file = content.getValue().toString();
	       		l.info("WAR [" + content.getValue().toString() + "]");
	   	}
    }
    
/*    private static void loadDependenciesFromPOM(  final WebArchive war ){
    	File[] libs = Maven.
							resolver().
								loadPomFromFile("pom.xml").
									importTestDependencies().
											resolve().
												withTransitivity().asFile();
        war.addAsLibraries(libs);
   }
*/    
    private static void loadDependencies( final WebArchive war ){
    	
        File xbean = Maven.
				resolver().
					resolve("org.apache.xbean:xbean-spring:4.3")
						.withoutTransitivity().asSingle(File.class);

        war.addAsLibraries(xbean);

        war.addAsLibraries(xbean);
        File springjms = Maven.
				resolver().
					resolve("org.springframework:spring-jms:4.1.1.RELEASE")
						.withoutTransitivity().asSingle(File.class);

        war.addAsLibraries(springjms);

        File springexpression = Maven.
				resolver().
					resolve("org.springframework:spring-expression:4.2.0.RELEASE")
						.withoutTransitivity().asSingle(File.class);

        war.addAsLibraries(springexpression);

        File springweb = Maven.
				resolver().
					resolve("org.springframework:spring-web:4.2.0.RELEASE")
						.withoutTransitivity().asSingle(File.class);

        war.addAsLibraries(springweb);

        File springcore = Maven.
				resolver().
					resolve("org.springframework:spring-core:4.1.1.RELEASE")
						.withoutTransitivity().asSingle(File.class);

        war.addAsLibraries(springcore);
        
        File springcontext = Maven.
				resolver().
					resolve("org.springframework:spring-context:4.1.1.RELEASE")
						.withoutTransitivity().asSingle(File.class);

        war.addAsLibraries(springcontext);

        File extensionspring = Maven.
				resolver().
					resolve("org.jboss.arquillian.extension:arquillian-service-deployer-spring-3:1.0.0.Beta3")
						.withoutTransitivity().asSingle(File.class);

        war.addAsLibraries(extensionspring);


        File springbeans = Maven.
				resolver().
					resolve("org.springframework:spring-beans:4.1.1.RELEASE")
						.withoutTransitivity().asSingle(File.class);

        war.addAsLibraries(springbeans);

        File activemqall = Maven.
				resolver().
					resolve("org.apache.activemq:activemq-all:5.11.1")
					.withoutTransitivity().asSingle(File.class);

        war.addAsLibraries(activemqall);

        File springaop = Maven.
				resolver().
					resolve("org.springframework:spring-aop:4.2.0.RELEASE")
					.withoutTransitivity().asSingle(File.class);

        war.addAsLibraries(springaop);

        File springtx = Maven.
				resolver().
					resolve("org.springframework:spring-tx:4.1.1.RELEASE")
					.withoutTransitivity().asSingle(File.class);

        war.addAsLibraries(springtx);

    }


    @Test
    public void checkJmsTemplate() throws Exception {
    	assertNotNull(consumerJmsTemplate);
    	messageSender.sendMessage("Test");
    	Message result = consumerJmsTemplate.receive("test");
    	l.info( ((TextMessage)result).getText());
    }   
}