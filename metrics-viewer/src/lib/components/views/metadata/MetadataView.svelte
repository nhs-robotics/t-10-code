<script lang="ts">
    import type {
        MetricsNewConnectionPacket,
        RobotWebSocket,
    } from "../../../RobotWebSocket";
    import Collapsible from "../../Collapsible.svelte";

    const { rws }: { rws: RobotWebSocket } = $props();
    let metadataPacket: MetricsNewConnectionPacket | undefined = $state();

    rws.onPacket("MetricsNewConnectionPacket", (packet) => {
        metadataPacket = packet;
    });
</script>

{#if metadataPacket}
    <p>
        <strong>{metadataPacket.opModeName}</strong>
        ({metadataPacket.opModeType})
    </p>
    
    {#each metadataPacket.hardware as hardware}
        <Collapsible title={hardware.name}>
            <p>type: {hardware.type}</p>
            <p>version: {hardware.version}</p>
            <p>info: {hardware.connectionDetails}</p>
        </Collapsible>
    {/each}
{:else}
    <p>(no metadata received)</p>
{/if}
