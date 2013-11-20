package cz.cuni.mff.xrg.odcs.commons.app.pipeline;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ExecutionContextInfo;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineFacade;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.Schedule;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.ScheduleFacade;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

/**
 * Test suite for pipeline facade interface.
 * Each test is run in own transaction, which is rolled back in the end.
 * 
 * @author Jan Vojt
 */
@ContextConfiguration(locations = {"classpath:commons-app-test-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback=true)
public class PipelineFacadeTest {
	
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private PipelineFacade facade;
	
	@Autowired
	private ScheduleFacade scheduler;
	
	@Test
	@Transactional
	public void testCreatePipeline() {
		
		Pipeline pipe = facade.createPipeline();
		
		assertNotNull(pipe);
		assertNotNull(pipe.getGraph());
		assertNull(pipe.getName());
	}
	
	@Test
	@Transactional
	public void testPersistPipeline() {
		
		Pipeline[] pipes = new Pipeline[3];
		for (int i = 0; i<3; i++) {
			pipes[i] = facade.createPipeline();
			facade.save(pipes[i]);
		}
		
		em.flush();

		for (int i = 0; i<3; i++) {
			assertNotNull(facade.getPipeline(pipes[i].getId()));
		}
	}
	
	@Test
	@Transactional
	public void testGetAllExecutions() {
		List<PipelineExecution> execsPrev = facade.getAllExecutions(PipelineExecutionStatus.CANCELLING);
		assertNotNull(execsPrev);
		
		Pipeline pipe = facade.createPipeline();
		PipelineExecution exec = facade.createExecution(pipe);
		exec.setStatus(PipelineExecutionStatus.CANCELLING);
		
		facade.save(pipe);
		facade.save(exec);
		
		List<PipelineExecution> execs = facade.getAllExecutions(PipelineExecutionStatus.CANCELLING);
		assertNotNull(execs);
		assertEquals(execsPrev.size() + 1, execs.size());
	}
	
	@Test
	@Transactional
	public void testExecutionsOfPipeline() {
		Pipeline pipe = facade.createPipeline();
		PipelineExecution exec = facade.createExecution(pipe);
		
		facade.save(pipe);
		facade.save(exec);
		
		List<PipelineExecution> execs = facade.getExecutions(pipe);
		
		assertNotNull(execs);
		assertEquals(1, execs.size());
		assertEquals(exec, execs.get(0));
	}
	
	@Test
	@Transactional
	public void testDeletePipeline() {
		
		Pipeline[] pipes = new Pipeline[3];
		for (int i = 0; i<3; i++) {
			pipes[i] = facade.createPipeline();
			facade.save(pipes[i]);
		}
		
		facade.delete(pipes[1]);
		
		em.flush();

		assertEquals(pipes[0], facade.getPipeline(pipes[0].getId()));
		assertNull(facade.getPipeline(pipes[1].getId()));
		assertEquals(pipes[2], facade.getPipeline(pipes[2].getId()));
	}
	
	@Test
	@Transactional
	public void testDeepDeletePipeline() {
		
		long pid = 1;
		Pipeline pipe = facade.getPipeline(pid);
		assertNotNull(pipe);
		List<PipelineExecution> execs = facade.getExecutions(pipe);
		List<Schedule> jobs = scheduler.getSchedulesFor(pipe);
		
		facade.delete(pipe);
		
		// Cascading of deletes is happenning on DB level, so we need to flush
		// changes to DB and clear netityManager to reread from DB.
		em.flush();
		em.clear();
		
		// make sure pipeline was deleted
		assertNull(facade.getPipeline(pid));
		
		// check that all pipeline executions were deleted
		for (PipelineExecution exec : execs) {
			assertNull(facade.getExecution(exec.getId()));
		}
		
		// check that all scheduled jobs were deleted
		for (Schedule job : jobs) {
			assertNull(scheduler.getSchedule(job.getId()));
		}
	}
	
	@Test
	@Transactional
	public void testPipelineList() {
		
		List<Pipeline> pipes = facade.getAllPipelines();
		
		for (int i = 0; i<3; i++) {
			Pipeline newPpl = facade.createPipeline();
			pipes.add(newPpl);
			facade.save(newPpl);
		}
		
		// refetch entities
		List<Pipeline> resPipes = facade.getAllPipelines();
		
		// test
		assertEquals(pipes.size(), resPipes.size());
		for (Pipeline pipe : pipes) {
			assertTrue(resPipes.contains(pipe));
		}
	}

	@Test
	@Transactional
	public void testExecutionsContext() {
		Pipeline pipe = facade.createPipeline();
		PipelineExecution exec = facade.createExecution(pipe);		
		facade.save(pipe);
		facade.save(exec);
		em.flush();
		
		// create context
		ExecutionContextInfo context = exec.getContext();
		
		assertNotNull(exec.getContext());
		assertNotNull(context.getId());		
	}
	
	@Test
	@Transactional
	public void testGetPipelinesUsingDPU() {
		DPUTemplateRecord dpu = new DPUTemplateRecord();
		dpu.setId(1L);
		
		List<Pipeline> pipes = facade.getPipelinesUsingDPU(dpu);
		
		assertNotNull(pipes);
		assertEquals(1, pipes.size());
		assertEquals("Test 1", pipes.get(0).getName());
	}
	
	@Test
	@Transactional
	public void testGetPipelinesUsingUnusedDPU() {
		DPUTemplateRecord dpu = new DPUTemplateRecord();
		dpu.setId(2L);
		
		List<Pipeline> pipes = facade.getPipelinesUsingDPU(dpu);
		
		assertNotNull(pipes);
		assertEquals(0, pipes.size());
	}
	
	@Test
	@Transactional
	public void testCopyPipeline() {
		
		Pipeline ppl = new Pipeline();
		ppl.setName("pplName");
		ppl.setDescription("pplDesc");
		
		Pipeline nPpl = facade.copyPipeline(ppl);

		String newName = "Copy of " + ppl.getName();
		assertNotSame(ppl, nPpl);
		assertEquals(newName, nPpl.getName());
		assertEquals(ppl.getDescription(), nPpl.getDescription());
	}
	
	@Test
	@Transactional
	public void testDuplicateCopyPipeline() {
		
		Pipeline ppl = new Pipeline();
		ppl.setName("pplName");
		ppl.setDescription("pplDesc");
		
		Pipeline nPpl = facade.copyPipeline(ppl);

		// test copying for the first time
		String newName = "Copy of " + ppl.getName();
		assertNotSame(ppl, nPpl);
		assertEquals(newName, nPpl.getName());
		assertEquals(ppl.getDescription(), nPpl.getDescription());

		Pipeline nPpl1 = facade.copyPipeline(ppl);
		
		// test copying for the second time
		String newName1 = "Copy of " + ppl.getName() + " #1";
		assertNotSame(ppl, nPpl1);
		assertEquals(newName1, nPpl1.getName());
		assertEquals(ppl.getDescription(), nPpl1.getDescription());
	}
}
