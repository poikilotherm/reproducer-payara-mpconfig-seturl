package com.example.it;

import com.example.DataSourceProducer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.io.File;
import java.util.logging.Logger;

@ExtendWith(ArquillianExtension.class)
//@org.junit.jupiter.api.Disabled
public class DataSourceProducerTest {
    private final static Logger LOGGER = Logger.getLogger(DataSourceProducerTest.class.getName());
    
    @Deployment
    public static WebArchive createDeployment() {
        
        // Import Postgres Driver from Maven
        // Taken from https://cassiomolin.com/2015/06/07/adding-maven-dependencies-to-arquillian-test/
        File[] files = Maven.resolver()
                            .loadPomFromFile("pom.xml")
                            .importRuntimeDependencies()
                            .resolve("org.postgresql:postgresql")
                            .withTransitivity()
                            .asFile();
        
        return ShrinkWrap.create(WebArchive.class)
                .addClass(DataSourceProducer.class)
                .addAsLibraries(files)
                .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }
    
    @Inject
    DataSource dataSource;
    
    @PersistenceContext
    EntityManager em;
    
    @Test
    public void emptyTest() {
        Assertions.assertEquals("hello", System.getProperty("db.host"));
    }
}
