<script lang="ts">
    import type { MetricsUpdatePacket } from "../../../RobotWebSocket";
    import { getMarketVisible, setMarkerVisible } from "../field/FieldView";

    let { packet }: { packet: MetricsUpdatePacket<"point"> } = $props();
    let visible = $state(getMarketVisible(packet.metricName));

    function toggleVisibility() {
        visible = !visible;
        setMarkerVisible(packet.metricName, visible);
    }
</script>

<p>x: {packet.metricValue.x}</p>
<p>y: {packet.metricValue.y}</p>

<button onclick={toggleVisibility}>
    {#if visible}
        Hide in 3D
    {:else}
        Show in 3D
    {/if}
</button>
