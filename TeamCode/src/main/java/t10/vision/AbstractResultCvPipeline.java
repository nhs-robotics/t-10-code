package t10.vision;

import org.openftc.easyopencv.OpenCvPipeline;

/**
 * An {@link OpenCvPipeline} which has a result.
 *
 * @param <T> The type of the result from the pipeline.
 * @author youngermax
 */
public abstract class AbstractResultCvPipeline<T> extends OpenCvPipeline {
	/**
	 * The result of the webcam processor.
	 */
	protected T result;

	/**
	 * @return The result of the pipeline.
	 */
	public T getResult() {
		return result;
	}

	/**
	 * Runs when the pipeline is first added to the webcam.
	 */
	public abstract void init();
}