<script lang="ts">
    import { onMount } from "svelte";
    import {
        AmbientLight,
        BoxGeometry,
        CanvasTexture,
        CircleGeometry,
        ConeGeometry,
        DirectionalLight,
        GridHelper,
        Group,
        Mesh,
        MeshStandardMaterial,
        PerspectiveCamera,
        Scene,
        Sprite,
        SpriteMaterial,
        WebGLRenderer
    } from "three";
    import { OBJLoader, OrbitControls } from "three/examples/jsm/Addons.js";
    import type { MetricsUpdatePacket, Point, Pose, RobotWebSocket } from "../../../RobotWebSocket";
    import { markerObjects, type MarkerObject } from "./FieldView";

    const { rws }: { rws: RobotWebSocket } = $props();

    let fieldViewDomElement: HTMLCanvasElement;
    let renderer: WebGLRenderer;
    let scene: Scene;

    function createTextSprite(text: string): Sprite {
        const canvas = document.createElement('canvas');
        const context = canvas.getContext('2d')!;
        canvas.width = 256;
        canvas.height = 64;

        context.font = '32px Arial';
        context.fillStyle = 'white';
        context.textAlign = 'center';
        context.fillText(text, 128, 40);

        const texture = new CanvasTexture(canvas);
        const spriteMaterial = new SpriteMaterial({ map: texture });
        const sprite = new Sprite(spriteMaterial);
        sprite.scale.set(1, 0.25, 1);

        return sprite;
    }

    function createPoseMarker(): Group {
        const group = new Group();
        
        // Create arrow body (box)
        const bodyGeometry = new BoxGeometry(0.1, 0.05, 0.2);
        const bodyMaterial = new MeshStandardMaterial({ color: 0x00ff00 });
        const body = new Mesh(bodyGeometry, bodyMaterial);
        body.position.z = -0.1; // Center the body
        
        // Create arrow head (cone)
        const headGeometry = new ConeGeometry(0.08, 0.15, 8);
        const headMaterial = new MeshStandardMaterial({ color: 0x00ff00 });
        const head = new Mesh(headGeometry, headMaterial);
        head.rotation.x = -Math.PI / 2;
        head.position.z = 0.075; // Position at front
        
        group.add(body);
        group.add(head);
        
        return group;
    }
    
    function createPointMarker(): Group {
        const group = new Group();
        
        // Create arrow body (box)
        const bodyGeometry = new CircleGeometry(0.08);
        const bodyMaterial = new MeshStandardMaterial({ color: 0x00ff00 });
        const body = new Mesh(bodyGeometry, bodyMaterial);
        body.rotation.x = -Math.PI / 2;
        body.position.y = -0.075;

        group.add(body);
        
        return group;
    }

    function createOrUpdateMarker(metric: MetricsUpdatePacket<'point' | 'pose'>): MarkerObject {
        if (!markerObjects.has(metric.metricName)) {
            const label = createTextSprite(metric.metricName);
            let group: Group;

            switch (metric.metricType) {
                case 'point':
                    group = createPointMarker();
                    break;
            
                case 'pose':
                    group = createPoseMarker();
                    break;

                default:
                    throw new Error(`invalid metric type: ${metric.metricType}`);
            }

            scene.add(group);
            scene.add(label);
            
            markerObjects.set(metric.metricName, { group, label });
        }

        return markerObjects.get(metric.metricName)!;
    }

    onMount(() => {
        scene = new Scene();
        renderer = new WebGLRenderer({ antialias: true, canvas: fieldViewDomElement });
        
        const camera = new PerspectiveCamera(75, 1, 0.1, 1000);
        camera.position.set(0, 5, 5);
        camera.lookAt(0, 0, 0);
        
        const controls = new OrbitControls(camera, renderer.domElement);
        controls.enableDamping = true;
        controls.dampingFactor = 0.25;
        controls.enableZoom = true;
        
        // Lighting
        scene.add(new AmbientLight(0x404040, 0.5));
        
        const lights = [
            { pos: [1, 1, 1], intensity: 0.75 },
            { pos: [-1, 1, 1], intensity: 0.75 }
        ];
        
        lights.forEach(({ pos, intensity }) => {
            const light = new DirectionalLight(0xffffff, intensity);
            light.position.set(pos[0], pos[1], pos[2]).normalize();
            scene.add(light);
        });
        
        // Grid
        scene.add(new GridHelper(1.8288 * 4, 4));
        
        // Load field
        new OBJLoader().load(
            "/field.obj",
            (object) => {
                object.position.set(-1.8288, 0, 1.8288);
                scene.add(object);
            },
            (xhr) => console.log((xhr.loaded / xhr.total) * 100 + "% loaded"),
            (error) => console.error("Loading error:", error)
        );
        
        // Resize handling
        const resize = () => {
            const rect = fieldViewDomElement.getBoundingClientRect();
            renderer.setSize(rect.width, rect.height);
            camera.aspect = rect.width / rect.height;
            camera.updateProjectionMatrix();
        };
        
        window.addEventListener("resize", resize);
        resize();
        
        // Animation loop
        function animate() {
            requestAnimationFrame(animate);
            controls.update();
            renderer.render(scene, camera);
        }
        animate();
        
        // Handle pose updates
        rws.onPacket('MetricsUpdatePacket', packet => {
            if (packet.metricValue === null) {
                return;
            }
            
            switch (packet.metricType) {
                case 'pose':
                    const pose = packet.metricValue as Pose;
                    const poseMarker = createOrUpdateMarker(packet as MetricsUpdatePacket<'pose'>);
                    let poseX = pose.x / 3.281 / 12;
                    let poseY = pose.y / 3.281 / 12;
                    
                    // Update marker position and rotation
                    poseMarker.group.position.set(poseX , 0.1, poseY);
                    poseMarker.group.rotation.y = pose.heading;
                    
                    // Update label position
                    poseMarker.label.position.set(poseX, 0.4, poseY);
                    break;
            
                case 'point':
                    const point = packet.metricValue as Point;
                    const pointMarker = createOrUpdateMarker(packet as MetricsUpdatePacket<'point'>);
                    let pointX = point.x / 3.281 / 12;
                    let pointY = point.y / 3.281 / 12;
                    
                    // Update marker position and rotation
                    pointMarker.group.position.set(pointX, 0.1, pointY);
                    
                    // Update label position
                    pointMarker.label.position.set(pointX, 0.4, pointY);
                    break;

                default:
                    break;
            }
        });
        
        // Cleanup
        return () => {
            window.removeEventListener("resize", resize);
            
            markerObjects.forEach(({ group, label }) => {
                scene.remove(group);
                scene.remove(label);
                
                group.children.forEach(child => {
                    if (child instanceof Mesh) {
                        child.geometry.dispose();
                        (child.material as MeshStandardMaterial).dispose();
                    }
                });
                
                if (label.material.map) {
                    label.material.map.dispose();
                }
                label.material.dispose();
            });
            
            markerObjects.clear();
        };
    });
</script>

<canvas bind:this={fieldViewDomElement}></canvas>

<style>
    canvas {
        width: 100%;
        height: 100%;
    }
</style>