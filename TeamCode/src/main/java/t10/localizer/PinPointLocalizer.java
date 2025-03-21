/*   MIT License
 *   Copyright (c) [2024] [Base 10 Assets, LLC]
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:

 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.

 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   SOFTWARE.
 */

package t10.localizer;

import static com.qualcomm.robotcore.util.TypeConversion.byteArrayToInt;

import com.qualcomm.hardware.lynx.LynxNackException;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import t10.bootstrap.PinPointHardware;
import t10.geometry.Pose;
import t10.utils.OdometryUtils;


public class PinPointLocalizer implements Localizer<Pose> {


	private PinPointHardware pinPoint;
	private double initialAngle;


	/**
	 *
	 * The center of rotation of the robot is the point it rotates around when spinning.
	 * It can be found by finding the intersection of the two lines made by diagonal wheel pairs.
	 *
	 * @param pinPoint The PinPoint device
	 * @param xPodOffsetFromCenter The distance in the x-direction (right is positive) of the forward-backward pod from the robot's center of rotation
	 * @param xDirection The direction the y-pod (which measures delta in the y direction) is oriented
	 * @param yPodOffsetFromCenter The distance in the y-direction (forward is positive) of the right-left pod from the robot's center of rotation
	 * @param yDirection The direction the x-pod (which measures delta in the x direction) is oriented
	 * @param encoderResolution The number of ticks per mm of the encoders attached to the PinPoint
	 */
	public PinPointLocalizer(PinPointHardware pinPoint, double xPodOffsetFromCenter, PinPointHardware.EncoderDirection xDirection, double yPodOffsetFromCenter, PinPointHardware.EncoderDirection yDirection, double encoderResolution) {
		this.pinPoint = pinPoint;
		this.pinPoint.resetPosAndIMU();
		this.pinPoint.setEncoderResolution(encoderResolution);
		//For them, x-left is positive, so the inputted offset is negative. That's intentional. It's not causing your bug, I promise
		//Also, they want values in mm instead of inches. Thus, 25.4
		this.pinPoint.setOffsets(-xPodOffsetFromCenter * 25.4,yPodOffsetFromCenter * 25.4);
		//For them, the x-pod detects changes in y and the y-pod detects changes in x. This, too, is not your bug
		this.pinPoint.setEncoderDirections(xDirection,yDirection);
	}

	/**
	 *
	 * The center of rotation of the robot is the point it rotates around when spinning.
	 * It can be found by finding the intersection of the two lines made by diagonal wheel pairs.
	 *
	 * @param pinPoint The PinPoint device
	 * @param xPodOffsetFromCenter The distance in the x-direction (right is positive) of the forward-backward pod from the robot's center of rotation
	 * @param xDirection The direction the y-pod (which measures delta in the y direction) is oriented
	 * @param yPodOffsetFromCenter The distance in the y-direction (forward is positive) of the right-left pod from the robot's center of rotation
	 * @param yDirection The direction the x-pod (which measures delta in the x direction) is oriented
	 * @param pods The type of pods you are using
	 */
	public PinPointLocalizer(PinPointHardware pinPoint, double xPodOffsetFromCenter, PinPointHardware.EncoderDirection xDirection, double yPodOffsetFromCenter, PinPointHardware.EncoderDirection yDirection, PinPointHardware.GoBildaOdometryPods pods) {
		this.pinPoint = pinPoint;
		this.pinPoint.resetPosAndIMU();
		this.pinPoint.setEncoderResolution(pods);
		//For them, x-left is positive, so the inputted offset is negative. That's intentional. It's not causing your bug, I promise
		//Also, they want values in mm instead of inches. Thus, 25.4
		this.pinPoint.setOffsets(-xPodOffsetFromCenter * 25.4,yPodOffsetFromCenter * 25.4);
		this.pinPoint.setEncoderDirections(xDirection,yDirection);
	}

	@Override
	public void setFieldCentric(Pose pose) {
		pinPoint.setPosition(Pose.toPose2D(OdometryUtils.convertFromOurConventionsToFTCField(pose)));
	}

	@Override
	public Pose getFieldCentric() {
		//return new Pose(pinPoint.getPosition());
		return OdometryUtils.convertFromFTCFieldToOurConventions(new Pose(pinPoint.getPosition()));
	}

	public Pose getVelocity() {
		return OdometryUtils.convertFromFTCFieldToOurConventions(new Pose(pinPoint.getVelocity()));
	}

	public String status() {
		return pinPoint.getDeviceStatus().toString();
	}

	public double getFrequency() {
		return pinPoint.getFrequency();
	}

	@Override
	public void loop() {
		try {
			pinPoint.update();
		} catch (LynxNackException e) {
		}
	}

	@Override
	public boolean isDoneInitializing() {
		return pinPoint.getDeviceStatus() == PinPointHardware.DeviceStatus.READY;
	}
}
