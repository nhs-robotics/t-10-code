package t10.motion;

import org.openftc.easyopencv.OpenCvPipeline;

/**
 *
 */
public abstract class AbstractEncoder {

	public abstract int getCurrentTicks();

	public abstract double getCurrentInches();
}