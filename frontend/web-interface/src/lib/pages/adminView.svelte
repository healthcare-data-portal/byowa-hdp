<script>
    import Header from '../components/Header.svelte';
    import '/src/lib/styles/adminView.css';
    import { onMount } from 'svelte';
    import UserRow from '$lib/components/UserRow.svelte';

    const API = 'http://localhost:8080/api';
    const ROLES = ['PATIENT', 'DOCTOR', 'ADMIN'];

    let loading = true;
    let error = '';
    let users = [];
    let filter = '';
    let savingIds = new Set();

    function authHeaders() {
        const token = localStorage.getItem('token');
        return {
            'Content-Type': 'application/json',
            'Authorization': token ? `Bearer ${token}` : ''
        };
    }

    async function loadUsers() {
        loading = true; error = '';
        savingIds = new Set();
        try {
            const res = await fetch(`${API}/admin/users`, { headers: authHeaders() });
            if (!res.ok) throw new Error(await res.text() || `Failed to load users (${res.status})`);
            users = await res.json();
        } catch (e) {
            error = e.message || 'Failed to load users';
        } finally {
            loading = false;
        }
    }

    async function handleSave(e) {
        const { id, role } = e.detail;
        savingIds.add(id); savingIds = new Set(savingIds);
        try {
            const res = await fetch(`${API}/admin/users/${id}/role`, {
                method: 'PUT',
                headers: authHeaders(),
                body: JSON.stringify({ role })
            });
            if (!res.ok) throw new Error(await res.text() || `Failed to update role (${res.status})`);
            await loadUsers();
        } catch (err) {
            alert(err.message || 'Update failed');
        } finally {
            savingIds.delete(id); savingIds = new Set(savingIds);
        }
    }

    $: filtered = users.filter(u => !filter || u.username.toLowerCase().includes(filter.toLowerCase()));

    onMount(loadUsers);
</script>

<Header role="admin" roleIcon="/src/lib/assets/pictures/white_shield.png" />

<div class="page-wrap">
    <div class="h1">Admin Dashboard</div>
    <div class="sub">Manage users, roles, and system settings</div>

    <div class="section">
        <div class="title">User Management</div>
        <div class="desc">Change roles and manage access</div>

        <div class="toolbar">
            <input class="input" placeholder="Search users..." bind:value={filter} />
            <button class="btn primary" on:click={loadUsers}>Reload</button>
        </div>

        {#if error}
            <div class="empty" style="color:#b91c1c">⚠ {error}</div>
        {/if}

        {#if loading}
            <div class="empty">Loading users…</div>
        {:else if filtered.length === 0}
            <div class="empty">No users to display.</div>
        {:else}
            <div class="table-wrap">
                <table class="table">
                    <thead>
                    <tr>
                        <th class="th">ID</th>
                        <th class="th">Email</th>
                        <th class="th">Role</th>
                        <th class="th">Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {#each filtered as u (u.id)}
                        <UserRow
                                user={u}
                                roles={ROLES}
                                saving={savingIds.has(u.id)}
                                on:save={handleSave}
                        />
                    {/each}
                    </tbody>
                </table>
            </div>
        {/if}
    </div>
</div>
