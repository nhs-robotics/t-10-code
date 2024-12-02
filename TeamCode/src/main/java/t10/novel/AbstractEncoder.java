package t10.novel;

import org.openftc.easyopencv.OpenCvPipeline;

/**
 *
 */
public abstract class AbstractEncoder {

	public abstract int getCurrentTicks();

	public abstract double getCurrentInches();
}