<script lang="ts">
    import type {
        MetricsUpdatePacket,
        MetricType,
    } from "../../../RobotWebSocket";
    import Collapsible from "../../Collapsible.svelte";
    import MetricPoint from "./MetricPoint.svelte";
    import MetricPose from "./MetricPose.svelte";
    import MetricValue from "./MetricValue.svelte";

    let { packet }: { packet: MetricsUpdatePacket<MetricType> } = $props();
</script>

<Collapsible title={packet.metricName}>
    {#if packet.metricValue === null}
        <p>null</p>
    {:else if packet.metricType === "pose"}
        <MetricPose packet={packet as MetricsUpdatePacket<"pose">} />
    {:else if packet.metricType === "point"}
        <MetricPoint packet={packet as MetricsUpdatePacket<"point">} />
    {:else if packet.metricType === "double" || packet.metricType === "string" || packet.metricType === "integer"}
        <MetricValue
            packet={packet as MetricsUpdatePacket<
                "double" | "string" | "integer"
            >}
        />
    {/if}
</Collapsible>
