package net.vsspl.services;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.vsspl.to.Transaction;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.DecisionTableInputType;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;

import com.google.gson.Gson;

/**
 * This is a sample class to launch a rule.
 */
@Path("/Rules")
public class DroolsTest {
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFileNames(String data, @Context HttpServletRequest request) {
		Gson gson = new Gson();
		Transaction transaction = null;
		try {
			
			KnowledgeBase kbase = readKnowledgeBase("Sample.drl");
            StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
            KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "test");
            transaction = gson.fromJson(data, Transaction.class);
            ksession.insert(transaction);
            ksession.fireAllRules();
            logger.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
		
		return Response.status(201).entity(transaction.getDocuments()).build();
	}
	
	/*@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFiles(@Context HttpServletRequest request,@Suspended final AsyncResponse asyncResponse) {
		 asyncResponse.setTimeoutHandler(new TimeoutHandler() {

        @Override
        public void handleTimeout(AsyncResponse asyncResponse) {
            asyncResponse.resume(Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("Operation time out.").build());
        	}
    	});
		 
    	asyncResponse.setTimeout(20, TimeUnit.SECONDS);
		
	        new Thread(new Runnable() { 
	            public void run() {
	                String result = veryExpensiveOperation();
	                asyncResponse.resume(result);
	            }
	 
	            private String veryExpensiveOperation() {
	                // ... very expensive operation
	            }
	        }).start();
		
		return Response.status(201).entity("working").build();
	}*/

    public static final void main(String[] args) {
        try {

        	/*KnowledgeBase kbase = readKnowledgeBase("DecisionTable.xls");
            StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
            KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "test");*/
        	KnowledgeBase kbase = readKnowledgeBase("Sample.drl");
            StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
            KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "test");
            Transaction transaction = new Transaction();
            transaction.setFromUser("farmer");
            transaction.setToUser("trader");
            transaction.setState("Karnataka");
            ksession.insert(transaction);
            ksession.fireAllRules();
            logger.close();
            
            System.out.println(transaction.getDocuments());
            
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
	
	/*public static void main(String[] args) {

        // works even without -SNAPSHOT versions
        String url = "http://localhost:8010/org/documentgeneration/1.0/documentgeneration-1.0.jar";

        // make sure you use "LATEST" here!
        ReleaseIdImpl releaseId = new ReleaseIdImpl("org", "documentgeneration", "1.0");

        KieServices ks = KieServices.Factory.get();

        ks.getResources().newUrlResource(url);

        KieContainer kieContainer = ks.newKieContainer(releaseId);

        // check every 5 seconds if there is a new version at the URL
        KieScanner kieScanner = ks.newKieScanner(kieContainer);
        kieScanner.start(5000L);
        // alternatively:
        // kieScanner.scanNow();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            runRule(kieContainer);
            System.out.println("Press enter in order to run the test again....");
            scanner.nextLine();
        }
    }*/
    
	 private static void runRule(KieContainer kieKontainer) {
	        StatelessKieSession kSession = kieKontainer.newStatelessKieSession("testSession");
	        kSession.setGlobal("out", System.out);
	        kSession.execute("testRuleAgain");
	    }
    
    private static KnowledgeBase readKnowledgeBase(String filename) throws Exception {/*
    	DecisionTableConfiguration dtableconfiguration =

        	    KnowledgeBuilderFactory.newDecisionTableConfiguration();

        	dtableconfiguration.setInputType( DecisionTableInputType.XLS );
    	
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource(filename,DroolsTest.class), ResourceType.DTABLE, dtableconfiguration);
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if (errors.size() > 0) {
            for (KnowledgeBuilderError error: errors) {
                System.err.println(error);
            }
            throw new IllegalArgumentException("Could not parse knowledge.");
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        
        return kbase;
    */

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource(filename), ResourceType.DRL);
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if (errors.size() > 0) {
            for (KnowledgeBuilderError error: errors) {
                System.err.println(error);
            }
            throw new IllegalArgumentException("Could not parse knowledge.");
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        return kbase;
    
    	}
}
