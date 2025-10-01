<script>
  export let items = [];
  export let activeTab;
  const select = (id) => { activeTab = id; };
</script>
<div role="tablist" aria-label="Tabs" style="display:flex; gap:10px; flex-wrap: wrap;">
  {#each items as item}
    <button
      class="tab"
      role="tab"
      aria-selected={item.id === activeTab}
      aria-controls={"panel-" + item.id}
      on:click={() => select(item.id)}
    >
      {item.label}
    </button>
  {/each}
</div>
