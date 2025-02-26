package t10.localizer;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import t10.geometry.Pose;


public class LocalizerPriorityOrderer implements Localizer {

	public Localizer[] orderXY;
	public Localizer[] orderRotation;
	public Localizer[] allLocalizers;

	public LocalizerPriorityOrderer(
		Localizer[] localizers,
		int[] orderXY,
		int[] orderRotation
	) {
		this.orderXY = reorder(localizers,orderXY);
		this.orderRotation = reorder(localizers,orderRotation);
		this.allLocalizers = localizers;
	}

	private Localizer[] reorder(Localizer[] localizers, int[] order) {
		Localizer[] output = new Localizer[Math.min(localizers.length, order.length)];
		for(int i = 0; i < Math.min(localizers.length, order.length); i++) {
			output[Math.min(localizers.length, order.length) - i] = localizers[order[i]];
		}
		return output;
	}

	@Override
	public void setFieldCentric(Pose pose) {
		for(Localizer localizer : allLocalizers) {
			localizer.setFieldCentric(pose);
		}
	}

	@Override
	public Pose getFieldCentric() {
		Pose output = new Pose(0,0,0, AngleUnit.RADIANS);

		for(Localizer localizer : orderXY) {
			if(localizer.getFieldCentric() != null) {
				output.setX(localizer.getFieldCentric().getX());
				output.setY(localizer.getFieldCentric().getY());
			}
		}

		for(Localizer localizer : orderRotation) {
			if(localizer.getFieldCentric() != null) {
				output.setHeading(localizer.getFieldCentric().getHeading(AngleUnit.RADIANS),AngleUnit.RADIANS);
			}
		}

		return output;
	}

	@Override
	public void loop() {
		for(Localizer localizer : allLocalizers) {
			localizer.loop();
		}
	}
}
