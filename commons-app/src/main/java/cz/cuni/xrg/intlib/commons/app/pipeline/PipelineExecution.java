package cz.cuni.xrg.intlib.commons.app.pipeline;

import cz.cuni.xrg.intlib.commons.app.module.ModuleFacade;

/**
 * Information about executed pipeline and their states.
 *
 * @author Jiri Tomes
 * @author Jan Vojt
 */
public class PipelineExecution /*, ApplicationEventPublisherAware*/ {

    /**
     * Actual status for executed pipeline.
     */
    private ExecutionStatus status;
    /**
     * Pipeline for executing.
     */
    private Pipeline pipeline;
    /**
     * Publisher instance responsible for publishing pipeline execution events.
     */
//    private ApplicationEventPublisher eventPublisher;
    /**
     * Module facade for fetching executable DPUs through OSGi.
     */
    private ModuleFacade moduleFacade;

    /**
     * Constructor
     *
     * @param pipeline
     */
    public PipelineExecution(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    public ExecutionStatus getExecutionStatus() {
        return status;
    }

    public void setExecutionStatus(ExecutionStatus newStatus) {
        status = newStatus;
    }

    /**
     * @return the pipeline
     */
    public Pipeline getPipeline() {
        return pipeline;
    }

    /**
     * @param pipeline the pipeline to set
     */
    public void setPipeline(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    /**
     *
     * @return moduleFacade
     */
    public ModuleFacade getModuleFacade() {
        return moduleFacade;
    }

    /**
     * @param moduleFacade the moduleFacade to set
     */
    public void setModuleFacade(ModuleFacade moduleFacade) {
        this.moduleFacade = moduleFacade;
    }

//	@Override
//	public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
//		this.eventPublisher = publisher;
//	}
    /**
     * Runs the pipeline.
     */
    /*
     public void run() {

     long pipelineStart = System.currentTimeMillis();
     DependencyGraph dependencyGraph = new DependencyGraph(pipeline.getGraph());
     eventPublisher.publishEvent(new PipelineStartedEvent(pipeline, runId, this));

     LocalRepo repository = LocalRepo.createLocalRepo();
     repository.cleanAllRepositoryData();

     for (Node node : dependencyGraph) {
     runNode(node, repository);
     }

     long duration = System.currentTimeMillis() - pipelineStart;
     eventPublisher.publishEvent(
     new PipelineCompletedEvent(duration, pipeline, runId, this)
     );
     }*/
    /**
     * Executes a general node (ETL) in pipeline graph.
     *
     * @param node
     * @param repo
     */
    /*
    private void runNode(Node node, LocalRepo repo) {

        DPUInstance dpuInstance = node.getDpuInstance();
        DPU dpu = dpuInstance.getDpu();

        Type dpuType = dpu.getType();
        String dpuJarPath = dpu.getJarPath();
        Configuration configuration = dpuInstance.getInstanceConfig();

        switch (dpuType) {
            case EXTRACTOR: {
                Extract extractor = moduleFacade.getInstanceExtract(dpuJarPath);
                extractor.setSettings(configuration);
                extractor.setLocalRepo(repo);
                ExtractContext ctx = new ExtractContext(runId, new HashMap<String, Object>());
                runExtractor(extractor, ctx);
                break;
            }
            case TRANSFORMER: {
                Transform transformer = moduleFacade.getInstanceTransform(dpuJarPath);
                transformer.setSettings(configuration);
                transformer.setLocalRepo(repo);
                TransformContext ctx = new TransformContext(runId, new HashMap<String, Object>());
                runTransformer(transformer, ctx);
                break;
            }
            case LOADER: {
                Load loader = moduleFacade.getInstanceLoader(dpuJarPath);
                loader.setSettings(configuration);
                loader.setLocalRepo(repo);
                LoadContext ctx = new LoadContext(runId, new HashMap<String, Object>());
                runLoader(loader, ctx);
                break;
            }
            default:
                throw new RuntimeException("Unknown DPU type.");
        }
    }*/

    /**
     * Runs a single extractor DPU module.
     *
     * @param extractor
     * @param ctx
     */
    /*
    private void runExtractor(Extract extractor, ExtractContext ctx) {

        try {
            long start = System.currentTimeMillis();

            extractor.extract(ctx);
            ctx.setDuration(System.currentTimeMillis() - start);
//            eventPublisher.publishEvent(
//            	new ExtractCompletedEvent(extractor, ctx, this)
//            );

        } catch (ExtractException ex) {
//            eventPublisher.publishEvent(
//            	new ExtractFailedEvent(ex, extractor, ctx, this)
//            );
            ex.fillInStackTrace();

        }
    }
    */
    /**
     * Runs a single Transformer DPU module.
     *
     * @param transformer
     * @param ctx
     */
    /*
    private void runTransformer(Transform transformer, TransformContext ctx) {

        try {
            long start = System.currentTimeMillis();

            transformer.transform(ctx);
            ctx.setDuration(System.currentTimeMillis() - start);
//            eventPublisher.publishEvent(
//            		new TransformCompletedEvent(transformer, ctx, this)
//            );

        } catch (TransformException ex) {
//            eventPublisher.publishEvent(
//            	new TransformFailedEvent(ex, transformer, ctx, this)
//            );
            ex.fillInStackTrace();
        }
    }*/

    /**
     * Runs a single Loader DPU module.
     *
     * @param loader
     * @param ctx
     */
    /*
    private void runLoader(Load loader, LoadContext ctx) {

        try {
            long start = System.currentTimeMillis();

            loader.load(ctx);
            ctx.setDuration(System.currentTimeMillis() - start);
//            eventPublisher.publishEvent(
//            	new LoadCompletedEvent(loader, ctx, this)
//            );
        } catch (LoadException ex) {
//            eventPublisher.publishEvent(
//            	new LoadFailedEvent(ex, loader, ctx, this)
//            );
            ex.fillInStackTrace();
        }
    }*/
}
