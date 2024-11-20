<script lang="ts">
    import { onMount } from "svelte";
    import { RobotWebSocket } from "./lib/RobotWebSocket";
    import Field3dView from "./lib/components/views/field/Field3dView.svelte";
    import MetadataView from "./lib/components/views/metadata/MetadataView.svelte";
    import MetricsView from "./lib/components/views/metrics/MetricsView.svelte";
    import CameraView from "./lib/components/views/camera/CameraView.svelte";

    let rws: RobotWebSocket | undefined = $state(undefined);

    onMount(() => {
        rws = new RobotWebSocket();
    });
</script>

<div class="grid">
    <div>
        {#if rws}
            <Field3dView {rws} />
        {/if}
    </div>
    
    <div class="grid-vertical">
        <div>
            {#if rws}
                <MetricsView {rws} />
            {/if}
        </div>

        <div class="scrollable">
            {#if rws}
                <MetadataView {rws} />
            {/if}
        </div>

        <div>
            {#if rws}
                <CameraView {rws} />
            {/if}
        </div>
    </div>
</div>

<style>
    .scrollable {
        overflow: auto;
        max-height: 100%;
    }
    .grid {
        width: 100vw;
        height: 100vh;
        display: grid;
        grid-template-columns: 1fr 1fr;
    }

    .grid-vertical {
        display: grid;
        max-height: 100vh;
        grid-template-rows: 1fr 1fr 1fr;
    }

    .grid div {
        width: 100%;
        height: 100%;
    }
</style>
