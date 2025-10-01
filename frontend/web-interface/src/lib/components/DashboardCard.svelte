<script>
  export let title = '';
  export let value = undefined; // pass real data later
  export let description = '';
  export let format = 'number'; // 'number' | 'text'
  $: display = value === undefined || value === null ? '—' : String(value);
</script>
<div style="background:var(--card); border-radius: var(--radius); padding: 18px; box-shadow: var(--shadow);" role="listitem">
  <div style="color:#334155; font-size:14px; font-weight:600; margin-bottom: 8px;">{title}</div>
  <div style="font-size: 32px; font-weight: 800; line-height: 1;">{display}</div>
  {#if description}<div class="muted" style="margin-top: 4px;">{description}</div>{/if}
</div>
