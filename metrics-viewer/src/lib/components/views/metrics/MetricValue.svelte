<script lang="ts">
    import type { MetricsUpdatePacket } from "../../../RobotWebSocket";

    let { packet }: { packet: MetricsUpdatePacket<'double' | 'integer' | 'string'> } = $props();
</script>

<p>{packet.metricValue}</p>