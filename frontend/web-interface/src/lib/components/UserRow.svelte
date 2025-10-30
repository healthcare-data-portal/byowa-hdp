<script>
    import { createEventDispatcher } from 'svelte';
    export let user;
    export let roles = [];
    export let saving = false;

    let selected = user.role;
    const dispatch = createEventDispatcher();

    $: dirty = selected !== user.role;

    function save() {
        if (!dirty || saving) return;
        dispatch('save', { id: user.id, role: selected });
    }

    function reset() {
        selected = user.role;
    }
</script>

<tr class="tr">
    <td>{user.id}</td>
    <td>{user.username}</td>
    <td>
        <select class="input" bind:value={selected}>
            {#each roles as r}
                <option value={r}>{r}</option>
            {/each}
        </select>
    </td>
    <td style="display:flex;gap:.5rem;align-items:center;">
        <button class="btn primary" disabled={!dirty || saving} on:click={save}>
            {saving ? 'Saving…' : 'Save role'}
        </button>
        {#if dirty}
            <button class="btn ghost" on:click={reset}>Reset</button>
        {/if}
    </td>
</tr>
