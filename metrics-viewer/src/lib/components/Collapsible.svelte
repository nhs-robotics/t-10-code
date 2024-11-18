<script lang="ts">
    const { title, children }: { title: string, children: any } = $props();
    
    let isOpen = $state(false);
</script>

<div class="metric">
    <button class="metric-collapse" onclick={ () => isOpen = !isOpen }>
        {title}
    </button>

    {#if isOpen}
        <div class="metric-content">
            {@render children()}
        </div>
    {/if}
</div>

<style>
    .metric {
        background-color: #eeeeee;
    }

    .metric-content {
        padding: 1rem;
    }

    .metric-collapse {
        border-radius: 0;
        background: #cccccc;
        border: none;
        width: 100%;
        text-align: left;
        padding: 1rem;
        font-weight: bold;
        font-size: 13pt;
        cursor: pointer;
    }
</style>