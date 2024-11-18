package t10.ai;

import ai.onnxruntime.*;
import org.opencv.core.Mat;
import t10.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class DepthEstimator implements AutoCloseable {
    private OrtEnvironment environment;
    private OrtSession session;

    public void createOrtSession() throws OrtException, IOException {
        this.environment = OrtEnvironment.getEnvironment();
        InputStream resourceAsStream = DepthEstimator.class.getResourceAsStream("/depth_anything_v2_small_q4.onnx");
        ByteBuffer byteBuffer = IOUtils.readInputStream(resourceAsStream, 50000000);
        this.session = this.environment.createSession(byteBuffer);
        resourceAsStream.close();
    }

    private OnnxTensor convertImageToTensor(Mat image) throws OrtException {
        // Convert to float buffer
        int width = image.width();
        int height = image.height();
        FloatBuffer buffer = FloatBuffer.allocate(width * height * 3);

        for (int x = 0; width > x; x++) {
            for (int y = 0; height > y; y++) {
                double[] bgr = image.get(y, x);

                buffer.put(x + y * width + 0 * (width * height), (float) bgr[2]);
                buffer.put(x + y * width + 1 * (width * height), (float) bgr[1]);
                buffer.put(x + y * width + 2 * (width * height), (float) bgr[0]);
            }
        }

        // Create the tensor
        return OnnxTensor.createTensor(
                this.environment,
                buffer,
                new long[]{
                        1,
                        3,
                        height,
                        width,
                }
        );
    }

    private float[][] estimateDepth(OnnxTensorLike tensor) throws OrtException {
        // Prepare inputs
        Map<String, OnnxTensorLike> inputs = new HashMap<>();
        inputs.put("pixel_values", tensor);

        // Run
        OrtSession.Result result = session.run(inputs);

        // Return depth map output
        Optional<OnnxValue> predictedDepth = result.get("predicted_depth");
        float[][][] value = (float[][][]) predictedDepth.get().getValue();
        return value[0];
    }

    public float[][] run(Mat image) throws OrtException {
        OnnxTensor onnxTensor = this.convertImageToTensor(image);
        return this.estimateDepth(onnxTensor);
    }

    @Override
    public void close() throws Exception {
        this.session.close();
        this.environment.close();
    }
}
