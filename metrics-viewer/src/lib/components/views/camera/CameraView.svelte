<script lang="ts">
    import { onMount } from "svelte";
    import type { RobotWebSocket } from "../../../RobotWebSocket";

    let canvas: HTMLCanvasElement;
    const { rws }: { rws: RobotWebSocket } = $props();

    onMount(() => {
        const ctx = canvas.getContext("2d")!;
        let img = new Image();

        // Handle image load to ensure proper dimensions are available
        img.onload = () => {
            // Clear the canvas with black background
            ctx.fillStyle = 'black';
            ctx.fillRect(0, 0, canvas.width, canvas.height);

            // Calculate aspect ratios
            const canvasRatio = canvas.width / canvas.height;
            const imgRatio = img.width / img.height;

            let drawWidth, drawHeight, x, y;

            if (imgRatio > canvasRatio) {
                // Image is wider relative to canvas
                drawWidth = canvas.width;
                drawHeight = canvas.width / imgRatio;
                x = 0;
                y = (canvas.height - drawHeight) / 2;
            } else {
                // Image is taller relative to canvas
                drawHeight = canvas.height;
                drawWidth = canvas.height * imgRatio;
                x = (canvas.width - drawWidth) / 2;
                y = 0;
            }

            // Draw the image centered and scaled
            ctx.drawImage(img, x, y, drawWidth, drawHeight);
        };

        // Set up canvas size
        const resizeCanvas = () => {
            canvas.width = canvas.clientWidth;
            canvas.height = canvas.clientHeight;
        };
        
        // Initial resize and listen for window changes
        resizeCanvas();
        window.addEventListener('resize', resizeCanvas);

        rws.onPacket("MetricsCameraFramePacket", (packet) => {
            img.src = "data:image/jpeg;base64," + packet.jpegBase64;
        });

        // Cleanup
        return () => {
            window.removeEventListener('resize', resizeCanvas);
        };
    });
</script>

<canvas bind:this={canvas}></canvas>

<style>
    canvas {
        width: 100%;
        height: 100%;
    }
</style>