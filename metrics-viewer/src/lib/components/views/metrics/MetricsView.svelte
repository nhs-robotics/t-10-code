<script lang="ts">
    import type { MetricsUpdatePacket, MetricType, RobotWebSocket } from "../../../RobotWebSocket";
    import Metric from "./Metric.svelte";

    const { rws }: { rws: RobotWebSocket } = $props();
    let metricsPackets: Record<string, MetricsUpdatePacket<MetricType>> = $state({});

    rws.onPacket('MetricsUpdatePacket', packet => {
        metricsPackets[packet.metricName] = packet;
    });
</script>

<h1>Metrics</h1>

{#each Object.keys(metricsPackets) as metricName}
    <Metric packet={metricsPackets[metricName]} />
{:else}
    <p>(no metrics)</p>
{/each}
