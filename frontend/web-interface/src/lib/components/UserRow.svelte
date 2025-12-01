<script>
    import { createEventDispatcher } from 'svelte';

    export let user;
    export let roles = [];
    export let saving = false;

    const dispatch = createEventDispatcher();

    let selectedRole = user?.role ?? '';

    function handleSaveClick() {
        if (!user?.id || !selectedRole) return;
        dispatch('save', { id: user.id, role: selectedRole });
    }
</script>

<tr class="table-row">
    <td class="td td-id">
        {user.id}
    </td>

    <td class="td td-email">
        {user.email ?? user.username}
    </td>

    <td class="td td-role">
        <select
                class="input select-role"
                bind:value={selectedRole}
        >
            {#each roles as r}
                <option value={r}>{r}</option>
            {/each}
        </select>
    </td>

    <td class="td td-actions">
        <button class="btn primary" disabled={saving} on:click={handleSaveClick}>
            {saving ? 'Saving…' : 'Save role'}
        </button>
    </td>
</tr>

<style>
    .td-id {
        width: 70px;
        font-variant-numeric: tabular-nums;
    }
    .td-email {
        max-width: 260px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }
    .td-role {
        width: 160px;
    }
    .td-actions {
        width: 130px;
        text-align: right;
    }
    .select-role {
        width: 100%;
    }
</style>
